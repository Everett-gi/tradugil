# Segurança

O que está implementado, por que está assim, e o que ainda não está.

Escrito para ser lido por alguém revisando o projeto, então cada decisão diz o
que ela protege e o que ela não protege.

## O que este sistema tem a perder

Três coisas, em ordem de gravidade:

1. **Texto lido da tela do usuário.** O `/traduzir` recebe trechos de
   conversas privadas. É o dado mais sensível do produto e o que menos aparece
   numa checagem de segurança comum, porque não fica guardado em lugar nenhum.
2. **A cota de IA.** O `/traduzir` é público e o nível 4 custa dinheiro por
   chamada. Uma automação de termos inventados vira fatura.
3. **Contas de moderador.** Quem modera decide o que entra no dicionário, que
   é lido por pessoas idosas e por famílias como se fosse verdade estabelecida.

Não há dados de pagamento, não há documentos e não há histórico de consultas:
a arquitetura foi desenhada para não ter o que vazar.

## Senhas

**BCrypt com custo 12, sobre um HMAC-SHA256 da senha com uma chave que não
está no banco.** A chave é a "pimenta" (`TRADUGIL_PIMENTA_DE_SENHA`).

O sal do BCrypt é guardado junto do hash: quem copia a tabela `usuario` leva
os dois e ataca offline no ritmo que quiser. A pimenta vive na variável de
ambiente do servidor, então um vazamento **apenas do banco** (backup exposto,
réplica mal configurada, injeção de SQL em outro ponto) rende hashes que não
dá para quebrar. Só serve contra o vazamento parcial, e é exatamente esse o
vazamento comum.

O HMAC vem antes do BCrypt, e não concatenado, por um motivo concreto: o
BCrypt **trunca em 72 bytes sem avisar**. Com a pimenta concatenada no fim,
uma senha longa empurraria parte dela para fora e a pimenta deixaria de
participar, silenciosamente. O HMAC devolve sempre 32 bytes. O truncamento
some junto, e com ele o problema de duas senhas com os mesmos 72 primeiros
bytes abrirem a mesma conta.

O hash gravado carrega o algoritmo como prefixo: `{pimenta}$2a$12$...`. Sem
esse prefixo o formato fica congelado para sempre. Com ele, um hash antigo
continua funcionando e é **regravado no formato atual no próximo login bem
sucedido**, uma conta de cada vez, sem migração em massa.

**Regras da senha:** mínimo 8 caracteres, máximo 200, e uma lista das senhas
mais previsíveis (incluindo as brasileiras, que listas em inglês não trazem).
Não há exigência de maiúscula, número e símbolo: essa regra produz
"Senha@123" e o NIST deixou de recomendá-la em 2017. O teto de 200 não é
estética: sem ele, alguém envia um megabyte e o servidor paga o BCrypt em
cima disso.

## Força bruta

Duas camadas, porque nenhuma sozinha cobre os dois ataques:

| Ataque | O que barra |
|--------|-------------|
| Um IP tentando muitas senhas | Teto por IP: 10/min em `/auth/*`, no Caddy e na aplicação |
| Muitos IPs contra uma conta | Travamento por conta, no banco |

O travamento é por conta e fica **no banco**, não em memória. Em memória
zeraria a cada reinicialização, e reiniciar é algo que qualquer um consegue
provocar; e com mais de uma instância, cada uma teria o próprio contador.

Cinco erros seguidos travam a conta. A espera dobra a cada novo travamento da
mesma sequência: 1 minuto, 2, 4, até o teto de 1 hora. O teto existe para
alguém não conseguir trancar a conta de outra pessoa para sempre errando
senha de propósito.

**Conta travada recusa até a senha certa**, com a mesma mensagem de senha
errada. Se a senha certa passasse enquanto as erradas travam, bastaria
observar qual tentativa se comporta diferente.

O contador é gravado em **transação própria**
(`RegistradorDeFalhaDeLogin`, com `REQUIRES_NEW`), e isso não é detalhe de
implementação. Registrar a falha e recusar a requisição são duas coisas;
feitas na mesma transação, a segunda desfaz a primeira, porque a exceção é
`RuntimeException` e o Spring faz rollback. Escrito errado na primeira
versão, e a CI pegou: o contador ficava em zero depois de cinco tentativas,
e o travamento existia no código, com coluna no banco e tudo, **sem nunca
contar nada**.

