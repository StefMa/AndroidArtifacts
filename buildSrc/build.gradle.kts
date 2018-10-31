plugins {
    kotlin("jvm") version "1.2.70"
    `java-gradle-plugin`
}

repositories {
    jcenter()
    maven(url = "https://plugins.gradle.org/m2/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // Dokka and gradle-site-plugin are for the DocsPlugin
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:0.9.17")
    implementation("gradle.plugin.com.github.gradle-guides:gradle-site-plugin:0.1")
}

gradlePlugin {
    plugins {
        register("docs") {
            id = "guru.stefma.androidartifacts.docs"
            implementationClass = "guru.stefma.buildsrc.DocsPlugin"
        }
        register("zeit") {
            id = "guru.stefma.androidartifacts.zeit"
            implementationClass = "guru.stefma.buildsrc.ZeitPlugin"
        }
    }
}