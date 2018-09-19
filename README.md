[![CircleCI](https://circleci.com/gh/StefMa/AndroidArtifacts.svg?style=svg)](https://circleci.com/gh/StefMa/AndroidArtifacts)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Download](https://api.bintray.com/packages/stefma/maven/AndroidArtifacts/images/download.svg)](https://bintray.com/stefma/maven/AndroidArtifacs/_latestVersion)
[![Dokka](https://img.shields.io/badge/Dokka--green.svg)](https://androidartifacts.now.sh/androidartifacts)
[![Gradle Site](https://img.shields.io/badge/Gradle_Site--green.svg)](https://androidartifacts.now.sh/gradleSite)

# AndroidArtifacts 
A super easy way to create Android and Java artifacts.

## Description
This is a simple helper for configure the `maven-publish` plugin.
It will create all the possible [`publications`](https://docs.gradle.org/current/userguide/publishing_maven.html#publishing_maven:publications) 
for your Android or Java project.

## Plugins
This project provides two different plugins.
The `guru.stefma.androidartifacts` & the `guru.stefma.javaartifacts` plugin.

Well, as the name reveals the first one should be used in **Android** projects
while the second one can be used in standalone **Java** projects.

The following describes the setup for the `androidartifacts` plugin.
But since it shares the same API with the `javaartifacts` the setup is quite similar.

For mor information checkout the [development documentation](DEVELOPMENT.md).

## How to apply
You can use it as a standalone plugin in the following way:

Put these lines into your **project** `build.gradle`
```groovy
buildscript {
    repositories {
        jcenter()
        google()
    }
    dependencies {
        // The current version can be found here https://git.io/vdsUY
        classpath "guru.stefma.androidartifacts:androidartifacts:$androidArtifactsVersion"
    }
}
```

Then you can apply the plugin to your **module** `build.gradle`:
```groovy
apply plugin: "com.android.library"
apply plugin: "org.jetbrains.kotlin-android" //1
apply plugin: "guru.stefma.androidartifacts" //2

version = "1.0.0"
group = "guru.stefma.androidartifacts"
androidArtifact {
    artifactId = 'androidartifacts'
}
```
* **//1:** The Kotlin plugin is optional of course. But if you add it, it will generate a KDoc together with a javadoc.
* **//2:** The **AndroidArtifacts** plugin should always be added **after** android library and kotlin-android plugin.

The plugin will automatically create some tasks - based on your setup - for you. 
Just run `./gradlew tasks` to see a list of them. 
All generated tasks are "prefixed" with `androidArtifact`.

## Publish
To finally publish you library to your local maven just run of the available `androidArtfactAar*` task.

E.g. the following will publish the **release** build type:
```
./gradlew :awesomeLib:androidArtifactRelease
```
