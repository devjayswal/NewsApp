import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)          // composable function standard library
            implementation(libs.compose.foundation)       // composable function standard library
            implementation(libs.compose.material3)       // composable function standard library
            implementation(libs.compose.ui)       // composable function standard library
            implementation(libs.compose.components.resources)       // composable function standard library
            implementation(libs.compose.uiToolingPreview)       // composable function standard library
            implementation(libs.androidx.lifecycle.viewmodelCompose)   // for accessing the specific scope
            implementation(libs.androidx.lifecycle.runtimeCompose) // for accessing the specific scope
            implementation(projects.core.utils) // to access the core utils and network
            implementation(projects.core.network) // to access the core utils and network
            implementation(libs.coil)  // for loading the async image from url
            implementation("io.coil-kt.coil3:coil-compose:3.0.0")  // for loading the async image from url
            
            // Use api for Room so that modules depending on :common can access Room classes
            api(libs.androidx.room.runtime)
            api(libs.androidx.room.ktx)
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
}

android {
    namespace = "com.example.kmp.common"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
