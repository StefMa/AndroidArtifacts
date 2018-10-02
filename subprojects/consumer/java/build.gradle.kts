plugins {
    `java-library`
}
apply<guru.stefma.androidartifacts.plugin.JavaArtifactsPlugin>()

version = "0.0.1"
group = "guru.stefma.androidartifacts.consumer"
configure<guru.stefma.androidartifacts.ArtifactsExtension> {
    artifactId = "java"
    license {
        name = "Apache License, Version 2.0"
        url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
        distribution = "repo"
        comments = "A business-friendly OSS license"
    }
}

repositories {
    jcenter()
}

dependencies {
    api("junit:junit:4.12")
    implementation("org.mockito:mockito-core:2.22.0")
    runtimeOnly("com.squareup.okio:okio:2.0.0")
    compileOnly("com.jakewharton.timber:timber:4.7.1")
    implementation(fileTree(mapOf("include" to arrayOf("*.jar"), "dir" to "libs")))
}