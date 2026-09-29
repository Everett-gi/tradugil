# O que falta fazer por fora

Este arquivo é a lista do que **só você pode fazer**: coisas que pedem uma
conta, um cartão ou uma decisão sua. O que é código já está no repositório.

Estado em 29 de setembro de 2026.

## Já está pronto

Nada a fazer nestes:

| Item | Situação |
|------|----------|
| Banco de dados (Neon) | Conectado, 35 migrações aplicadas, 1.024 verbetes |
| `.env` local | Preenchido, e fora do git |
| Chave de assinatura do APK | `android/tradugil.jks` gerada, fora do git |
| APK assinado | `android/app/build/outputs/apk/release/app-release.apk` |
| Repositório e CI | 7 jobs, todos verdes |
| Escolha do endereço | **DuckDNS**, decidido em 29/09. O código já está todo apontando para `tradugil.duckdns.org` |
| Nome registrado | Feito: `tradugil.duckdns.org` |
| Chaves guardadas fora da máquina | Feito |
| Escolha do servidor | **Google Cloud `e2-micro`**, decidido em 29/09 |
| Imagens de implantação | A CI constrói e publica no GHCR a cada push na `main` |

## 1. Guardar a chave de assinatura fora desta máquina  (FEITO)

Era o item mais urgente da lista, e o unico sem conserto depois. Fica aqui
como registro do que foi guardado e por que, para o dia em que precisar.

O Android recusa atualizar um aplicativo se o APK novo vier assinado com
outra chave. Quem já instalou teria que desinstalar e perder o que estava
salvo. Sem Google Play não existe o Play App Signing para guardar a chave
por você: ela existe em um lugar só, que é o seu disco.

Copie estes dois para um pendrive, um HD externo ou um armazenamento na
nuvem que **não** seja o repositório:

```
android/tradugil.jks
android/keystore.properties
```

**Guarde junto o conteúdo de `.env`**, em especial a linha
`TRADUGIL_PIMENTA_DE_SENHA`. Essa chave entra no cálculo do hash das senhas
e não fica no banco de propósito: é o que torna um vazamento só do banco
inútil para quem o roubou. Perdê-la significa que nenhuma senha cadastrada
funciona mais, e não há como recalcular sem a senha em claro, que ninguém
tem. Ela é tão irreversível quanto a chave do APK.

Se você usa um gerenciador de senhas, guarde a senha lá também. Perder o
`.jks` ou a senha significa nunca mais conseguir atualizar quem instalou.

Um disco nao avisa antes de falhar, e por isso vale conferir de tempos em
tempos se a copia ainda abre.

## 2. Registrar o nome no DuckDNS  (FEITO)

**Decidido: DuckDNS.** O endereço é `tradugil.duckdns.org`, já registrado, e
o código está todo apontando para ele: o `Caddyfile`, o CORS da API, o manifesto da
extensão e a URL da build de release do Android.

São três minutos:

1. Abra <https://www.duckdns.org> e entre com Google, GitHub ou Reddit.
2. No campo do topo, digite `tradugil` e clique em **add domain**.
3. A página mostra um **token**. Guarde no gerenciador de senhas.

**O token é a senha do serviço:** quem o tem aponta `tradugil.duckdns.org`
para o servidor que quiser. Não cole aqui no chat, não coloque no
repositório. Ele vai direto no servidor, em `/etc/tradugil/duckdns.env`,
quando chegarmos no item 3.

Nada trava enquanto o nome não existir: o servidor só precisa dele na hora de
pedir o certificado.

### Uma coisa que o DuckDNS exige a mais

O nome só continua apontando para o servidor enquanto alguém disser qual é o
IP, e a Oracle Cloud entrega IP efêmero por padrão. Sem isso o site para de
responder um dia, sem erro em lugar nenhum: o DNS aponta para um endereço que
não é mais nosso.

