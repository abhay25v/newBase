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
        // Uncomment and configure once Scandit license/credentials are available:
        // maven { url = uri("https://ssl.scandit.com/sdk/download/") }
    }
}

rootProject.name = "SahlaWarehouse"
include(":app")
