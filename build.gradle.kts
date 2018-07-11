plugins {
    id("java-gradle-plugin")
    kotlin("jvm") version "1.2.50"
    id("maven-publish")
}

repositories {
    jcenter()
    google()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.android.tools.build:gradle:3.1.3")
    implementation("org.jetbrains.dokka:dokka-android-gradle-plugin:0.9.17")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.2.0")
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
    }
}

/**
 * For local development.
 * Just run
 * ````
 * ./gradlew publishPluginMavenPublicationToMavenLocal
 * ```
 * to publish this plugin to the local maven
 *
 * Can be used either via
 * ```
 *     plugins { id("guru.stefma.androidartifcts") }
 * ```
 * or with the old `buildscript` block
 * ```
 *     buildscript {
 *         repositories {
 *             google()
 *             jcenter()
 *             mavenLocal()
 *         }
 *         dependencies { classpath "guru.stefma.androidartifacts:androidartifacts:DEV" }
 *     }
 *
 *     apply<guru.stefma.androidartifacts.AndroidArtifactsPlugin>()
 * ```
 */
group = "guru.stefma.androidartifacts"
version = "1.0.0"