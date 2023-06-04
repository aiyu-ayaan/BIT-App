buildscript {
 
    dependencies {
        classpath("com.google.gms:google-services:4.3.15")
        classpath("com.google.gms:google-services:${Versions.googleServiceVersion}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.nav_version}")
        classpath("com.google.firebase:firebase-crashlytics-gradle:${Versions.crashlyticsVersion}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin_version}")
    }

    repositories {
        google()
    }

}
plugins {
    id ("com.android.application") version "7.4.2" apply false
    id ("com.android.library") version "7.4.2" apply false
    id ("org.jetbrains.kotlin.android") version "1.7.20" apply false
    id("com.google.dagger.hilt.android") version "2.46.1" apply false
}
