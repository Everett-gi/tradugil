# Subir o Tradugil num servidor

O que está aqui é o suficiente para sair da máquina de desenvolvimento e ir
para a internet. São dois contêineres: a API e a borda (Caddy, que faz TLS,
proxy reverso, limite de requisição e serve o site e o APK).

O banco **não** está aqui. Ele é o Neon, gerenciado, com backup e ponto no
tempo que ninguém precisa manter. Subir um Postgres neste servidor
significaria ser responsável por backup, atualização e recuperação numa
máquina de plano gratuito, que é onde projeto pequeno perde dados.

| Arquivo | O que é |
|---------|---------|
| `Caddyfile` | Configuração da borda: TLS, cabeçalhos, limites, rotas |
| `Dockerfile.caddy` | Caddy compilado com o módulo de limite, e o site dentro |
| `docker-compose.yml` | Os dois serviços, com o que cada um pode e não pode |
| `duckdns-atualiza.sh` | Mantém `tradugil.duckdns.org` apontando para a máquina |
| `duckdns.service`, `duckdns.timer` | Rodam o script de cinco em cinco minutos |
| `publicar-apk.sh` | Publica o APK assinado numa Release do GitHub |
| `../api/Dockerfile` | A API: compila numa etapa, roda noutra |

**As imagens não são construídas no servidor.** A CI constrói e publica no
GHCR a cada push na `main`, e a máquina só baixa. Isso deixou de ser
preferência quando o servidor virou uma `e2-micro`: com 0,25 de vCPU e 1 GB
de RAM, compilar a API com Maven ali levaria dezenas de minutos e
provavelmente morreria sem memória no meio de um deploy.

## Antes de começar

Três coisas, todas do [`PASSO-A-PASSO.md`](../PASSO-A-PASSO.md):

- a máquina no Google Cloud, criada e acessível por SSH;
- o nome registrado no DuckDNS, com o token guardado;
- a string de conexão do Neon.

## 1. A máquina

O gratuito para sempre do Google é uma `e2-micro`, e ele é exigente: **três
escolhas têm que estar certas ou a conta começa a ser cobrada**, e o console
sugere o valor errado em uma delas.

| Campo | Valor | Por quê |
|-------|-------|---------|
| Região | `us-west1`, `us-central1` ou `us-east1` | São as três únicas. Qualquer outra é paga, inclusive `southamerica-east1` |
| Tipo de máquina | `e2-micro` | `e2-small` é o dobro e é pago |
| Disco de inicialização | **Standard persistent disk**, 30 GB | O console sugere *Balanced*, que é pago. Tem que trocar na mão |
| Imagem | Ubuntu 24.04 LTS | |
| Firewall | Marque *Allow HTTP traffic* e *Allow HTTPS traffic* | Cria as regras de entrada para 80 e 443 |

Não existe região no Brasil no gratuito. A latência dos Estados Unidos fica
em torno de 150 ms, o que dá para conviver: o nível 0 da cascata é o
dicionário no próprio aparelho, e o site é um PWA que fica em cache depois da
primeira visita.

### Ponha um alerta de orçamento antes de qualquer outra coisa

Em *Billing > Budgets & alerts*, crie um orçamento de **R$ 1** com alerta em
50%. Leva um minuto e é o que transforma "achei que era gratuito" em um
e-mail no mesmo dia.

Vale especialmente por um item que eu não consegui confirmar na documentação
oficial: o Google cobra por endereço IPv4 externo desde fevereiro de 2024, e
várias fontes dizem que o nível gratuito é isento, mas a página oficial do
nível gratuito não menciona o assunto. O alerta responde isso em dois dias,
com a sua conta, melhor do que qualquer página.

### Memória: 1 GB, e a JVM não gosta disso

Crie uma área de troca antes de subir qualquer coisa. Sem ela, um pico
durante uma migração do Flyway faz o kernel matar a JVM, e o sintoma é um
contêiner que reinicia sozinho sem nada no log da aplicação:

