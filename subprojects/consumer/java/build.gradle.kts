plugins {
    `java-library`
}
apply<guru.stefma.androidartifacts.JavaArtifactsPlugin>()

version = "0.0.1"
group = "guru.stefma.androidartifacts.consumer"
configure<guru.stefma.androidartifacts.ArtifactsExtension> {
    artifactId = "java"
}

repositories {
    jcenter()
}

dependencies {
    api("junit:junit:4.12")
    implementation("org.mockito:mockito-core:2.22.0")
    runtimeOnly("com.squareup.okio:okio:2.0.0")
    compileOnly("com.jakewharton.timber:timber:4.7.1")
}