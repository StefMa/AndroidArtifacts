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

## How to use
### Add to your project
To add the plugin you have to add the following dependency to your **project** `build.gradle` first:
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

### Apply the plugin
Then you are able to apply the plugin in each of your **module** `build.gradle` files:
```groovy
// Add depending plugins. See in the configuration section 👇 for more
apply plugin: "guru.stefma.artifacts"

version = "1.0.0"
group = "guru.stefma.androidartifacts"
androidArtifact {
    artifactId = 'androidartifacts'
}
```

### Configuration
#### Depending plugins
The `guru.stema.artifacts` plugin needs at least one of the following depending plugins to do their work. 
Otherwise the plugin does nothing:
* `com.android.library`
* `java-library`
* `kotlin`
* `org.jetbrains.kotlin.jvm`

Please note that the plugin should be always added **after** the depending plugins.
This limitation might change in the future.

#### Extension name
The name of the extension (`androidArtifact` in the sample above) depends on the **depending plugin**.
When the `com.android.library` plugin is added it is named `androidArtifact`. 
On pure Java or Kotlin projects it will be named `javaArtifact`.

#### Properties
The following properties are available in the extension:

| Name   |      Mandatory      |  Description |
|----------|---------------|------|
| artifactId | ✅ | The artifactId. This is the string after the first colon in a dependency. |
| sources | ❌ | Set to true will generate a *-sources.jar. Fallback is `true`. | 
| javadoc | ❌ | Set to true will generate a *-javadoc.jar. In Kotlin projects it will also generate a *-kdoc.jar. Fallback is `true`. | 
| name | ❌ | The name of your project. Fallback is [`Project.name`](https://docs.gradle.org/current/javadoc/org/gradle/api/Project.html#getName--). |
| url | ❌ | An URL to your project. Probably something like `https://github.com/$username/$project` |
| description | ❌ | A description about your project. Fallback is [`Project.description`](https://docs.gradle.org/current/javadoc/org/gradle/api/Project.html#getDescription--). |
| pom | ❌ | Call this **method** to customize the generated POM file. See also [this doc](https://docs.gradle.org/current/dsl/org.gradle.api.publish.maven.MavenPom.html).

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
