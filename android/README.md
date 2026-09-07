# Tradugil: Android

Aplicativo nativo. Distribuído como **APK baixável pelo site**, não pela
Google Play.

## Estado deste módulo

| Parte | Situação |
|-------|----------|
| `Normalizador` e `Tokenizador` | Verificados: 13 testes passando |
| Cliente da API, ViewModel, tela Compose | **Compilam**; APK de depuração gerado |
| Compartilhar de outro app (RF20) | Declarado no manifesto, a validar em aparelho |
| Dicionário offline (Room) | Pronto; falta o pacote de sincronização |
| Bolha flutuante, leitura de tela, OCR | Não iniciado |

O módulo compila e o APK de depuração é gerado. Falta validar em aparelho
real o fluxo de compartilhamento, e implementar o dicionário offline e as
funções de leitura de tela.

## Abrindo

Abra **a pasta `android/`** no Android Studio: não a raiz do repositório.
A raiz é um monorepo poliglota sem `settings.gradle.kts`, então o Studio a
trata como pasta comum e nunca oferece sincronizar.

Pela linha de comando:

```bash
cd android && ./gradlew assembleDebug
```

O APK sai em `app/build/outputs/apk/debug/app-debug.apk`.

Os erros encontrados na primeira compilação real estão em
[`ARMADILHAS.md`](ARMADILHAS.md): vale ler antes de mexer no build.

### Versões

Todas conferidas nos repositórios reais (Google Maven e Maven Central) em
2026-09-06, não escolhidas de memória:

| Item | Versão |
|------|--------|
| Android Gradle Plugin | 9.4.0 |
| Kotlin | 2.4.10 |
| Gradle | 9.7.1 |
| Compose BOM | 2026.08.00 |
| compileSdk / targetSdk | 37 |
| minSdk | 26 (Android 8) |

`minSdk 26` é deliberado: o público inclui pessoas com aparelhos antigos, e
subir o mínimo por conveniência excluiria exatamente quem o produto quer
atender.

## Apontando para a API

A URL vem do `BuildConfig`, não de uma constante no código:

- **debug** → `http://10.0.2.2:8080` (como o emulador enxerga o `localhost`
  da máquina que o hospeda). Em aparelho físico, troque pelo IP da máquina
  na rede local.
- **release** → `https://tradugil.app`

Estão em `app/build.gradle.kts`.

## Assinatura do APK

Como a distribuição é por APK direto, a assinatura é responsabilidade do
projeto: não há Play App Signing para guardar a chave.

O Android **recusa atualizar** um app instalado se o APK novo vier assinado
com outra chave: o usuário teria que desinstalar, perdendo o que estava
salvo no aparelho. Então:

- Gere **um** keystore e use sempre o mesmo.
- Guarde-o fora do repositório (já está no `.gitignore`) **e com backup**.
  Perder essa chave significa nunca mais conseguir atualizar quem já
  instalou.

### Gerando a chave

O `keytool` vem junto com o Java, que já está instalado. Abra o PowerShell,
entre na pasta `android` e rode isto **em uma linha só**:

```bash
keytool -genkeypair -v -keystore tradugil.jks -alias tradugil -keyalg RSA -keysize 4096 -validity 10000
```

Ele vai perguntar, nesta ordem:

1. Uma senha, e depois a confirmação. **Nada aparece na tela enquanto você
   digita**, nem asteriscos. Isso é normal e é o que mais confunde: parece
   travado, mas está recebendo.
2. Nome, unidade, organização, cidade, estado e país. Pode preencher com
   seus dados; nada disso vira público no aplicativo.
3. Uma confirmação final, em que a resposta esperada é `sim` ou `yes`.

`-validity 10000` são cerca de 27 anos. Um prazo curto obrigaria a trocar a
chave depois, e trocar a chave é justamente o que não dá para fazer.

### Ligando a assinatura no build

Copie `keystore.properties.exemplo` para `keystore.properties` e preencha as
senhas. O arquivo está no `.gitignore`, junto com o próprio `.jks`.

```bash
./gradlew assembleRelease
```

O APK sai em `app/build/outputs/apk/release/app-release.apk`, já assinado.

**O build funciona sem esse arquivo**, e gera um APK sem assinatura. Isso é
necessário, não descuido: a CI não tem o keystore e não deveria ter, porque
a chave de release dentro do repositório é exatamente o que não pode
acontecer. Sem o arquivo, a CI ainda prova que o código compila; o APK
distribuível sai só da máquina de quem tem a chave.

### Conferindo a assinatura

```bash
apksigner verify --verbose --print-certs app/build/outputs/apk/release/app-release.apk
```

O esperado é `v2 scheme: true` e `v1 scheme: false`. O V1 só é necessário
abaixo da API 24, e o `minSdk` aqui é 26.

## Por que sem Google Play

Decisão de setembro de 2026: não há orçamento para a taxa de US$ 25 nem para
a verificação de conta. A publicação fica para quando houver.

Isso tem um efeito colateral bom: fora da loja não há análise de política, e
o `AccessibilityService` (que a especificação apontava como o maior risco de
rejeição) pode entrar na v1 sem esse obstáculo.

Nada da arquitetura muda quando a publicação acontecer; só o canal de entrega.
