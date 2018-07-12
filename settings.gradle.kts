pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "guru.stefma.bintrayrelease") {
                useModule("guru.stefma.bintrayrelease:bintrayrelease:${requested.version}")
            }
        }
    }
}

rootProject.name = "androidartifacts"
