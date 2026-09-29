#!/usr/bin/env bash
# Publica o APK assinado numa Release do GitHub.
#
# POR QUE NAO E UM JOB DA CI
#
# Porque a CI teria que assinar, e assinar exige a chave. Por o tradugil.jks
# num segredo do GitHub faria a unica coisa irreversivel do projeto existir em
# mais um lugar, sob a politica de acesso de outra empresa. O Android recusa
# atualizar um aplicativo assinado com outra chave: perder ou vazar essa
# significa que quem ja instalou teria que desinstalar.
#
# Entao a assinatura continua acontecendo aqui, na maquina que tem a chave, e
# so o resultado sobe. A CI constroi um APK de release NAO assinado, que prova
# que o projeto compila, e e um artefato diferente deste.
#
# POR QUE O APK NAO FICA NO SERVIDOR
#
# A e2-micro do Google da 1 GB de trafego de saida por mes. Um APK de 15 MB
# gasta isso em uns 60 downloads, e o excedente e cobrado. O GitHub serve
# Release sem teto e de graca, e o /baixar/tradugil.apk do site redireciona
# para la, entao o endereco que as pessoas usam nao muda.
#
# COMO USAR
#
#   ./infra/publicar-apk.sh 0.1.0
#
# O token sai do credential manager do git, o mesmo que o `git push` usa. Ele
# precisa de permissao de escrita no repositorio (o escopo `repo`, ou
# `contents: write` num fine-grained).

set -euo pipefail

REPOSITORIO="Everett-gi/tradugil"
APK="android/app/build/outputs/apk/release/app-release.apk"

VERSAO="${1:-}"
if [ -z "$VERSAO" ]; then
	echo "uso: $0 <versao>   (exemplo: $0 0.1.0)" >&2
	exit 1
fi
TAG="v$VERSAO"

if [ ! -f "$APK" ]; then
	echo "APK nao encontrado em $APK" >&2
	echo "Gere com:  cd android && ./gradlew assembleRelease" >&2
	exit 1
fi

# CONFERE QUE ESTA ASSINADO ANTES DE PUBLICAR.
#
# Um APK sem assinatura sai do Gradle sem erro nenhum quando o
# keystore.properties nao existe: e o comportamento certo para a CI, e seria
# um desastre aqui. Ninguem consegue instalar, e a proxima versao assinada
# nao atualizaria quem tivesse instalado esta.
if command -v apksigner >/dev/null 2>&1; then
	if ! apksigner verify "$APK" >/dev/null 2>&1; then
		echo "O APK nao esta assinado. Verifique o android/keystore.properties." >&2
		exit 1
	fi
	echo "assinatura conferida"
else
	echo "aviso: apksigner nao encontrado, nao deu para conferir a assinatura" >&2
fi

SOMA=$(sha256sum "$APK" | cut -d' ' -f1)
TAMANHO=$(du -h "$APK" | cut -f1)
echo "versao $VERSAO, $TAMANHO, sha256 $SOMA"

TOKEN=$(printf 'protocol=https\nhost=github.com\n\n' | git credential fill | sed -n 's/^password=//p')
if [ -z "$TOKEN" ]; then
	echo "nao consegui obter o token do git credential" >&2
	exit 1
fi

# O corpo da Release traz o SHA-256. Fora da Play Store, e a unica forma de
# alguem conferir que baixou o que foi publicado: o aviso de "fontes
# desconhecidas" do Android diz que o arquivo nao foi verificado por ninguem,
# e a soma e o que permite verificar por conta propria.
#
# Montado ja com \n literal, em vez de texto de varias linhas convertido
# depois. Sem jq na maquina (o Git Bash do Windows nao traz), escapar JSON com
# sed e awk e o tipo de codigo que funciona ate o dia em que alguem poe uma
# aspa no texto. Aqui todo o conteudo e conhecido: versao e uma soma hexa.
CORPO="Aplicativo Android do Tradugil, versao ${VERSAO}.\n\n"
CORPO="${CORPO}O Android vai avisar sobre fontes desconhecidas, porque o APK "
CORPO="${CORPO}nao vem da Play Store. Para conferir que o arquivo e este "
CORPO="${CORPO}mesmo, antes de instalar:\n\n    sha256sum tradugil.apk\n\n"
CORPO="${CORPO}Tem que dar exatamente:\n\n    ${SOMA}\n"

CORPO_DA_REQUISICAO=$(printf \
	'{"tag_name":"%s","name":"Tradugil %s","body":"%s"}' \
	"$TAG" "$VERSAO" "$CORPO")

echo "criando a release $TAG..."
RESPOSTA=$(curl -sS -X POST \
	-H "Authorization: Bearer $TOKEN" \
	-H "Accept: application/vnd.github+json" \
	"https://api.github.com/repos/$REPOSITORIO/releases" \
	-d "$CORPO_DA_REQUISICAO")

# O id da release e o primeiro campo "id" da resposta. Seis digitos ou mais
# para nao casar com id curto de outro objeto aninhado.
ID=$(printf '%s' "$RESPOSTA" | tr ',' '\n' | sed -n 's/.*"id": *\([0-9]\{6,\}\).*/\1/p' | head -1)
if [ -z "$ID" ]; then
	echo "nao consegui criar a release:" >&2
	printf '%s\n' "$RESPOSTA" | head -20 >&2
	exit 1
fi

# O nome do anexo e tradugil.apk, e nao app-release.apk, porque e ele que
# aparece na URL do /releases/latest/download/ para a qual o site redireciona.
echo "enviando o arquivo..."
curl -sS -X POST \
	-H "Authorization: Bearer $TOKEN" \
	-H "Content-Type: application/vnd.android.package-archive" \
	--data-binary "@$APK" \
	"https://uploads.github.com/repos/$REPOSITORIO/releases/$ID/assets?name=tradugil.apk" \
	-o /dev/null

echo "pronto: https://github.com/$REPOSITORIO/releases/tag/$TAG"
echo "o site serve em /baixar/tradugil.apk, que redireciona para a ultima release"
