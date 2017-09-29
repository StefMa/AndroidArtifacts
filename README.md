# AndroidArtifacts

A super easy way to create Android and Java artifacts.

## Description
This is a simple helper for configure the `maven-publish` plugin. It will automatically setup the `publications` for your project.

## How to use it
You can use it as a standalone plugin in the following way:
Put these lines into your **project** `build.gradle`
```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'guru.stefma.androidartifacts:androidartifacts:0.0.2'
    }
}
```

Then put these into your **module** `build.gradle`:
```groovy
apply plugin: 'guru.stefma.androidartifacts' // Add this after your `com.android.library` or `java` plugin!

androidArtifacts {
    groupId = 'com.example'
    artifactId = 'androidartifacts'
    publishVersion = '0.1'
}
```

## Publish
To finally publish you lib (to your local maven) just run
```
./gradlew build :myLib:publishToMavenLocal
```

## Credits

Goes to [Novoda](https://github.com/novoda/) and there inital idea of there [bintray-release](https://github.com/novoda/bintray-release).
Basically this plugin have extracted some parts of the bintray-release with some changes by me. 
