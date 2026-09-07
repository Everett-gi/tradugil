plugins {
    // A partir do AGP 9.0 o suporte a Kotlin e embutido: aplicar tambem o
    // plugin org.jetbrains.kotlin.android faz o build falhar na configuracao.
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "br.com.tradugiria.android"
    compileSdk = 37

    defaultConfig {
        applicationId = "br.com.tradugiria.android"
        // Android 8. O publico inclui pessoas com aparelhos antigos, e subir
        // o minimo por conveniencia excluiria exatamente quem o produto quer
        // atender. Nada do que o app faz exige API mais nova que isso.
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // 10.0.2.2 é como o emulador enxerga o localhost da máquina que
            // o hospeda. Em aparelho físico, troque pelo IP da máquina na
            // rede local ou pela URL de produção.
            buildConfigField("String", "URL_DA_API", "\"http://10.0.2.2:8080\"")
        }
        release {
            buildConfigField("String", "URL_DA_API", "\"https://tradugiria.app\"")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            //
            // A distribuicao e por APK baixavel do site, nao pela Play Store.
            // Isso torna a assinatura responsabilidade do projeto: o Android
            // recusa atualizar um app instalado se o APK novo vier assinado
            // com outra chave, e o usuario teria que desinstalar e perder os
            // dados locais. O keystore fica fora do git (ver .gitignore) e
            // precisa ser guardado com backup -- perde-lo significa nunca
            // mais conseguir atualizar quem ja instalou.
            //
            // signingConfig = signingConfigs.getByName("release")
            //
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    buildFeatures {
        compose = true
        // A URL da API vem daqui, e não de uma constante no código: o app de
        // depuração fala com a máquina de desenvolvimento e o de release com
        // o servidor real, sem ninguém precisar lembrar de trocar uma linha
        // antes de gerar o APK que vai para o site.
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)
}
