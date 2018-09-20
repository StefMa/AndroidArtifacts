## AndroidArtifacts Consumer
This "standalone" project contains some consumers (or test-examples) for the **AndroidArtifacts** plugin.

You can test them by simply running the following Gradle tasks:
```
./gradlew androidArtifactJava androidArtifactRelease
```
The output can then be found in your local maven under
```
guru/stefma/androidartifacts/consumer/[CONSUMER_NAME]
```

### Consumers
#### Java
The java consumer is a java project which just apply the `java-library` plugin 
and the `guru.stefma.androidartifacts` plugin.

#### Kotlin
Equally to the **java consumer** - this is a standalone "kotlin-jvm" project.
It applies the `org.jetbrains.kotlin.jvm` plugin together with the `guru.stefma.androidartifacts` plugin.

#### Android
This "Java-Android" project applies the `com.android.library` plugin 
and the `guru.stefma.androidartifacts` plugin.

#### Android-Kotlin
An consumer which applies the same plugins as the **Android** consumer 
but also the `org.jetbrains.kotlin.android` plugin.

### CircleCI
The CI will publish each consumer and publish the local maven
to the **artifacts** page.
There you can see the output and check with previous created versions of the 
generated **jars** or **pom** files.

