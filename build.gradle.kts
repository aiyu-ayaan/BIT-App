plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
    alias(libs.plugins.androidLibrary) apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0" apply false
}
true // Needed to make the Suppress annotation work for the plugins block