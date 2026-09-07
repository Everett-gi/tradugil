# O que falta fazer por fora

Este arquivo é a lista do que **só você pode fazer**: coisas que pedem uma
conta, um cartão ou uma decisão sua. O que é código já está no repositório.

Estado em 7 de setembro de 2026.

## Já está pronto

Nada a fazer nestes:

| Item | Situação |
|------|----------|
| Banco de dados (Neon) | Conectado, 27 migrações aplicadas, 934 verbetes |
| `.env` local | Preenchido, e fora do git |
| Chave de assinatura do APK | `android/tradugil.jks` gerada, fora do git |
| APK assinado | `android/app/build/outputs/apk/release/app-release.apk` |
| Repositório e CI | 6 jobs, todos verdes |

## 1. Guardar a chave de assinatura fora desta máquina

**É o item mais urgente da lista**, e o único que não tem conserto depois.

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

Faça isso hoje. Um disco não avisa antes de falhar.

## 2. Um endereço na internet

Hoje o Tradugil só roda na sua máquina. Para outra pessoa acessar, precisa
de um endereço.

### Opção gratuita: DuckDNS

Serve para testar e mostrar para alguém. O endereço fica no formato
`tradugil.duckdns.org`.

1. Abra <https://www.duckdns.org> e entre com Google, GitHub ou Reddit.
2. No campo do topo, digite `tradugil` e clique em **add domain**.
3. Guarde o **token** que aparece na página. Ele é a senha do serviço:
   trate como senha, não cole em lugar nenhum público.

### Opção paga: registro.br

Cerca de R$ 40 por ano, e dá um `tradugil.com.br`. Vale quando o projeto
sair do teste, porque um endereço próprio é o que faz o site parecer um
produto e não um experimento.

1. Abra <https://registro.br>, crie conta com CPF.
2. Busque `tradugil.com.br` e conclua o pagamento (boleto ou Pix).
3. O domínio fica ativo em algumas horas.

**Escolha uma das duas e me avise qual.** A configuração do servidor muda
conforme a escolha, e eu já deixo o `infra/Caddyfile` pronto.

## 3. Um servidor

A Oracle Cloud tem um plano permanentemente gratuito que comporta este
projeto inteiro com folga: 4 núcleos ARM, 24 GB de memória.

1. Abra <https://www.oracle.com/br/cloud/free/> e clique em
   **Comece gratuitamente**.
2. Preencha o cadastro. **Ele pede um cartão de crédito.** É verificação de
   identidade, não cobrança: o plano Always Free não vira pago sozinho, e
   sem confirmar o upgrade nada é cobrado. Um cartão virtual com limite
   baixo funciona e é o mais seguro.
3. Escolha a região **Brazil East (São Paulo)**. Perto daqui, e é onde o
   banco no Neon já está.
4. Espere o e-mail de confirmação. Pode levar de minutos a algumas horas.

Quando a conta estiver de pé, me avise: eu passo o passo a passo de criar a
máquina, abrir as portas e subir a aplicação.

**Cuidado com uma pegadinha conhecida:** ao criar a máquina, o formato
`VM.Standard.A1.Flex` é o gratuito. O `VM.Standard.E2.1.Micro` também é,
mas é pequeno demais para a API Java. Qualquer outro é pago.

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

## Se você tiver 20 minutos hoje

Faça o item 1 (guardar a chave) e comece o item 3 (a conta na Oracle, que
demora para aprovar). Os outros esperam.
