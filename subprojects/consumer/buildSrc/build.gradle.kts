plugins {
    kotlin("jvm") version "1.2.70"
}

repositories {
    jcenter()
    google()
}

// We have to make sure that we are using the same dependencies as in our top-level project
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.2.70")
    implementation("com.android.tools.build:gradle:3.1.4")
    implementation("org.jetbrains.dokka:dokka-android-gradle-plugin:0.9.17")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:0.9.17")
}

kotlin.sourceSets.getByName("main").kotlin.srcDir("../../../src/main/kotlin")