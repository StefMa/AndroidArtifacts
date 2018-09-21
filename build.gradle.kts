import guru.stefma.bintrayrelease.PublishExtension
import guru.stefma.buildsrc.CreateNowDockerfile
import guru.stefma.buildsrc.CreateNowEntrypointIndexHtml
import guru.stefma.buildsrc.CreateNowJson
import guru.stefma.buildsrc.MoveDokkaAndGradleSiteToNow

plugins {
    kotlin("jvm") version "1.2.70"

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
    implementation("com.android.tools.build:gradle:3.1.4")
    implementation("org.jetbrains.dokka:dokka-android-gradle-plugin:0.9.17")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:0.9.17")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.3.1")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.3.1")
    testImplementation("org.assertj:assertj-core:3.11.1")
}

tasks.withType(Test::class.java) {
    useJUnitPlatform()
}

// This githubSite will also be used for the `guru.stefma.bintrayrelease` plugin
val githubSite = "https://github.com/StefMa/AndroidArtifacts"
site {
    vcsUrl = githubSite
}

// Section for preparing to publish the docs to now.sh
tasks.create("moveDocsToNow", MoveDokkaAndGradleSiteToNow::class.java) {
    dependsOn("dokka", "generateSite")
}
tasks.create("createNowDockerfile", CreateNowDockerfile::class.java)
tasks.create("createNowEntrypoint", CreateNowEntrypointIndexHtml::class.java)
tasks.create("createNowJson", CreateNowJson::class.java)

// This task requires a valid now-cli installation...
// Alternatively you can put a now token via gradle properties in.
tasks.create("publishDocsToNow") {
    dependsOn("moveDocsToNow", "createNowDockerfile", "createNowEntrypoint", "createNowJson")

    doLast {
        exec {
            workingDir("$buildDir/now")
            val token = findProperty("nowToken")
            if (token != null) {
                commandLine("now", "--public", "--token", token)
            } else {
                // Try to run without token...
                commandLine("now", "--public")
            }
        }
    }
}

tasks.create("createNowAlias") {
    dependsOn("publishDocsToNow")

    doLast {
        exec {
            workingDir("$buildDir/now")
            val token = findProperty("nowToken")
            if (token != null) {
                commandLine("now", "alias", "--token", token)
            } else {
                // Try to run without token...
                commandLine("now", "alias")
            }
        }
    }
}
// Section end

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