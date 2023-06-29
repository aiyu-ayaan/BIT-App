import org.gradle.api.JavaVersion

object PlugIns {
    const val androidApplication = "com.android.application"
    const val androidLibrary = "com.android.library"
    const val kotlinAndroid = "org.jetbrains.kotlin.android"
    const val kotlinKapt = "kapt"
    const val hilt = "dagger.hilt.android.plugin"
    const val navigationSafeArgs = "androidx.navigation.safeargs.kotlin"
    const val parcelize = "kotlin-parcelize"
    const val crashlytics = "com.google.firebase.crashlytics"
    const val googleServices = "com.google.gms.google-services"
    const val ksp = "com.google.devtools.ksp"
}

object AndroidSdk {
    const val min = 24
    const val compile = 33
}

object App {
    const val id = "com.atech.bit"
    const val versionCode = 60
    const val versionName = "4.1.2 Patch 1"
}

object JavaVersionCompatibility {
    val javaVersion = JavaVersion.VERSION_17
    const val kotlinVersion = "17"
}


object Modules {
    const val core = ":core"
    const val syllabus = ":syllabus"
}
