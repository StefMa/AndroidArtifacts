import guru.stefma.bintrayrelease.PublishExtension

plugins {
    kotlin("jvm") version "1.2.50"

    id("org.jetbrains.dokka") version "0.9.17"
    id("com.github.gradle-guides.site") version "0.1"
    id("java-gradle-plugin")

    id("java-library")
    id("guru.stefma.bintrayrelease") version "1.0.0" apply false
}
apply(plugin = "guru.stefma.bintrayrelease")

repositories {
    jcenter()
    google()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.android.tools.build:gradle:3.1.3")
    implementation("org.jetbrains.dokka:dokka-android-gradle-plugin:0.9.17")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.2.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.2.0")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.2.0")
    testImplementation("org.assertj:assertj-core:3.10.0")
}

tasks.withType(Test::class.java) {
    useJUnitPlatform()
}

// This githubSite will also be used for the `guru.stefma.bintrayrelease` plugin
val githubSite = "https://github.com/StefMa/AndroidArtifacts"
site {
    vcsUrl = githubSite
}

gradlePlugin {
    plugins {
        create("androidArtifacts") {
            id = "guru.stefma.androidartifacts"
            implementationClass = "guru.stefma.androidartifacts.AndroidArtifactsPlugin"
        }
        create("javaArtifacts") {
            id = "guru.stefma.javaartifacts"
            implementationClass = "guru.stefma.androidartifacts.JavaArtifactsPlugin"
        }
    }
}

group = "guru.stefma.androidartifacts"
version = "1.1.1"
// The description will also be used for the `com.github.gradle-guides.site` plugin
description = "A Gradle Plugin which will easify the process to publish Android and Java artifacts to the local maven"
configure<PublishExtension> {
    artifactId = "androidartifacts"
    userOrg = "stefma"
    desc = description
    uploadName = "AndroidArtifacts"
    website = githubSite
}
