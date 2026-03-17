import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(projects.feature.home)  // this line include home feature in this module
            implementation(projects.core.network)  // this line include network core in this module
            implementation(projects.core.utils)   // this line include utils core in this module
            implementation(libs.coil.compose)     // this lib help to  render image  from url with async
            implementation(libs.coil.network.okhttp)  // this lib  help to use http for above lib
            implementation("io.coil-kt.coil3:coil-gif:3.0.0")   // for support gif img rendering
            implementation("io.coil-kt.coil3:coil-svg:3.0.0")   // for support svg img rendering

        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)  // this libs for composable ui standard lib
            implementation(libs.compose.foundation) // this libs for composable ui standard lib
            implementation(libs.compose.material3)   // this libs for composable ui standard lib
            implementation(libs.compose.ui)         // this libs for composable ui standard lib
            implementation(libs.compose.components.resources)  // this libs for composable ui standard lib implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose) // to access viewmodel in compose
            implementation(libs.androidx.lifecycle.runtimeCompose)  // to access viewmodel in compose
            implementation(projects.shared)  // to access shared folder currently unused
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.example.kmp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.kmp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}
