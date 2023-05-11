plugins {
    id(PlugIns.androidLibrary)
    id(PlugIns.kotlinAndroid)
    kotlin(PlugIns.kotlinKapt)
    id(PlugIns.hilt)
    id(PlugIns.navigationSafeArgs)
    id(PlugIns.parcelize)
}

android {
    namespace = "com.atech.core"
    compileSdk = AndroidSdk.compile

    defaultConfig {
        minSdk = AndroidSdk.min

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true"
                )
            }
        }

    }
    sourceSets {
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }
    flavorDimensions +="type"

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
            isMinifyEnabled= true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersionCompatibility.javaVersion
        targetCompatibility = JavaVersionCompatibility.javaVersion
    }
    kotlinOptions {
        jvmTarget = JavaVersionCompatibility.kotlinVersion
    }
    tasks.withType().configureEach {
        kotlinOptions {
            freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
        }
    }
}

dependencies {

    implementation(Deps.core)
    implementation(Deps.appcompat)
    implementation(Deps.material)
    implementation(Deps.playstorecore)

    implementation(platform(Deps.firebaseBoM))
    implementation(Deps.firebaseAnalytics)
    implementation(Deps.firebaseFirestore)
    implementation(Deps.firebaseMessaging)
    implementation(Deps.firebaseConfig)
    implementation(Deps.firebaseAuth)
    releaseImplementation(Deps.firebaseCrashlytics)


    implementation(Deps.liveData)


//    Hilt
    implementation(Deps.hilt)
    kapt(Deps.hiltCompiler)
    implementation(Deps.webkit)
    implementation("com.google.android.gms:play-services-fido:20.0.1")

    implementation(Deps.customChrome)

    // Coroutines
    implementation(Deps.coroutines)
    implementation(Deps.coroutinesAndroid)


    implementation(Deps.room)
    annotationProcessor(Deps.annotationProcessor)
    kapt(Deps.annotationProcessor)


    implementation(Deps.glide)
    implementation(Deps.gson)
    implementation(Deps.dataStore)

    implementation(Deps.navFragment)
    implementation(Deps.navUi)

    implementation(Deps.cryptore)

    implementation(Deps.retrofit)
    implementation(Deps.retrofitJson)
    implementation(Deps.retrofitScalars)
}

kapt {
    correctErrorTypes =true
    useBuildCache =true
}