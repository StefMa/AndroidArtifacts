import guru.stefma.bintrayrelease.PublishExtension

plugins {
    id("java-gradle-plugin")
    kotlin("jvm") version "1.2.50"
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
version = "1.1.0"
configure<PublishExtension> {
    artifactId = "androidartifacts"
    userOrg = "stefma"
    desc = "A Gradle Plugin which will"
    uploadName = "AndroidArtifacts"
    website = "https://github.com/StefMa/AndroidArtifacts"
}