É a segunda vez que este projeto comete esse erro. A primeira foi na
revogação de família de token, e o efeito foi igualmente silencioso: o
sistema registrava a detecção no log, devolvia erro, e deixava a sessão
roubada funcionando. Nas duas vezes a defesa parecia implementada e não
existia.

## Não revelar quem tem conta

E-mail inexistente, senha errada e conta travada devolvem **a mesma frase**.
E demoram o mesmo tempo: quando o e-mail não existe, a senha é comparada
contra um hash descartável calculado na subida com o codificador de verdade.

Isso já esteve quebrado. O hash descartável era uma constante escrita à mão
que não era BCrypt válido (57 caracteres onde o formato exige 53). O Spring
rejeitava pelo formato antes de calcular, e-mail inexistente respondia em
microssegundos e senha errada custava 250 ms. A diferença era de três ordens
de grandeza. `SegurancaDoLoginIT` mede os dois caminhos agora.

O cadastro também não confirma: "não foi possível concluir o cadastro com
esses dados" serve para e-mail repetido e para qualquer outro problema.

## Sessão

**Access token JWT de 15 minutos, refresh token opaco de 30 dias.**

Um JWT não pode ser revogado: uma vez emitido vale até expirar, mesmo que a
conta seja banida no minuto seguinte. Os 15 minutos são o quanto de dano um
token vazado consegue causar. A revogação de verdade acontece no refresh
token, que fica no banco como SHA-256.

SHA-256, e não BCrypt, para o refresh: o token é aleatório de 256 bits, não
tem a entropia baixa de uma senha escolhida por pessoa. Não há dicionário a
atrasar.

**Rotação com detecção de reuso:** cada renovação troca o token e marca o
antigo como substituído. Um token já usado que reaparece significa que ou o
cliente legítimo repetiu, ou alguém roubou. Como não dá para distinguir, a
família inteira cai.

A revogação roda em transação própria (`RevogadorDeSessao`, com
`REQUIRES_NEW`). Sem isso a exceção lançada logo depois desfazia a revogação
no rollback: o sistema registrava a detecção, devolvia erro e **deixava a
sessão roubada funcionando**.

**O token não carrega o e-mail.** Já carregou. Um JWT é Base64, não é
cifrado, e vai junto em toda requisição, fica no navegador e sobra em
qualquer lugar por onde um cabeçalho passe. O `sub` já identifica a conta.
`ConteudoDoTokenIT` confere que só `iss`, `iat`, `exp`, `sub` e `papel` estão
lá.

O emissor é validado na verificação, e o algoritmo é fixo em HS256: aceitar o
cabeçalho do token como fonte da verdade sobre o algoritmo é a família de
ataques "alg confusion", incluindo `alg=none`.

## Autorização

A regra final é `anyRequest().denyAll()`, e não `authenticated()`. Um endpoint
novo criado sem que ninguém pense em autorização **nasce fechado**: o modo de
falha vira "não funciona e alguém reclama", em vez de "funciona para qualquer
um e ninguém percebe".

Isso já foi acionado de verdade: o `/api/v1/categorias` do catálogo devolveu
401 até alguém liberá-lo explicitamente. `AcessoPublicoIT` fixa hoje a
superfície pública **e** a fechada, porque uma linha `permitAll` larga demais,
escrita às pressas para consertar um 401 desses, abriria a contribuição e a
moderação sem nada ficar vermelho.

Ler o dicionário é público de propósito: exigir cadastro para entender uma
mensagem afastaria exatamente o público que o produto quer atender.

## Injeção de prompt na camada de IA

O texto que chega ao nível 4 foi lido da tela do usuário e é **dado não
confiável por definição**: pode conter instruções plantadas por quem escreveu
a mensagem que a vítima está tentando entender. O atacante não precisa de
acesso nenhum ao sistema; basta mandar uma mensagem para alguém que usa o
Tradugil.

Quatro camadas, nenhuma suficiente sozinha: delimitação por tags,
neutralização do delimitador no texto do usuário, esquema de saída fixo (sem
campo de texto livre por onde uma injeção sairia) e validação semântica do
conteúdo.

E a defesa que não está no prompt: a resposta **nunca** é apresentada como
verbete revisado. Mesmo que todas as camadas falhem, o pior resultado é um
texto errado rotulado como não confiável.

## Cabeçalhos

Enviados pela aplicação, não só pela borda, porque defesa que depende de uma
peça externa estar no lugar falha em silêncio:

