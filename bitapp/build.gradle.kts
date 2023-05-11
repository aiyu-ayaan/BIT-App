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

//    tasks.withType(KotlinCompile).configureEach {
//        kotlinOptions {
//            freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
//        }
//    }

}

dependencies {

    implementation(project(Modules.core))
    implementation(project(Modules.syllabus))


    implementation(Deps.core)
    implementation(Deps.appcompat)
    implementation(Deps.material)
    implementation(Deps.playstorecore)
    implementation(Deps.ads)

    implementation(Deps.composeActivity)
    implementation(platform(Deps.composeboM))
    implementation(Deps.composeUI)
    implementation(Deps.composeUiGraphics)
    implementation(Deps.composeUiToolingPreview)
    implementation(Deps.composeMaterial3)
    implementation(Deps.composeNavigation)
    implementation(Deps.composeViewModel)
    debugImplementation(Deps.composeTooling)
    debugImplementation(Deps.composeTestManifest)
    implementation(Deps.composeAdapter)

    implementation(platform(Deps.firebaseBoM))
    releaseImplementation(Deps.firebaseAnalytics)
    releaseImplementation(Deps.firebaseCrashlytics)
    implementation(Deps.firebaseFirestore)
    implementation(Deps.firebaseMessaging)
    implementation(Deps.firebaseConfig)
    implementation(Deps.firebaseAuth) {
        exclude(module = "play-services-safetynet")
    }

    implementation(Deps.playStoreAuth)

    implementation(Deps.viewModel)
    implementation(Deps.liveData)
    implementation(Deps.fragment)


    implementation(Deps.navFragment)
    implementation(Deps.navUi)

    implementation(Deps.hilt)
    implementation(Deps.hiltCompose)
    kapt(Deps.hiltCompiler)

    implementation(Deps.webkit)

    implementation(Deps.coroutines)
    implementation(Deps.coroutinesAndroid)


    implementation(Deps.room)
    kapt(Deps.annotationProcessor)


    implementation(Deps.glide)

    implementation(Deps.dataStore)

    implementation(Deps.lottie)

    implementation(Deps.gson)

    implementation(Deps.viewBinding)

    implementation(Deps.customChrome)

    implementation(Deps.recyclerView)
    implementation(Deps.recyclerViewSelection)

    implementation(Deps.palette)

    implementation(Deps.splashScreen)
    implementation(Deps.calendar)

    implementation(Deps.graph)

    implementation(Deps.exoMedia3)
    implementation(Deps.exoMedia3ui)
    implementation(Deps.exoMedia3dash)


    implementation(Deps.cryptore)
    implementation(Deps.retrofit)

    implementation(Deps.circleIndicator)

    implementation(Deps.markDownView)

}

kapt {
    correctErrorTypes = true
    useBuildCache = true
}