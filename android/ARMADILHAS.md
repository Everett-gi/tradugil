# Armadilhas encontradas ao subir este módulo

Registro do que quebrou na primeira compilação real, para não custar de novo.

## 1. Backslash em `includeGroupByRegex`

`settings.gradle.kts` não compilava:

```
e: settings.gradle.kts:5:41: Unsupported escape sequence.
```

`includeGroupByRegex` recebe uma **string Kotlin**, não um regex literal. O
ponto do regex precisa de duas barras invertidas na fonte:

```kotlin
includeGroupByRegex("com\\.android.*")   // certo
includeGroupByRegex("com\.android.*")    // erro de compilação
```

O erro entrou porque o arquivo foi criado por um heredoc de shell, que
consumiu uma das barras. Ao gerar arquivos Kotlin ou Java por script, confira
o resultado — o escape acontece duas vezes e é fácil perder uma camada.

## 2. O plugin do Kotlin não existe mais no AGP 9

```
The 'org.jetbrains.kotlin.android' plugin is no longer required for Kotlin
support since AGP 9.0.
```

Desde o Android Gradle Plugin 9.0, o suporte a Kotlin é embutido. Aplicar
`org.jetbrains.kotlin.android` junto **faz o build falhar na configuração** —
não é aviso, é erro.

O que continua sendo necessário é `org.jetbrains.kotlin.plugin.compose`, para
o compilador do Compose.

Praticamente todo tutorial e todo modelo de projeto anterior ao AGP 9 traz os
dois plugins. Se você copiar um `build.gradle.kts` da internet, é o primeiro
lugar para olhar.

## 3. `withStyle` precisa de import explícito

```
e: TelaPrincipal.kt:179:13 Unresolved reference 'withStyle'.
```

`withStyle` é função de extensão de `AnnotatedString.Builder` e mora em
`androidx.compose.ui.text.withStyle` — não vem junto com
`buildAnnotatedString`, que está no mesmo pacote. O Android Studio importa
sozinho quando você digita; ao escrever o arquivo por fora, tem que lembrar.

## 4. Abrir a pasta certa

O Android Studio precisa abrir **`tradugiria/android`**, não `tradugiria`.

A raiz é um monorepo poliglota — Maven em `api/`, npm em `web/`, Gradle só em
`android/` — e não tem `settings.gradle.kts`. Sem ele, o Studio trata a pasta
como projeto genérico e **nunca oferece sincronizar**. Não aparece erro
nenhum: simplesmente não acontece nada, que é o mais confuso.

## 5. `gradle-daemon-jvm.properties` fica fora do git

O Gradle 9 gera esse arquivo pedindo um JDK 25 para o daemon. O projeto
compila com 17, e versioná-lo obrigaria a CI a baixar um JDK inteiro só para
rodar o daemon. Está no `.gitignore`.