| Cabeçalho | Valor |
|-----------|-------|
| `Content-Security-Policy` | `default-src 'none'; frame-ancestors 'none'` |
| `Strict-Transport-Security` | 1 ano, com subdomínios |
| `Referrer-Policy` | `no-referrer` |
| `Permissions-Policy` | geolocalização, câmera, microfone, pagamento e USB negados |
| `Cross-Origin-Opener-Policy` | `same-origin` |
| `Cross-Origin-Resource-Policy` | `same-site` |
| `X-Content-Type-Options` | `nosniff` |
| `X-Frame-Options` | `DENY` |

`no-referrer` não é enfeite: a URL de uma consulta contém o termo pesquisado,
e o termo pesquisado é conteúdo da tela de alguém.

CORS tem lista explícita de origens, nunca `*`: liberar qualquer origem
deixaria qualquer página da web gastar a cota de IA em nome do usuário.

## Auditoria de moderação

Append-only, **garantido pelo banco**. A coluna `moderador_id` guarda o estado
atual, e estado atual pode ser sobrescrito: um moderador que aprova algo
impróprio e depois rejeita para encobrir faz a aprovação desaparecer. A tabela
`auditoria_de_moderacao` tem um gatilho que recusa `UPDATE` e `DELETE`,
inclusive por SQL direto, o que o teste de integração confirma.

## A documentação da API não é pública

Fechada por padrão (`tradugil.documentacao.publica: false`) e não encaminhada
pelo Caddy. Fechada por **padrão**, e não fechada quando um perfil de produção
estiver ativo: assim o esquecimento resulta em documentação indisponível, e
não em documentação publicada sem ninguém ter decidido isso.

Em desenvolvimento: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`.

## Auditoria de setembro de 2026

Varredura completa das superfícies, com as checagens rodadas e não supostas.
O que estava limpo:

| Verificação | Resultado |
|---|---|
| Segredo em qualquer commit do histórico | nenhum |
| `.env`, `.jks`, `keystore.properties` rastreados | nenhum, e todos ignorados |
| `npm audit` nos três pacotes | zero vulnerabilidades |
| SQL por concatenação | nenhum: tudo parametrizado |
| `dangerouslySetInnerHTML`, `innerHTML`, `eval` | nenhum |
| `Runtime.exec`, desserialização Java | nenhum |
| Permissões do Android | só `INTERNET` e `ACCESS_NETWORK_STATE` |
| Injeção de script no workflow | nenhuma interpolação perigosa |
| Corpo de erro ecoando o que foi enviado | não, e há teste |
| Cabeçalho `Server` na resposta | ausente |

E as quatro coisas que ela encontrou:

### 1. O limitador podia ser burlado com um cabeçalho forjado

A pior das quatro, e era minha. O filtro lia `X-Forwarded-For` direto para
descobrir o cliente. Quem falasse com a aplicação sem passar pelo proxy
escrevia o cabeçalho que quisesse e ganhava um balde novo a cada requisição:
o limitador continuava contando e não limitava ninguém.

Quem valida esse cabeçalho é o `RemoteIpValve` do Tomcat, que já estava
ligado e que só o respeita quando a conexão vem de um proxy confiável. Ler o
cabeçalho por conta própria desfazia essa validação. Agora usa
`getRemoteAddr()`, que é o endereço já apurado.

`LimitadorDeRequisicoesTest` cobre. O teste foi conferido contra o código
antigo: falha com ele, passa com a correção.

### 2. O `GITHUB_TOKEN` da CI era mais poderoso do que precisava

O workflow não declarava `permissions:`, então o token herdava o padrão do
repositório, que costuma ser leitura **e escrita**. Uma ação de terceiro
comprometida teria permissão de empurrar commit com a identidade do
repositório. Agora é `contents: read`.

### 3. A build de depuração do Android não conseguia falar com a API local

Ela aponta para `http://10.0.2.2:8080`, e o Android bloqueia tráfego em
texto puro por padrão desde a versão 9. Toda chamada falharia com "Cleartext
HTTP traffic not permitted".

O caminho fácil seria `usesCleartextTraffic="true"` no manifesto principal,
que liberaria HTTP para qualquer endereço **inclusive na build que vai para o
telefone das pessoas**, e é das primeiras coisas que análise automática aponta
como risco. A correção é um `network_security_config.xml` em `src/debug`,
válido só para `10.0.2.2` e `localhost`. Conferido no APK: a configuração
está na build de depuração e não está na de release.

