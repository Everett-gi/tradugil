pluginManagement {
    repositories {
        // Filtra por grupo para não procurar dependências da AndroidX no
        // Maven Central nem bibliotecas gerais no repositório do Google:
        // cada consulta a repositório errado é uma ida à rede que sempre
        // volta vazia e atrasa todo build limpo.
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    // Recusa repositório declarado dentro de um módulo: todos ficam aqui,
    // onde dá para auditar de onde o build baixa código.
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Tradugil"
include(":app")
