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
    const val versionCode = 57
    const val versionName = "4.1.1 Patch 30"
}

object JavaVersionCompatibility {
    val javaVersion = JavaVersion.VERSION_17
    const val kotlinVersion = "17"
}


object Versions {
    const val kotlin_version = "1.8.21"
    const val playstorecore = "1.8.1"
    const val material = "1.9.0-beta01"
    const val playServicesAuthVersion = "20.5.0"
    const val playServicesAds = "21.5.0"
    const val firebaseFireStoreVersion = "24.6.0"
    const val googleServiceVersion = "4.3.15"
    const val coroutines = "1.7.0"
    const val lifecycle_version = "2.6.1"
    const val fragment_version = "1.5.7"
    const val nav_version = "2.5.3"
    const val hilt_version = "2.46"
    const val hilt_version_compose = "1.0.0"
    const val room = "2.6.0-alpha01"
    const val glideVersion = "4.15.1"
    const val dataStoreVersion = "1.0.0"
    const val lottiVersion = "6.0.0"
    const val recyclerViewVersion = "1.3.0"
    const val recyclerViewSelectionVersion = "1.1.0"
    const val calendarVersion = "3.0.0"
    const val gsonVersion = "2.10.1"
    const val viewBindingVersion = "1.0.4"
    const val crashlyticsVersion = "2.9.5"
    const val browserVersion = "1.5.0"
    const val webKit_version = "1.6.1"
    const val palette_version = "28.0.0"
    const val splashScreenVersion = "1.0.1"
    const val review_version = "2.0.0"
    const val graphVersion = "v3.1.0"
    const val mediaVersion = "1.0.1"
    const val cryptoreVersion = "1.4.0"
    const val retrofit_version = "2.9.0"
    const val retrofit_json_version = "2.9.0"
    const val circleIndicatorVersion = "2.1.6"
    const val markdownViewVersion = "1.1.1"
}

object Deps {
    const val core = "androidx.core:core-ktx:1.10.0"
    const val appcompat = "androidx.appcompat:appcompat:1.6.1"
    const val material = "com.google.android.material:material:${Versions.material}"
    const val playstorecore = "com.google.android.play:core-ktx:${Versions.playstorecore}"

    const val composeActivity = "androidx.activity:activity-compose:1.7.1"
    const val composeboM = "androidx.compose:compose-bom:2023.05.00"
    const val composeUI = "androidx.compose.ui:ui"
    const val composeUiGraphics = "androidx.compose.ui:ui-graphics"
    const val composeUiToolingPreview = "androidx.compose.ui:ui-tooling-preview"
    const val composeMaterial3 = "androidx.compose.material3:material3"
    const val composeNavigation = "androidx.navigation:navigation-compose:2.6.0-beta01"
    const val activityCompose = "androidx.activity:activity-compose:1.7.1"
    const val composeViewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1"
    const val composeTooling = "androidx.compose.ui:ui-tooling"
    const val composeTestManifest = "androidx.compose.ui:ui-test-manifest"
    const val composeAdapter = "com.google.accompanist:accompanist-themeadapter-material3:0.30.1"

    const val firebaseBoM = "com.google.firebase:firebase-bom:32.0.0"
    const val firebaseFirestore = "com.google.firebase:firebase-firestore-ktx"
    const val firebaseMessaging = "com.google.firebase:firebase-messaging-ktx"
    const val firebaseAnalytics = "com.google.firebase:firebase-analytics-ktx"
    const val firebaseConfig = "com.google.firebase:firebase-config-ktx"
    const val firebaseAuth = "com.google.firebase:firebase-auth-ktx"
    const val firebaseAuthExclude = "play-services-safetynet"
    const val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics-ktx"
    const val playStoreAuth =
        "com.google.android.gms:play-services-auth:${Versions.playServicesAuthVersion}"

    const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle_version}"
    const val liveData = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle_version}"
    const val fragment = "androidx.fragment:fragment-ktx:${Versions.fragment_version}"
    const val navFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.nav_version}"
    const val navUi = "androidx.navigation:navigation-ui-ktx:${Versions.nav_version}"

    const val hilt = "com.google.dagger:hilt-android:${Versions.hilt_version}"
    const val hiltCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hilt_version}"
    const val hiltCompose ="androidx.hilt:hilt-navigation-compose:${Versions.hilt_version_compose}"

    const val webkit = "androidx.webkit:webkit:${Versions.webKit_version}"

    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val coroutinesAndroid =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"

    const val room = "androidx.room:room-ktx:${Versions.room}"
    const val annotationProcessor = "androidx.room:room-compiler:${Versions.room}"

    const val glide = "com.github.bumptech.glide:glide:${Versions.glideVersion}"

    const val dataStore = "androidx.datastore:datastore-preferences:${Versions.dataStoreVersion}"

    const val lottie = "com.airbnb.android:lottie:${Versions.lottiVersion}"

    const val gson = "com.google.code.gson:gson:${Versions.gsonVersion}"

    const val viewBinding = "com.github.yogacp:android-viewbinding:${Versions.viewBindingVersion}"

    const val customChrome = "androidx.browser:browser:${Versions.browserVersion}"

    const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.recyclerViewVersion}"
    const val recyclerViewSelection =
        "androidx.recyclerview:recyclerview-selection:${Versions.recyclerViewSelectionVersion}"

    const val palette = "com.android.support:palette-v7:${Versions.palette_version}"

    const val splashScreen = "androidx.core:core-splashscreen:${Versions.splashScreenVersion}"

    const val calendar = "com.github.sundeepk:compact-calendar-view:${Versions.calendarVersion}"

    const val graph = "com.github.PhilJay:MPAndroidChart:${Versions.graphVersion}"

    const val exoMedia3 = "androidx.media3:media3-exoplayer:${Versions.mediaVersion}"
    const val exoMedia3ui = "androidx.media3:media3-ui:${Versions.mediaVersion}"
    const val exoMedia3dash = "androidx.media3:media3-exoplayer-dash:${Versions.mediaVersion}"

    const val cryptore = "com.kazakago.cryptore:cryptore:${Versions.cryptoreVersion}"

    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit_version}"
    const val retrofitJson =
        "com.squareup.retrofit2:converter-gson:${Versions.retrofit_json_version}"
    const val retrofitScalars =
        "com.squareup.retrofit2:converter-scalars:${Versions.retrofit_json_version}"

    const val circleIndicator =
        "me.relex:circleindicator:${Versions.circleIndicatorVersion}"

    const val markDownView =
        "com.github.mukeshsolanki:MarkdownView-Android:${Versions.markdownViewVersion}"
}

object Modules {
    const val core = ":core"
    const val syllabus = ":syllabus"
}
