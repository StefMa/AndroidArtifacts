import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.LibraryVariant
import com.android.build.gradle.tasks.LintGlobalTask
import com.android.builder.model.AndroidLibrary

buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.2.70")
    }
}

apply(plugin = "com.android.library")
apply(plugin = "org.jetbrains.kotlin.android")
apply<guru.stefma.androidartifacts.AndroidArtifactsPlugin>()

version = "0.0.1"
group = "guru.stefma.androidartifacts.consumer"
configure<guru.stefma.androidartifacts.ArtifactsExtension> {
    artifactId = "android-kotlin"
}

configure<LibraryExtension> {
    compileSdkVersion(28)
}

repositories {
    google()
    jcenter()
}

dependencies {
    "implementation"(kotlin("stdlib-jdk8:1.2.70"))

    // The following are "test" dependencies
    "api"("junit:junit:4.12")
    "implementation"("org.mockito:mockito-core:2.22.0")
    "runtimeOnly"("com.squareup.okio:okio:2.0.0")
    "compileOnly"("com.jakewharton.timber:timber:4.7.1")
}