```bash
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile && sudo swapon /swapfile
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

O `docker-compose.yml` já limita a API a 768 MB, o que deixa folga para o
Caddy e para o sistema. Área de troca não substitui memória, mas transforma
"morreu" em "ficou lento por alguns segundos", que é uma falha muito melhor.

### O tráfego de saída é 1 GB por mês

É o teto que mais aperta, e por isso o APK saiu do servidor: um arquivo de
15 MB gastaria a cota em uns 60 downloads. Ele vive nas Releases do GitHub, e
o `/baixar/tradugil.apk` do site redireciona para lá.

O que sobra é o site, que tem uns 90 KB comprimidos e vira cache de service
worker na primeira visita. Dá para uns milhares de acessos por mês.

## 2. Docker

```bash
sudo apt-get update && sudo apt-get install -y ca-certificates curl git
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg \
  -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" \
  | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
```

Do repositório da Docker, e não o `docker.io` do Ubuntu: o pacote da
distribuição costuma estar velho e não traz o `docker compose` v2, que é o
que o `docker-compose.yml` daqui usa.

## 3. O DuckDNS apontando para cá

```bash
sudo mkdir -p /etc/tradugil
sudo install -m 750 infra/duckdns-atualiza.sh /usr/local/bin/
sudo install -m 644 infra/duckdns.service infra/duckdns.timer /etc/systemd/system/
```

O arquivo com o token, que é a senha do serviço:

```bash
sudo install -m 600 /dev/null /etc/tradugil/duckdns.env
sudo nano /etc/tradugil/duckdns.env
```

```
DUCKDNS_DOMINIO=tradugil
DUCKDNS_TOKEN=o-token-da-pagina-do-duckdns
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable --now duckdns.timer
sudo systemctl start duckdns.service && sudo systemctl status duckdns.service
```

Confirme antes de seguir. Se o nome não apontar para esta máquina, o
certificado não sai:

```bash
dig +short tradugil.duckdns.org
curl -s ifconfig.me
```

Os dois têm que mostrar o mesmo endereço.

## 4. Os segredos

```bash
git clone https://github.com/Everett-gi/tradugil.git /opt/tradugil/repo
sudo install -m 600 /dev/null /etc/tradugil/api.env
sudo nano /etc/tradugil/api.env
```

O conteúdo é o mesmo do [`.env.exemplo`](../.env.exemplo), que explica cada
linha: o ajuste que a string do Neon precisa para virar JDBC, como gerar o
segredo do JWT e a pimenta, e por que a pimenta nunca pode mudar depois de
existirem contas.

Modo 600 e dono `root` não é zelo: o arquivo tem a senha do banco e a
pimenta das senhas. Ele fica fora do repositório e fora da imagem de
propósito, porque imagem é copiável.

## 5. Subir

As imagens já estão prontas no GHCR, publicadas pela CI. A máquina só baixa:

```bash
cd /opt/tradugil/repo/infra
sudo docker compose pull
sudo docker compose up -d
```

`pull` antes de `up` de propósito. O `docker-compose.yml` também tem `build:`,
para funcionar numa máquina de desenvolvimento sem depender do GHCR, e sem o
`pull` explícito o compose poderia decidir construir aqui, que é justamente o
que esta máquina não aguenta.

```bash
sudo docker compose ps
sudo docker compose logs -f borda
```

O log da borda mostra o certificado sendo obtido. Depois disso:

```bash
curl -I https://tradugil.duckdns.org
curl -s https://tradugil.duckdns.org/api/v1/categorias | head -c 200
```

### Se o pull for recusado

As imagens do GHCR nascem privadas. Abra
`https://github.com/Everett-gi?tab=packages`, entre em `tradugil-api` e em
`tradugil-borda`, e em *Package settings* mude a visibilidade para **Public**.

Imagem pública aqui não expõe nada: ela tem a aplicação compilada e o site,
que já são código aberto, e nenhum segredo. Os segredos vivem no
`/etc/tradugil/api.env`, na máquina, fora de qualquer imagem.

