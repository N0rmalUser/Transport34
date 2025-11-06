import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)

    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "ru.normal.trans34"
    compileSdk = 36

    defaultConfig {
        applicationId = "ru.normal.trans34"
        minSdk = 26
        targetSdk = 36

        val versionNameFile = rootProject.file("version.txt")
        versionName = versionNameFile.readText().trim()

        val versionCodeFromGit = "git rev-list --count HEAD".runCommand(rootDir).toInt()
        versionCode = versionCodeFromGit

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProps = rootProject.file("local.properties")
        val properties = Properties().apply {
            load(localProps.inputStream())
        }

        val mapsApiKey = properties.getProperty("MAPS_API_KEY")
            ?: error("MAPS_API_KEY is missing in local.properties")
        buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")

        val githubUser = properties.getProperty("GITHUB_USER")
            ?: error("GITHUB_USER is missing in local.properties")
        buildConfigField("String", "GITHUB_USER", "\"$githubUser\"")

        val repoName = properties.getProperty("REPO_NAME")
            ?: error("REPO_NAME is missing in local.properties")
        buildConfigField("String", "REPO_NAME", "\"$repoName\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            resValue("string", "app_name", "Transport 34 Debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += listOf(
            "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
            "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api"
        )
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Default
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // JSON
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)

    // Fonts
    implementation(libs.androidx.ui.text.google.fonts)

    // Icons
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)

    // Dagger Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Navigation
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Maps
    implementation(libs.yandex.maps)

    // Other
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.appcompat)
    implementation(libs.accompanist.systemuicontroller)

    configurations.all {
        exclude("com.intellij","annotations")
    }
}

tasks.register("printVersionName") {
    doLast {
        println(android.defaultConfig.versionName)
    }
}

fun String.runCommand(workingDir: File): String {
    return ProcessBuilder(*split(" ").toTypedArray())
        .directory(workingDir)
        .redirectErrorStream(true)
        .start()
        .inputStream
        .bufferedReader()
        .readText()
        .trim()
}