/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.googleAndroidLibrariesMapsplatformSecretsGradlePlugin) apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    id("com.google.devtools.ksp") version "2.1.10-1.0.30" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("androidx.room") version "2.6.1" apply false
    id("com.google.firebase.crashlytics") version "3.0.2" apply false
    id("org.jetbrains.dokka") version "1.9.10"
    alias(libs.plugins.compose.compiler) apply false
}