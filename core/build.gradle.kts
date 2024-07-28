/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("androidx.room")
    id("org.jetbrains.dokka")
}

android {
    namespace = "com.atech.core"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")


        room {
            schemaDirectory("$projectDir/schemas")
        }
    }
    flavorDimensions += "type"

    productFlavors {
        create("global") {
            dimension = "type"
        }
        create("beta") {
            dimension = "type"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
    }

}

dependencies {


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.hilt.android)
    implementation(libs.androidx.browser)
    ksp(libs.hilt.android.compiler)

    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation(libs.androidx.room.paging)
    ksp(libs.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.gson)


    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    releaseImplementation(libs.firebase.crashlytics)
    implementation(libs.firebase.remote.config)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)



    implementation(libs.retrofit)
    implementation(libs.retrofit.json)
    implementation(libs.retrofit.scalars)
    implementation(libs.okhttp3.logging.interceptor)
    implementation (libs.jsoup)



    implementation(libs.androidx.datastore.preferences)
    implementation(libs.cryptore)
}
