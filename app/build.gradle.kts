import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)

    alias(libs.plugins.jetbrains.kotlin.serialization)
}
val keystorePropertiesFile: File = rootProject.file("keystore.properties")

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

        val repoName =
            properties.getProperty("REPO_NAME") ?: error("REPO_NAME is missing in local.properties")
        buildConfigField("String", "REPO_NAME", "\"$repoName\"")
    }

    if (keystorePropertiesFile.exists()) {
        val keystoreProperties = Properties()
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
        signingConfigs {
            create("release") {
                keyAlias = keystoreProperties["keyAlias"].toString()
                keyPassword = keystoreProperties["keyPassword"].toString()
                storeFile = file(keystoreProperties["storeFile"]!!)
                storePassword = keystoreProperties["storePassword"].toString()
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            if (keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            resValue("string", "app_name", "Debug 34")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    kotlin {
        android {
            compilerOptions {
                jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
                freeCompilerArgs.addAll(
                    listOf(
                        "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
                        "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api"
                    )
                )
            }
        }
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = false
        }
    }

    applicationVariants.all {
        outputs.all {
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                "Transport34-${defaultConfig.versionName}-${name}.apk"
        }
    }
}

dependencies {
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.serialization)

    implementation(libs.bundles.ktor)

    implementation(libs.bundles.hilt)
    ksp(libs.hilt.android.compiler)

    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    implementation(libs.androidx.datastore)

    implementation(libs.yandex.maps)

    implementation(libs.androidx.ui.text.google.fonts)

    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.foundation)

    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    configurations.all {
        exclude(group = "com.intellij", module = "annotations")
    }
}

tasks.register("printVersionName") {
    doLast {
        println(android.defaultConfig.versionName)
    }
}

fun String.runCommand(workingDir: File): String {
    return ProcessBuilder(*split(" ").toTypedArray()).directory(workingDir)
        .redirectErrorStream(true).start().inputStream.bufferedReader().readText().trim()
}