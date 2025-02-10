pluginManagement {
    repositories {
        repositories {
            google()
            mavenCentral()
            gradlePluginPortal()
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven("https://www.jitpack.io")
    }
}

rootProject.name = "My Application"
include(":app")
