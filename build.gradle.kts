import java.net.URL
import guru.stefma.bintrayrelease.PublishExtension
import guru.stefma.buildsrc.CreateNowDockerfile
import guru.stefma.buildsrc.CreateNowEntrypointIndexHtml
import guru.stefma.buildsrc.CreateNowJson
import guru.stefma.buildsrc.MoveDokkaAndGradleSiteToNow
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    kotlin("jvm") version "1.2.70"

    id("org.jetbrains.dokka") version "0.9.17"
    id("com.github.gradle-guides.site") version "0.1"
    id("com.gradle.plugin-publish") version "0.10.0"
    `java-gradle-plugin`

    id("java-library")
    id("guru.stefma.bintrayrelease") version "1.0.0" apply false

    id("guru.stefma.androidartifacts.zeit")
}
apply(plugin = "guru.stefma.bintrayrelease")

group = "guru.stefma.androidartifacts"
version = "1.2.0"
description = "A Gradle Plugin which will easify the process to publish Android and Java artifacts to the local maven"
val githubSite = "https://github.com/StefMa/AndroidArtifacts"

repositories {
    jcenter()
    google()
}

val optionalPlugins by configurations.creating {
    configurations["compileOnly"].extendsFrom(this)
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.dokka:dokka-android-gradle-plugin:0.9.17")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:0.9.17")

    optionalPlugins("com.android.tools.build:gradle:3.1.4")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.3.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")
    testImplementation("org.assertj:assertj-core:3.11.1")
}

tasks.withType(Test::class.java) {
    useJUnitPlatform()
}

// This will add the android tools into the "test classpath"
tasks.withType<PluginUnderTestMetadata> {
    pluginClasspath.from(optionalPlugins)
}

tasks.withType<DokkaTask> {
    withGroovyBuilder {
        "externalDocumentationLink" {
            "setUrl"(URL("https://docs.gradle.org/${gradle.gradleVersion}/javadoc/"))
        }
    }
}

site {
    vcsUrl = githubSite
}

gradlePlugin {
    plugins {
        create("androidArtifacts") {
            id = "guru.stefma.androidartifacts"
            implementationClass = "guru.stefma.androidartifacts.plugin.AndroidArtifactsPlugin"
        }
        create("javaArtifacts") {
            id = "guru.stefma.javaartifacts"
            implementationClass = "guru.stefma.androidartifacts.plugin.JavaArtifactsPlugin"
        }
        create("umbrellaArtifacts") {
            id = "guru.stefma.artifacts"
            implementationClass = "guru.stefma.androidartifacts.plugin.UmbrellaArtifactsPlugin"
        }
    }
}

pluginBundle {
    website = githubSite
    vcsUrl = githubSite
    description = project.description
    tags = listOf("publish", "publishing", "maven", "mavenLocal", "android", "java", "kotlin")

    plugins {
        getByName("androidArtifacts") {
            displayName = "AndroidArtifacts"
        }
        getByName("javaArtifacts") {
            displayName = "JavaArtifacts"
        }
        getByName("umbrellaArtifacts") {
            displayName = "UmbrellaArtifacts"
            description = "An wrapper around the guru.stefma.androidartifacts and guru.stefma.javaartifacts plugin"
        }
    }
}

configure<PublishExtension> {
    artifactId = "androidartifacts"
    userOrg = "stefma"
    desc = description
    uploadName = "AndroidArtifacts"
    website = githubSite
}