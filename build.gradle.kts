plugins {
    kotlin("jvm") version "1.3.21"

    id("com.gradle.plugin-publish") version "0.10.1"
    `java-gradle-plugin`

    id("guru.stefma.bintrayrelease") version "1.1.1"

    id("guru.stefma.androidartifacts.docs")
    id("guru.stefma.androidartifacts.zeit")
}

group = "guru.stefma.androidartifacts"
version = "1.4.0"
description = "A Gradle Plugin which will easify the process to publish Android and Java artifacts to the local maven"
val githubSite = "https://github.com/StefMa/AndroidArtifacts"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

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

    optionalPlugins("com.android.tools.build:gradle:3.3.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.4.0")
    testImplementation("org.assertj:assertj-core:3.11.1")
}

afterEvaluate {
    tasks.withType(Test::class.java).configureEach {
        dependsOn(tasks.named("publishToMavenLocal"))
        systemProperty("pluginVersion", version)

        useJUnitPlatform()
    }
}

// This will add the android tools into the "test classpath"
tasks.withType<PluginUnderTestMetadata> {
    pluginClasspath.from(optionalPlugins)
}

docs {
    vcsUrl = githubSite
    externalDocLinks.apply {
        add("https://docs.gradle.org/${gradle.gradleVersion}/javadoc/")
    }
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

// version and group are
// declared above
javaArtifact {
    artifactId = "androidartifacts"
}

publish {
    userOrg = "stefma"
    desc = description
    uploadName = "AndroidArtifacts"
    website = githubSite
}