pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "BIT App"
include(":bitapp")
include(":core")
include(":syllabus")
include(":bitapp:course")
include(":bitapp:attendance")
include(":bitapp:login")
include(":theme")
