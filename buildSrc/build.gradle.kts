plugins {
    kotlin("jvm") version "1.2.70"
    `java-gradle-plugin`
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

gradlePlugin {
    plugins {
        register("zeit") {
            id = "guru.stefma.androidartifacts.zeit"
            implementationClass = "guru.stefma.buildsrc.ZeitPlugin"
        }
    }
}