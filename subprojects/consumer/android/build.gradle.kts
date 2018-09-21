import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.LibraryVariant
import com.android.build.gradle.tasks.LintGlobalTask
import com.android.builder.model.AndroidLibrary

apply(plugin = "com.android.library")
apply<guru.stefma.androidartifacts.plugin.AndroidArtifactsPlugin>()

version = "0.0.1"
group = "guru.stefma.androidartifacts.consumer"
configure<guru.stefma.androidartifacts.ArtifactsExtension> {
    artifactId = "android"
}

configure<LibraryExtension> {
    compileSdkVersion(28)
}

repositories {
    google()
    jcenter()
}

dependencies {
    "api"("junit:junit:4.12")
    "implementation"("org.mockito:mockito-core:2.22.0")
    "runtimeOnly"("com.squareup.okio:okio:2.0.0")
    "compileOnly"("com.jakewharton.timber:timber:4.7.1")
}