A alternativa seria guardar um token do GitHub no servidor só para baixar
imagem, o que é mais uma credencial de longa vida para cuidar em troca de
esconder algo que já está publicado.

## 6. O APK para download

Ele **não** fica no servidor. Vai para as Releases do GitHub, por duas
razões: a `e2-micro` tem 1 GB de tráfego de saída por mês e um APK de 15 MB
gastaria isso em uns 60 downloads; e ele é assinado com a chave que não está
no repositório, então servi-lo daqui dependia de alguém lembrar de copiar a
cada versão.

Da sua máquina, a que tem a chave:

```bash
cd android && ./gradlew assembleRelease && cd ..
./infra/publicar-apk.sh 0.1.0
```

O script confere que o APK está assinado antes de publicar (sem o
`keystore.properties`, o Gradle produz um APK sem assinatura e sem reclamar,
que ninguém consegue instalar), calcula o SHA-256 e o escreve no corpo da
Release.

O endereço que as pessoas usam não muda:
`https://tradugil.duckdns.org/baixar/tradugil.apk` redireciona para a última
Release.

**A assinatura continua acontecendo só na sua máquina.** Pôr o
`tradugil.jks` num segredo do GitHub faria a única coisa irreversível do
projeto existir em mais um lugar, sob a política de acesso de outra empresa.

## 7. O primeiro administrador

Cadastre-se normalmente pelo site. **Depois** ponha o e-mail em
`TRADUGIL_ADMIN_INICIAL` no `/etc/tradugil/api.env` e reinicie a API:

```bash
sudo docker compose up -d api
```

A ordem importa: o promotor nunca cria conta, só promove uma que já existe.
Um bootstrap que criasse usuário precisaria de uma senha vinda de
configuração, e senha em variável de ambiente acaba em log de deploy e em
histórico de shell.

## Atualizar depois

Esperando a CI ficar verde no commit que você quer, são dois comandos:

```bash
cd /opt/tradugil/repo && git pull
cd infra && sudo docker compose pull && sudo docker compose up -d
```

O `git pull` é só pelo `docker-compose.yml` e pelo `Caddyfile`; o código já
vem dentro das imagens.

As migrações do Flyway rodam sozinhas na subida da API. O `healthcheck` tem
90 segundos de carência justamente para isso: marcar a API como doente no
meio de uma migração faria o restart matar o Flyway pela metade.

### Voltar atrás

Cada imagem é publicada com duas etiquetas: `:main`, que é móvel, e a do
commit, que não é. Para voltar a uma versão específica sem adivinhar qual
estava no ar:

```bash
sudo docker compose pull
# ou, para fixar um commit:
TRADUGIL_VERSAO=<sha do commit> sudo docker compose up -d
```

Isso exige trocar a etiqueta no `docker-compose.yml`. Para um projeto deste
tamanho, o caminho honesto é editar a linha `image:` e subir de novo.

Atenção a uma coisa que voltar atrás **não** desfaz: migração do Flyway é só
para frente. Se a versão nova aplicou uma migração, subir a imagem antiga
pode falhar no `ddl-auto: validate`, que é o comportamento correto (recusar
subir em vez de descobrir a divergência no primeiro select).

## Quando trocar o DuckDNS por domínio próprio

Mudam juntos, e eu faço: o nome no `Caddyfile`, o `tradugil.cors.origens` do
`application.yml`, o `URL_DA_API` da build de release do Android e o
manifesto da extensão. Volte também o `max-age` do HSTS para `31536000` e
estenda o `Expires` do `security.txt`: os dois estão curtos de propósito
enquanto o nome é emprestado, pelo motivo que está em
[`docs/SEGURANCA.md`](../docs/SEGURANCA.md).

O único ponto que não é instantâneo é a extensão: a lista de endereços
permitidos vive no manifesto, e isso exige publicar versão nova na loja.
