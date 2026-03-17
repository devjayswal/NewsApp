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
            implementation(projects.core.utils)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.coil)
        }
        androidMain.dependencies {
            implementation(libs.retrofit.core)
            implementation(libs.retrofit.kotlin.serialization)
            implementation(libs.okhttp.core)
            implementation(libs.okhttp.logging)
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
