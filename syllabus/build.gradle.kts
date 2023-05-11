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
    implementation(Deps.core)
    implementation(Deps.appcompat)
    implementation(Deps.material)

    implementation(Deps.viewBinding)
    implementation(Deps.hilt)
    kapt(Deps.hiltCompiler)
}
kapt {
    correctErrorTypes= true
    useBuildCache =true
}