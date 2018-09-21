plugins {
    kotlin("jvm") version "1.2.70"
}
apply<guru.stefma.androidartifacts.plugin.JavaArtifactsPlugin>()

version = "0.0.1"
group = "guru.stefma.androidartifacts.consumer"
configure<guru.stefma.androidartifacts.ArtifactsExtension> {
    artifactId = "kotlin"
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // The following are "test" dependencies
    api("junit:junit:4.12")
    implementation("org.mockito:mockito-core:2.22.0")
    runtimeOnly("com.squareup.okio:okio:2.0.0")
    compileOnly("com.jakewharton.timber:timber:4.7.1")
}