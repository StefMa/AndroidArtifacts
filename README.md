[![CircleCI](https://circleci.com/gh/StefMa/AndroidArtifacts.svg?style=svg)](https://circleci.com/gh/StefMa/AndroidArtifacts)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Download](https://api.bintray.com/packages/stefma/maven/AndroidArtifacts/images/download.svg)](https://bintray.com/stefma/maven/AndroidArtifacs/_latestVersion)
[![Dokka](https://img.shields.io/badge/Dokka-2E7D32.svg)](https://androidartifacts.now.sh/androidartifacts)
[![Gradle Site](https://img.shields.io/badge/Gradle_Site-2E7D32.svg)](https://androidartifacts.now.sh/gradleSite)

# AndroidArtifacts 
A super easy way to create Android and Java artifacts.

## Description
This is a helper to configure the [`maven-publish`](https://docs.gradle.org/current/userguide/publishing_maven.html) Gradle plugin.
It will create all "possible" [`publications`](https://docs.gradle.org/current/userguide/publishing_maven.html#publishing_maven:publications) 
for your Android, Java and Kotlin projects.

## Plugins
This project provides basically two plugins.
The `guru.stefma.androidartifacts` & the `guru.stefma.javaartifacts` plugin.

Well, as the name reveals the first one should be used in **Android** projects
while the second one has to be used in pure **Java** and/or **Kotlin** projects.

However. Because it is easier for consumers - like you - to don't handle multiple
plugins in the same environment (e.g. an Android App which has a pure Kotlin module)
there is an additional plugin **`guru.stefma.artifacts`**.

This plugin will take care of applying the **correct** plugin for the current
environment. So you don't have to decide if you should apply the `guru.stefma.androidartifacts`
or the `guru.stefma.javaartifacts` plugin.

For more information checkout the [development documentation](DEVELOPMENT.md).

## How to apply
To apply the plugin you have to setup your build scripts in the following way:

1. Put these lines into your **project** `build.gradle`
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

<details>
<summary><b>For the `master-SNAPSHOT` version click here</b></summary>

```groovy
buildscript {
    repositories {
        jcenter()
        google()
        maven { url "https://jitpack.io" }
    }
    dependencies {
        classpath "com.github.stefma:androidartifacts:master-SNAPSHOT"
    }
}
```    

Please note that this may be fail for the first attempts because [JitPack](https://jitpack.io)
build the plugin on the fly.
Please just try it again after some minutes until JitPack have build the plugin.

It can also happen that your current `master-SNAPSHOT` is locally outdated. 
If so just run `./gradlew --refresh-dependencies`.
This will force Gradle to update all dependencies **and plugins**.
</details>
<br>

2. Then you can apply the plugin to your **module** `build.gradle`:
```groovy
apply plugin: "com.android.library"
apply plugin: "org.jetbrains.kotlin.android" //1
apply plugin: "guru.stefma.artifacts" //2

version = "1.0.0"
group = "guru.stefma.androidartifacts"
androidArtifact { // 3
    artifactId = 'androidartifacts'
    pom { // 4
        
    }
}
```
* **//1:** The Kotlin plugin is optional for this plugin of course. But if you add it, the plugin will generate a KDoc.
* **//2:** The `guru.stefma.artifacts` plugin should always be added **after** the `com.android.library`  
and the `org.jetbrains.kotlin.android` plugin.
* **//3:** The extension is either named `androidArtifact` **or** `javaArtifact`. Depending on the environment.
* **//4:** Customize the POM file directly. See also [this doc](https://docs.gradle.org/current/dsl/org.gradle.api.publish.maven.MavenPom.html) for more.

## Tasks
The plugin will automatically create some tasks based on your (Android BuildType/Flavors) setup for you. 
Just run `./gradlew tasks` to see a list of them. 
All generated tasks are "prefixed" with `androidArtifact`.

## Publish
To finally publish you library to your local maven just run one of the available `androidArtfact[BuildType|Java]` task.

E.g. the following will publish the **release** build type in **Android** projects:
```
./gradlew androidArtifactRelease
```

This wil publish your Java/Kotlin project:
```
./gradlew androidArtifactJava
```
