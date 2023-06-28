plugins {
    id(PlugIns.androidLibrary)
    id(PlugIns.kotlinAndroid)
    id(PlugIns.hilt)
    kotlin(PlugIns.kotlinKapt)
}

android {
    namespace = "com.atech.syllabus"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(project(Modules.core))
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.view.binding)
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)
}
kapt {
    correctErrorTypes= true
    useBuildCache =true
}