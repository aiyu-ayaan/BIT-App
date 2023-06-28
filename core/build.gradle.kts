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

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.playstore.core)
    implementation(libs.play.services.ads)
    implementation(libs.play.services.fido)


    implementation(platform(libs.firebase.bom))
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.config.ktx)
    implementation(libs.firebase.auth.ktx) {
        exclude(module = "play-services-safetynet")
    }


    implementation(libs.lifecycle.liveData)


//    Hilt
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)
    implementation(libs.web.kit)

    implementation(libs.custom.chrome)

    // Coroutines
    implementation(libs.coroutines)
    implementation(libs.coroutines.android)


    implementation(libs.room.ktx)
    annotationProcessor(libs.room.annotation)
    kapt(libs.room.annotation)



    implementation(libs.glide)
    implementation(libs.gson)
    implementation(libs.data.store)

    implementation(libs.nav.fragment)
    implementation(libs.nav.ui)

    implementation(libs.cryptore)

    implementation(libs.retrofit)
    implementation(libs.retrofit.json)
    implementation(libs.retrofit.scalars)
}

kapt {
    correctErrorTypes =true
    useBuildCache =true
}