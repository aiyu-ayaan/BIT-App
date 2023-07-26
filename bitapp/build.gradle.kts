@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.atech.bit"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.atech.bit"
        minSdk = 24
        targetSdk = 33
        versionCode = 60
        versionName = "4.1.2 Patch 1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(project(mapOf("path" to ":bitapp:attendance")))
    implementation(project(mapOf("path" to ":bitapp:course")))
    implementation(project(mapOf("path" to ":bitapp:login")))
    implementation(project(":core"))
    implementation(project(":syllabus"))
    implementation(project(":theme"))


    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)

    implementation(libs.hilt.android)
    implementation(libs.playstore.core)
    kapt(libs.hilt.android.compiler)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    releaseImplementation(libs.firebase.crashlytics.ktx)


    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    implementation(libs.android.viewbinding)

    implementation(libs.markDownView)

    implementation(libs.lotti)

    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)

    implementation(libs.exoplayer.core)
    implementation(libs.exoplayer.ui)
    implementation(libs.exoplayer.dash)

    implementation(libs.graph)
    implementation(libs.glide)

    implementation(libs.palette)

    implementation(libs.core.splashscreen)
}

kapt {
    correctErrorTypes = true
}
hilt {
    enableAggregatingTask = true
}