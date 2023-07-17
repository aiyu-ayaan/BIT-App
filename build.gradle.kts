buildscript {
 
    dependencies {
        classpath(libs.google.service)
        classpath(libs.navigation.safe.args.gradle.plugin)
        classpath(libs.firebase.crashlytics.gradle)
        classpath(libs.kotlin.gradle.plugin)
    }

    repositories {
        google()
    }

}
plugins {
    id ("com.android.application") version "7.4.2" apply false
    id ("com.android.library") version "7.4.2" apply false
    id ("org.jetbrains.kotlin.android") version "1.7.20" apply false
    id("com.google.dagger.hilt.android") version "2.47" apply false
}
