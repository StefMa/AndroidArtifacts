# AndroidArtifacts Development

## Tasks
The following tasks are available for a default Android library with a `release` and a `debug` build type:
* androidArtifactDebug
* androidArtifactDebugJavadoc
* androidArtifactDebugSources
* androidArtifactRelease
* androidArtifactReleaseJavadoc
* androidArtifactReleaseSources

If the `org.jetbrains.kotlin-android` plugin is applied there will be also a `androidArtifactDebugDokka` and `androidArtifactReleaseDokka` task.
But this is currently in "experimental mode".

### Tasks output
The output for the `androidArtifact{$libraryVariant}` is expected at `$project/build/generated/outputs/aar/$projectName-$variantName.aar`.
This is the default output path for the `assemble{$libraryVariant}` task.

The `*Javadoc` and `*Sources` task output will be created inside the `$project/build/libs/$projectName-$version-$type.jar` 
(while `$type` is either `javadoc` or `sources`).

The published aar and jar's can be then found in your local maven. Typicall this is located at `~/.m2/repository/`.

## KDoc support
As already mentioned above there is some **experimental** KDoc support.

If the plugin detect that the `org.jetbrains.kotlin-android` plugin is applied it will create belong to the `javadoc` task (and output)
a `dokka` task and output.
