#!/bin/sh
# Avisa o DuckDNS de qual e o IP desta maquina.
#
# POR QUE ISTO EXISTE
#
# DuckDNS e DNS dinamico: o nome so aponta para o servidor enquanto alguem
# continuar dizendo qual e o IP. A Oracle Cloud da um IP efemero por padrao, e
# ele muda quando a maquina e recriada ou parada por tempo demais. Sem este
# script, o site simplesmente para de responder um dia, sem erro nenhum em
# lugar nenhum: o DNS aponta para um endereco que nao e mais nosso.
#
# Vale rodar mesmo com IP reservado. O custo e uma requisicao a cada cinco
# minutos, e o beneficio e nao depender de ninguem lembrar de atualizar na mao
# no dia em que o IP mudar.
#
# COMO USAR
#
#   sudo install -m 750 duckdns-atualiza.sh /usr/local/bin/
#   sudo install -m 600 /dev/null /etc/tradugil/duckdns.env
#   sudo tee /etc/tradugil/duckdns.env >/dev/null <<'FIM'
#   DUCKDNS_DOMINIO=tradugil
#   DUCKDNS_TOKEN=cole-o-token-aqui
#   FIM
#
# O token e a senha do servico: quem o tem aponta o nome para onde quiser.
# Por isso o arquivo e 600 e fica fora do repositorio, como o .env da API.
#
# Depois, a unidade e o timer em duckdns.service e duckdns.timer.

set -eu

ARQUIVO_DE_AMBIENTE="${DUCKDNS_ENV:-/etc/tradugil/duckdns.env}"

if [ -r "$ARQUIVO_DE_AMBIENTE" ]; then
	# shellcheck disable=SC1090
	. "$ARQUIVO_DE_AMBIENTE"
fi

if [ -z "${DUCKDNS_DOMINIO:-}" ] || [ -z "${DUCKDNS_TOKEN:-}" ]; then
	echo "duckdns: falta DUCKDNS_DOMINIO ou DUCKDNS_TOKEN em $ARQUIVO_DE_AMBIENTE" >&2
	exit 1
fi

# O ip fica vazio de proposito: assim o DuckDNS usa o endereco de origem da
# propria requisicao. Descobrir o IP por conta propria daria errado atras de
# NAT, que e exatamente o caso de uma maquina na nuvem.
#
# --fail para um HTTP de erro virar codigo de saida diferente de zero, senao o
# curl devolve sucesso e o systemd acha que deu certo. E o token vai no corpo,
# com --data-urlencode, para nao aparecer na linha de comando: argumento de
# processo e visivel para qualquer usuario da maquina pelo /proc.
RESPOSTA=$(
	curl --silent --show-error --fail --max-time 20 \
		--get "https://www.duckdns.org/update" \
		--data-urlencode "domains=$DUCKDNS_DOMINIO" \
		--data-urlencode "token=$DUCKDNS_TOKEN" \
		--data-urlencode "ip=" \
		--data-urlencode "verbose=true"
)

# O DuckDNS responde 200 mesmo quando recusa: o resultado vem no corpo, como
# "OK" ou "KO". Tratar so o codigo HTTP deixaria um token errado passar por
# sucesso, e o nome apontaria para o IP velho ate alguem reparar.
case "$RESPOSTA" in
OK*)
	# Sem ecoar o IP: vai para o journal, e o log da maquina nao precisa
	# guardar o endereco de ninguem.
	echo "duckdns: atualizado"
	;;
*)
	echo "duckdns: o servico recusou a atualizacao (verifique o token)" >&2
	exit 1
	;;
esac
