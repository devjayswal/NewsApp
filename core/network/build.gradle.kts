// core/network/build.gradle.kts
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.utils)   // to import utils core so that we can use apiwrapper
            implementation(libs.kotlinx.serialization.json) // for json serialization and deserialization
            implementation(libs.coil)   // for image rendering not in  use in this module
        }
        androidMain.dependencies {
            implementation(libs.retrofit.core)       // retrofit for api call
            implementation(libs.retrofit.kotlin.serialization)  // retrofit for json to object convert
            implementation(libs.okhttp.core)    // okhttp for network call and logging interceptor for log network call in logcat
            implementation(libs.okhttp.logging) // okhttp for network call and logging interceptor for log network call in logcat

        }
    }
}

android {
    namespace = "com.example.kmp.core.network"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
