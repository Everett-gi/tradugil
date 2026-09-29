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
| `../api/Dockerfile` | A API: compila numa etapa, roda noutra |

## Antes de começar

Três coisas, todas do [`PASSO-A-PASSO.md`](../PASSO-A-PASSO.md):

- a máquina na Oracle Cloud, criada e acessível por SSH;
- o nome registrado no DuckDNS, com o token guardado;
- a string de conexão do Neon.

## 1. A máquina

Ao criar, escolha **`VM.Standard.A1.Flex`**. É o formato ARM do Always Free,
e o único com memória suficiente para a JVM. O `VM.Standard.E2.1.Micro`
também é gratuito e é pequeno demais. Qualquer outro é pago.

Imagem: **Ubuntu 24.04**. Quatro OCPUs e 24 GB cabem no gratuito.

### As portas, que são duas configurações e não uma

Este é o erro que faz a pessoa passar uma tarde achando que o servidor está
quebrado. A Oracle filtra em dois lugares independentes, e abrir só um não
abre nada:

**Na nuvem**, em *Networking > Virtual Cloud Networks > a sua VCN > Security
Lists*, acrescente regras de entrada para TCP 80 e 443 vindas de `0.0.0.0/0`.

**Dentro da máquina.** A imagem Ubuntu da Oracle vem com regras de iptables
que descartam quase tudo, e elas continuam valendo mesmo com a nuvem
liberada:

```bash
sudo iptables -I INPUT 6 -m state --state NEW -p tcp --dport 80 -j ACCEPT
sudo iptables -I INPUT 6 -m state --state NEW -p tcp --dport 443 -j ACCEPT
sudo netfilter-persistent save
```

Sem a segunda parte, o desafio HTTP-01 da Let's Encrypt não chega, e o Caddy
fica repetindo que não conseguiu o certificado, sem dizer por quê.

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

```bash
cd /opt/tradugil/repo/infra
sudo docker compose up -d --build
```

A primeira vez demora: compila a API com Maven, compila um Caddy com o módulo
de limite e constrói o site. As seguintes reaproveitam as camadas.

```bash
sudo docker compose ps
sudo docker compose logs -f borda
```

O log da borda mostra o certificado sendo obtido. Depois disso:

```bash
curl -I https://tradugil.duckdns.org
curl -s https://tradugil.duckdns.org/api/v1/categorias | head -c 200
```

## 6. O APK para download

Ele não entra na imagem, porque é assinado com a chave que não está no
repositório e imagem é copiável. Entra por volume:

```bash
sudo mkdir -p /opt/tradugil/apk
# da sua máquina:
scp android/app/build/outputs/apk/release/app-release.apk \
    ubuntu@SEU-IP:/tmp/tradugil.apk
# no servidor:
sudo mv /tmp/tradugil.apk /opt/tradugil/apk/tradugil.apk
```

Fica em `https://tradugil.duckdns.org/baixar/tradugil.apk`, com o tipo MIME
que o Android exige para aceitar instalar.

Publique o SHA-256 ao lado do link. Fora da Play Store é a única forma de
alguém conferir que baixou o que você publicou:

```bash
sha256sum /opt/tradugil/apk/tradugil.apk
```

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

```bash
cd /opt/tradugil/repo && git pull
cd infra && sudo docker compose up -d --build
```

As migrações do Flyway rodam sozinhas na subida da API. O `healthcheck` tem
90 segundos de carência justamente para isso: marcar a API como doente no
meio de uma migração faria o restart matar o Flyway pela metade.

## Quando trocar o DuckDNS por domínio próprio

Mudam juntos, e eu faço: o nome no `Caddyfile`, o `tradugil.cors.origens` do
`application.yml`, o `URL_DA_API` da build de release do Android e o
manifesto da extensão. Volte também o `max-age` do HSTS para `31536000` e
estenda o `Expires` do `security.txt`: os dois estão curtos de propósito
enquanto o nome é emprestado, pelo motivo que está em
[`docs/SEGURANCA.md`](../docs/SEGURANCA.md).

O único ponto que não é instantâneo é a extensão: a lista de endereços
permitidos vive no manifesto, e isso exige publicar versão nova na loja.