Já está resolvido no repositório, e é só instalar quando a máquina existir:
`infra/duckdns-atualiza.sh` com `duckdns.service` e `duckdns.timer` ao lado,
que atualizam de cinco em cinco minutos. As instruções estão no cabeçalho do
próprio script.

### Quando valer a pena trocar por um domínio próprio

`registro.br` custa cerca de R$ 40 por ano e dá um `tradugil.com.br`. Vale
quando o projeto sair do teste, porque endereço próprio é o que faz o site
parecer produto e não experimento.

A troca é barata do meu lado: quatro arquivos mudam juntos, e eu faço. O
único ponto que não é instantâneo é a extensão, porque a lista de endereços
permitidos vive no manifesto e exige publicar uma versão nova na loja.

Enquanto for DuckDNS, duas configurações ficam mais curtas de propósito: o
HSTS (uma semana em vez de um ano) e a validade do `security.txt` (seis meses
em vez de um ano). O motivo está em [`docs/SEGURANCA.md`](docs/SEGURANCA.md):
nome DuckDNS abandonado volta para a fila e outra pessoa pode registrar, e
promessa longa sobre nome emprestado é herança que ninguém pediu.

## 3. Um servidor: Google Cloud

**Decidido em 29 de setembro**, com a Oracle e a AWS fora.

Vale saber o que a pesquisa mostrou, porque o campo encolheu: hoje existem
exatamente **duas** máquinas gratuitas para sempre, a da Oracle e a
`e2-micro` do Google. O Fly.io acabou com a cota gratuita, e o Koyeb passou a
exigir cartão com bloqueio de US$ 29 em fevereiro de 2026. Render, Railway e
Zeabur continuam gratuitos, mas **dormem por inatividade**, e isso é ruim
justamente aqui: acordar uma JVM com Spring Boot e Flyway leva de 20 a 40
segundos, e quem abrisse o site depois de 15 minutos parados acharia que
está fora do ar.

### A conta

1. Abra <https://console.cloud.google.com> e entre com uma conta Google.
2. Crie um projeto (o nome não importa, pode ser `tradugil`).
3. Ative o faturamento. **Pede cartão de crédito**, igual à Oracle: é
   verificação de identidade. O nível gratuito não vira pago sozinho, e um
   cartão virtual com limite baixo funciona.

### Antes de criar a máquina, ponha um alerta de orçamento

Em *Billing > Budgets & alerts*, crie um orçamento de **R$ 1** com alerta em
50%. Um minuto, e é o que transforma "achei que era gratuito" em um e-mail no
mesmo dia.

Há um item que eu não consegui confirmar na documentação oficial: o Google
cobra por endereço IPv4 externo desde fevereiro de 2024, e várias fontes
dizem que o nível gratuito é isento, mas a página oficial do nível gratuito
não toca no assunto. O alerta responde isso em dois dias, com a sua conta.

### A máquina

Em *Compute Engine > VM instances > Create instance*. **Três campos têm que
estar certos, e em um deles o console sugere o valor errado:**

| Campo | Valor | Se errar |
|-------|-------|----------|
| Região | `us-west1`, `us-central1` ou `us-east1` | Qualquer outra é paga, inclusive São Paulo |
| Tipo de máquina | `e2-micro` | `e2-small` é o dobro, e é pago |
| Disco de inicialização | **Standard persistent disk**, 30 GB | O console sugere *Balanced*, que é pago. Troque na mão |

Imagem: **Ubuntu 24.04 LTS**. E marque *Allow HTTP traffic* e *Allow HTTPS
traffic*, que criam as regras de firewall para as portas 80 e 443.

Não existe região no Brasil no gratuito. A latência fica em torno de 150 ms,
e dá para conviver: o nível 0 da cascata é o dicionário no próprio aparelho,
e o site é um PWA que fica em cache depois da primeira visita.

### O que eu já ajustei por causa dessa máquina