### 4. O CORS de produção só permitia localhost

O padrão em `application.yml` trazia as origens do Vite, o que significa que
uma implantação feita sem sobrescrever a variável aceitaria requisição da
máquina de qualquer pessoa e recusaria a do próprio site. Não era furo grave,
porque a API não usa cookie e as rotas liberadas já são públicas, mas é o tipo
de padrão que envelhece mal. As origens de desenvolvimento foram para
`application-dev.yml`.

## Os certificados, e o que ainda faz o aplicativo parecer suspeito

Esta parte não é sobre o código estar correto: é sobre os sistemas de
segurança de terceiros confiarem nele.

**O APK está assinado**, com certificado próprio
(`CN=Gildean Monteiro Do Nascimento`, SHA-256 começando em `abab4cc9`), e
essa assinatura é o que permite atualizar quem já instalou. Ela não é o que
faz o Android confiar no aplicativo.

**O que ainda vai assustar quem instalar:** fora da Google Play, o Android
mostra o aviso de "fontes desconhecidas" e o Play Protect exibe uma tela de
alerta na primeira execução. Isso não é sinal de que algo está errado no
aplicativo; é o comportamento padrão para qualquer APK que não veio da loja.
Só há dois caminhos para remover esse atrito, e ambos custam:

- publicar na Google Play (US$ 25, uma vez), que é o que faz o Play Protect
  parar de alertar;
- ou publicar o SHA-256 do APK no site, ao lado do link, para quem quiser
  conferir que baixou o arquivo íntegro. Não remove o aviso, e dá a quem
  desconfia uma forma de verificar.

**A extensão** hoje só se instala em modo desenvolvedor. Para chegar a alguém
que não seja você, ela precisa passar pela análise da Chrome Web Store, que é
justamente o processo que atesta que ela não é maliciosa. O desenho já foi
feito pensando nisso: ela não declara script de conteúdo, pede quatro
permissões e duas origens, e nada disso é gratuito na análise.

**O `AccessibilityService`** do Android, previsto para a F2, é o item mais
delicado do projeto inteiro. É a permissão que aplicativos maliciosos mais
usam para ler tela alheia, e a Play Console exige justificativa em vídeo para
liberá-la. Fora da loja não há essa análise, o que foi registrado como
vantagem no README; vale registrar aqui o outro lado: é também o recurso que
mais faz um APK de fora da loja parecer malicioso para um antivírus.

**`/.well-known/security.txt`** passou a ser publicado (RFC 9116), com
contato e prazo de validade, para quem encontrar uma falha ter caminho até
quem resolve.

## O que ainda não está feito

Escrito aqui porque uma lista de defesas sem a lista de buracos é propaganda.

- **Sem verificação de e-mail.** Dá para cadastrar com um e-mail de outra
  pessoa. Só importa de verdade quando existir recuperação de senha, que
  também não existe.
- **Sem 2FA para moderadores.** É a conta com mais poder no sistema, e hoje
  ela é protegida só por senha.
- **Sem HIBP.** A lista de senhas proibidas é curta e escrita à mão. O certo
  seria consultar a base de senhas vazadas por k-anonimato; isso traz uma
  chamada de rede para dentro do cadastro e a decisão sobre o que fazer
  quando ela falha.
- **O limitador da aplicação conta por instância.** Com várias instâncias, o
  teto efetivo multiplica. Aceitável porque não é a única camada: o teto
  global por IP é do Caddy e o travamento por conta está no banco. Quando
  incomodar, o lugar de resolver é trocar o Caffeine por um contador
  compartilhado, sem mudar a interface do filtro.
- **`X-Forwarded-For` é forjável** por quem fala direto com a aplicação. A
  mitigação é não expor a porta 8080 fora do proxy.
- **Sem cifragem de coluna.** O e-mail é o único dado pessoal guardado e está
  em texto no banco. Cifrar exigiria um índice cego (HMAC) para continuar
  achando o usuário pelo e-mail no login. Vale fazer, e ainda não foi feito.

## Como testar

```bash
cd api && mvn test
```

Os testes de segurança precisam de PostgreSQL real:
`SegurancaDoLoginIT`, `ConteudoDoTokenIT`, `AcessoPublicoIT`,
`ServicoDeAutenticacaoIT` e `ModeracaoIT`. Sem
`TRADUGIL_TEST_DB_URL` eles são pulados com aviso, e rodam integralmente na
CI.
