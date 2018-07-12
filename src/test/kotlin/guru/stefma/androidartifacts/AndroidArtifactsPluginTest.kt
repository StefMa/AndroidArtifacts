package guru.stefma.androidartifacts

import guru.stefma.androidartifacts.junit.AndroidBuildScript
import guru.stefma.androidartifacts.junit.AndroidTempDirectory
import guru.stefma.androidartifacts.junit.TempDir
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File

@ExtendWith(AndroidTempDirectory::class)
class AndroidArtifactsPluginTest {


    @Test
    fun `test apply should generate tasks`(@TempDir tempDir: File, @AndroidBuildScript buildScript: File) {
        buildScript.appendText(
                """
                        group = "guru.stefma"
                        version = "1.0"
                        androidArtifact {
                            artifactId = "androidartifacts"
                        }

                """
        )

        val buildResult = GradleRunner.create()
                .default(tempDir)
                .withArguments("tasks")
                .build()

        buildResult.output.assertContainDebugTasks()
        buildResult.output.assertContainReleaseTasks()
        buildResult.output.assertMavenPublicationTasks()
    }

    private fun String.assertContainDebugTasks() =
            assertThat(this).contains("androidArtifactDebug", "androidArtifactDebugJavadoc", "androidArtifactDebugSources")

    private fun String.assertContainReleaseTasks() =
            assertThat(this).contains("androidArtifactRelease", "androidArtifactReleaseJavadoc", "androidArtifactReleaseSources")

    private fun String.assertMavenPublicationTasks() =
            assertThat(this).contains("generatePomFileForDebugAarPublication", "generatePomFileForReleaseAarPublication")

    @Test
    fun `test apply should generate pom correctly`(@TempDir tempDir: File, @AndroidBuildScript buildScript: File) {
        buildScript.appendText(
                """
                        group = "guru.stefma"
                        version = "1.0"
                        androidArtifact {
                            artifactId = "androidartifacts"
                        }

                        repositories {
                            jcenter()
                        }

                        dependencies {
                            implementation("guru.stefma.androidartifacts:androidartifacts:1.0.0")
                            api("guru.stefma.artifactorypublish:artifactorypublish:1.0.0")
                            compileOnly("guru.stefma.bintrayrelease:bintrayrelease:1.0.0")
                        }
                """
        )

        GradleRunner.create()
                .default(tempDir)
                .withArguments("generatePomFileForReleaseAarPublication")
                .build()

        val pomFile = File(tempDir, "/build/publications/releaseAar/pom-default.xml")
        pomFile.assertContainsAndroidArtifactsDependency()
        pomFile.assertContainsArtifactoryPublishDependency()
        // This is not yet supported. See https://github.com/StefMa/AndroidArtifacts/issues/19
        // pomFile.assertContainsBintrayReleaseDependency()
    }

    private fun File.assertContainsAndroidArtifactsDependency() =
            assertThat(readText()).contains(
                    "<dependency>",
                    "<groupId>guru.stefma.androidartifacts</groupId>",
                    "<artifactId>androidartifacts</artifactId>",
                    "<version>1.0.0</version>",
                    "<scope>compile</scope>",
                    "</dependency>"
            )

    private fun File.assertContainsArtifactoryPublishDependency() =
            assertThat(readText()).contains(
                    "<dependency>",
                    "<groupId>guru.stefma.artifactorypublish</groupId>",
                    "<artifactId>artifactorypublish</artifactId>",
                    "<version>1.0.0</version>",
                    "<scope>compile</scope>",
                    "</dependency>"
            )

    private fun File.assertContainsBintrayReleaseDependency() =
            assertThat(readText()).contains(
                    "<dependency>",
                    "<groupId>guru.stefma.bintrayrelease</groupId>",
                    "<artifactId>bintrayrelease</artifactId>",
                    "<version>1.0.0</version>",
                    "<scope>provided</scope>",
                    "</dependency>"
            )

    @Test
    fun `test task androidArtifactRelease should generate jars`(@TempDir tempDir: File, @AndroidBuildScript buildScript: File) {
        buildScript.appendText(
                """
                        group = "guru.stefma"
                        version = "1.0"
                        androidArtifact {
                            artifactId = "androidartifacts"
                        }

                        repositories {
                            jcenter()
                            google()
                        }
                """
        )
        File(tempDir, "src/main/java/TestFile.java").apply {
            parentFile.mkdirs()
            writeText(
                    """
                        /**
                        * Just a simple Java TestFile
                        */
                        class TestFile {}
                    """
            )
        }

        GradleRunner.create()
                .default(tempDir)
                .withArguments("androidArtifactRelease")
                .build()

        assertThat(File(tempDir, "/build/outputs/aar/${tempDir.name}-release.aar")).exists()
        // TODO: Check why they aren't named like we describe in the development.
        // Should be $tempDir.name-variantName-sources according to the doc
        assertThat(File(tempDir, "/build/libs/${tempDir.name}-1.0-sources.jar")).exists()
        assertThat(File(tempDir, "/build/libs/${tempDir.name}-1.0-javadocs.jar")).exists()
    }

    @Test
    fun `test task androidArtifactRelease without sources, javadoc`(@TempDir tempDir: File, @AndroidBuildScript buildScript: File) {
        buildScript.appendText(
                """
                        group = "guru.stefma"
                        version = "1.0"
                        androidArtifact {
                            artifactId = "androidartifacts"
                            javadoc = false
                            sources = false
                        }

                        repositories {
                            jcenter()
                            google()
                        }
                """
        )
        File(tempDir, "src/main/java/TestFile.java").apply {
            parentFile.mkdirs()
            writeText(
                    """
                        /**
                        * Just a simple Java TestFile
                        */
                        class TestFile {}
                    """
            )
        }

        GradleRunner.create()
                .default(tempDir)
                .withArguments("androidArtifactRelease")
                .build()

        assertThat(File(tempDir, "/build/outputs/aar/${tempDir.name}-release.aar")).exists()
        assertThat(File(tempDir, "/build/libs/").listFiles()).isNull()
    }

    @Disabled("It currently not working somehow... ")
    @Test
    fun `test apply kotlin should create dokka task`(@TempDir tempDir: File, @AndroidBuildScript buildScript: File) {
        buildScript.writeText(
                """
                        plugins {
                            id "com.android.library" apply false
                            id "org.jetbrains.kotlin.android" version "1.2.50" apply false
                            id "guru.stefma.androidartifacts" apply false
                        }

                        apply plugin: "com.android.library"
                        apply plugin: "org.jetbrains.kotlin.android"
                        apply plugin: "guru.stefma.androidartifacts"

                        android {
                            compileSdkVersion 27
                            defaultConfig {
                                minSdkVersion 21
                                targetSdkVersion 28
                            }
                        }

                        group = "guru.stefma"
                        version = "1.0"
                        androidArtifact {
                            artifactId = "androidartifacts"
                        }
                """
        )

        val buildResult = GradleRunner.create()
                .default(tempDir)
                .withArguments("tasks")
                .build()

        assertThat(buildResult.output).contains("androidArtifactDebugDokka")
        assertThat(buildResult.output).contains("androidArtifactReleaseDokka")
    }
}
