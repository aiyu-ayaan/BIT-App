plugins {
    id(PlugIns.androidApplication)
    id(PlugIns.kotlinAndroid)
    id(PlugIns.googleServices)
    kotlin(PlugIns.kotlinKapt)
    id(PlugIns.navigationSafeArgs)
    id(PlugIns.hilt)
    id(PlugIns.crashlytics)
    id(PlugIns.parcelize)
}

android {
    namespace = App.id
    compileSdk = AndroidSdk.compile

    defaultConfig {
        applicationId = App.id
        minSdk = AndroidSdk.min
        versionCode = App.versionCode
        versionName = App.versionName
        targetSdk = AndroidSdk.compile


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    bundle {
        storeArchive {
            enable = false
        }
    }

    flavorDimensions += "type"
//
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
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersionCompatibility.javaVersion
        targetCompatibility = JavaVersionCompatibility.javaVersion
    }
    kotlinOptions {
        jvmTarget = JavaVersionCompatibility.kotlinVersion
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }

    tasks.withType().configureEach {
        kotlinOptions {
            freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
        }
    }

}

dependencies {

    implementation(project(Modules.core))
    implementation(project(Modules.syllabus))


    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.playstore.core)

    implementation(libs.play.services.ads)
    implementation(libs.play.services.auth)

    implementation(libs.compose.activity)
    implementation(platform(libs.compose.boM))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.navigation)
    implementation(libs.compose.viewModel)
    debugImplementation(libs.compose.tooling)
    debugImplementation(libs.compose.testManifest)
    implementation(libs.compose.adapter)

    implementation(platform(libs.firebase.bom))
    releaseImplementation(libs.firebase.analytics.ktx)
    releaseImplementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.config.ktx)
    implementation(libs.firebase.auth.ktx) {
        exclude(module = "play-services-safetynet")
    }


    implementation(libs.lifecycle.viewModel)
    implementation(libs.lifecycle.liveData)
    implementation(libs.fragment)


    implementation(libs.nav.fragment)
    implementation(libs.nav.ui)

    implementation(libs.hilt)
    implementation(libs.hilt.compose)
    kapt(libs.hilt.compiler)

    implementation(libs.web.kit)

    implementation(libs.coroutines)
    implementation(libs.coroutines.android)


    implementation(libs.room.ktx)
    kapt(libs.room.annotation)


    implementation(libs.glide)

    implementation(libs.data.store)

    implementation(libs.lotti)

    implementation(libs.gson)

    implementation(libs.view.binding)

    implementation(libs.custom.chrome)

    implementation(libs.recycler.view)
    implementation(libs.recycler.view.selection)

    implementation(libs.palette)

    implementation(libs.splash.screen)
    implementation(libs.calender)

    implementation(libs.graph)

    implementation(libs.exoMedia3)
    implementation(libs.exoMedia3.ui)
    implementation(libs.exoMedia3.dash)


    implementation(libs.cryptore)
    implementation(libs.retrofit)

    implementation(libs.circle.indicator)

    implementation(libs.markDownView)

}

kapt {
    correctErrorTypes = true
    useBuildCache = true
}