/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googleAndroidLibrariesMapsplatformSecretsGradlePlugin)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("org.jetbrains.dokka")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.atech.bit"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.atech.bit"
        minSdk = 24
        targetSdk = 35
        versionCode = 84
        versionName = "6.0.1.5.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").reader())

        buildConfigField(
            "String",
            "FIREBASE_WEB_CLIENT",
            "\"${properties.getProperty("FIREBASE_WEB_CLIENT")}\""
        )
    }
    bundle {
        storeArchive {
            enable = false
        }
    }
    flavorDimensions += "type"
    productFlavors {
        create("global") {
            dimension = "type"
            versionNameSuffix = "-global"
        }
        create("beta") {
            dimension = "type"
            versionNameSuffix = "-beta"
        }
    }
    signingConfigs {
        create("release") {
            storeFile = file("keystore/keystore.jks")
            storePassword = System.getenv("SIGNING_STORE_PASSWORD")
            keyAlias = System.getenv("SIGNING_KEY_ALIAS")
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.5.8"
//    }
    composeCompiler {
        enableStrongSkippingMode = true

        reportsDestination = layout.buildDirectory.dir("compose_compiler")
        stabilityConfigurationFile =
            rootProject.layout.projectDirectory.file("stability_config.conf")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(project(":chat"))
    implementation(project(":core"))
    implementation(project(":syllabus"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material)

    implementation(libs.app.update.ktx)

//    androidTestImplementation(platform(libs.androidx.compose.bom))
//    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.fragment)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.activity.ktx)

//    implementation(libs.swipe)

    implementation(libs.play.service.auth)

    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    releaseImplementation(libs.firebase.crashlytics)
    implementation(libs.firebase.message)

    implementation(libs.calender)


    implementation(libs.lottie.compose)

    implementation(libs.coil.kt.coil.compose)
    implementation(libs.coil.svg)


    implementation(libs.markdownView.android)

    implementation(libs.accompanist.permissions)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    releaseImplementation(libs.firebase.crashlytics)

    implementation(libs.compose.video)
    implementation(libs.androidx.media3.exoplayer) // [Required] androidx.media3 ExoPlayer dependency
    implementation(libs.androidx.media3.session) // [Required] MediaSession Extension dependency
    implementation(libs.media3.ui) // [Required] Base Player UI

    implementation(libs.androidx.core.splashscreen)

    implementation(libs.graph)
    implementation(libs.truetime.android)

    implementation(libs.jetlime)


}
