plugins {
    id("java-gradle-plugin")
    kotlin("jvm") version "1.2.50"
}

repositories {
    jcenter()
    google()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.android.tools.build:gradle:3.1.3")
    implementation("org.jetbrains.dokka:dokka-android-gradle-plugin:0.9.17")
}

gradlePlugin {
    plugins {
        create("androidArtifacts") {
            id = "guru.stefma.androidartifacts"
            implementationClass = "guru.stefma.androidartifacts.AndroidArtifactsPlugin"
        }
    }
}