Ela é pequena: 1 GB de memória, 0,25 de núcleo, e **1 GB de tráfego de saída
por mês**. Duas mudanças no repositório tiram o aperto:

- **As imagens são construídas pela CI**, publicadas no GHCR, e o servidor só
  baixa. Compilar a API com Maven nessa máquina levaria dezenas de minutos e
  provavelmente morreria sem memória no meio de um deploy.
- **O APK saiu do servidor** e foi para as Releases do GitHub. Um arquivo de
  15 MB gastaria a cota de tráfego em uns 60 downloads. O endereço do site
  continua o mesmo: `/baixar/tradugil.apk` redireciona para a última Release.

As imagens já estão publicadas. Elas sobem a cada push na `main`, e você pode
conferir em <https://github.com/Everett-gi?tab=packages>.

### Um ajuste de conta que só você pode fazer

As imagens do GHCR **nascem privadas**, e o servidor vai recusar o download
até você liberar. São dois cliques, e depois nunca mais:

1. Abra <https://github.com/Everett-gi?tab=packages>.
2. Entre em `tradugil-api`, vá em *Package settings*, role até *Danger Zone*
   e mude a visibilidade para **Public**.
3. Repita em `tradugil-borda`.

Imagem pública aqui não expõe nada. Ela tem a aplicação compilada e o site,
que já são código aberto, e nenhum segredo: eles vivem no
`/etc/tradugil/api.env`, na máquina, fora de qualquer imagem. A alternativa
seria guardar um token do GitHub no servidor só para baixar imagem, o que é
mais uma credencial de longa vida para cuidar em troca de esconder algo que
já está publicado.

**Quando a máquina estiver de pé, me avise.** O passo a passo completo (área
de troca, Docker, o atualizador do DuckDNS, os segredos e a subida) está em
[`infra/README.md`](infra/README.md), e eu te acompanho em cada comando.

### Se um dia quiser trocar

Uma VPS de verdade custa de R$ 20 a R$ 25 por mês (Hetzner, Contabo, ou
provedor brasileiro), sem teto de tráfego e com região perto. Nada do que
está montado muda: o servidor só baixa duas imagens e sobe.

## 4. Camada de inteligência artificial (opcional)

Sem ela o Tradugil funciona: os níveis 0 a 2 da cascata respondem tudo que
está no dicionário. O que ela acrescenta é uma explicação para termos que
**ninguém cadastrou ainda**, sempre marcada na tela como não verificada.

1. Abra <https://console.anthropic.com>, crie conta.
2. Em **Billing**, adicione crédito. O mínimo é US$ 5, e não é assinatura:
   é saldo que se gasta conforme o uso.
3. Em **API Keys**, crie uma chave.
4. Cole no `.env`, na linha `ANTHROPIC_API_KEY=`.

**Não cole a chave aqui no chat.** Coloque direto no arquivo.

Com US$ 5 dá para milhares de consultas, porque o modelo usado é o mais
barato da família e as respostas ficam em cache no banco.

## 5. Google Play (adiado)

Custa US$ 25, uma vez só. A decisão foi deixar para depois, e o APK do site
cobre a distribuição por enquanto.

Vale saber de dois efeitos dessa escolha:

- **A favor:** fora da loja não há análise de política, e o
  `AccessibilityService` (ler a tela de outros aplicativos), que a
  especificação apontava como o maior risco de rejeição, pode entrar na v1.
- **Contra:** quem instalar vai ver o aviso do Android sobre "fontes
  desconhecidas", e precisa liberar na mão. Isso derruba parte das
  instalações, especialmente do público menos habituado, que é justamente o
  público do produto.

## O que falta agora

Os itens 1 e 2 estão feitos. Só resta o **item 3**: criar a conta no Google
Cloud e a máquina, com atenção aos três campos da tabela.

Quando ela existir, me avise. O resto é comando, e eu te acompanho.

O item 4 (IA) e o 5 (Play Store) continuam podendo esperar.
