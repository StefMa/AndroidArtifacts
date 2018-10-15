package guru.stefma.androidartifacts.plugin

import guru.stefma.androidartifacts.internal.AbstractTest
import guru.stefma.androidartifacts.internal.junit.TempDir
import guru.stefma.androidartifacts.internal.junit.TempDirectory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File

class UmbrellaArtifactsPluginTest : AbstractTest {

    @Test
    @ExtendWith(TempDirectory::class)
    fun `apply artifacts after java-library should generate tasks`(
            @TempDir tempDir: File
    ) {
        val buildScript = """
                            plugins {
                                id("java-library")
                                id("guru.stefma.artifacts")
                            }

                            group = "guru.stefma"
                            version = "1"
                            javaArtifact {
                                artifactId = "umbrella"
                            }
                        """
        withGradleRunner(tempDir, buildScript, "tasks") {
            output.assertContainJavaArtifactsTasks()
            output.assertMavenPublicationTask()
        }
    }

    @Test
    @ExtendWith(TempDirectory::class)
    fun `apply artifacts before java-library should generate tasks`(
            @TempDir tempDir: File
    ) {
        val buildScript = """
                            plugins {
                                id("guru.stefma.artifacts")
                                id("java-library")
                            }

                            group = "guru.stefma"
                            version = "1"
                            javaArtifact {
                                artifactId = "umbrella"
                            }
                        """

        withGradleRunner(tempDir, buildScript, "tasks") {
            output.assertContainJavaArtifactsTasks()
            output.assertMavenPublicationTask()
        }
    }

    @Test
    @ExtendWith(TempDirectory::class)
    fun `apply artifacts after kotlin-jvm should generate tasks`(
            @TempDir tempDir: File
    ) {
        val buildScript = """
                            plugins {
                                id("org.jetbrains.kotlin.jvm") version "1.2.71"
                                id("guru.stefma.artifacts")
                            }

                            group = "guru.stefma"
                            version = "1"
                            javaArtifact {
                                artifactId = "umbrella"
                            }
                        """
        withGradleRunner(tempDir, buildScript, "tasks") {
            output.assertContainJavaArtifactsTasks()
            assertThat(output).contains("androidArtifactJavaKdoc")
            output.assertMavenPublicationTask()
        }
    }

    @Test
    @ExtendWith(TempDirectory::class)
    fun `apply artifacts before kotlin-jvm should generate tasks`(
            @TempDir tempDir: File
    ) {
        val buildScript = """
                            plugins {
                                id("guru.stefma.artifacts")
                                id("org.jetbrains.kotlin.jvm") version "1.2.71"
                            }

                            group = "guru.stefma"
                            version = "1"
                            javaArtifact {
                                artifactId = "umbrella"
                            }
                        """
        withGradleRunner(tempDir, buildScript, "tasks") {
            output.assertContainJavaArtifactsTasks()
            assertThat(output).contains("androidArtifactJavaKdoc")
            output.assertMavenPublicationTask()
        }
    }

    private fun String.assertContainJavaArtifactsTasks() =
            assertThat(this).contains("androidArtifactJava", "androidArtifactJavaJavadoc", "androidArtifactJavaSources")

    private fun String.assertMavenPublicationTask() =
            assertThat(this).contains("generatePomFileForMavenPublication")

    @Test
    @ExtendWith(TempDirectory::class)
    fun `apply artifacts after android-library should generate tasks`(
            @TempDir tempDir: File
    ) {
        val buildScript = """
                            plugins {
                                id("com.android.library")
                                id("guru.stefma.artifacts")
                            }

                            android {
                                compileSdkVersion 27
                                defaultConfig {
                                    minSdkVersion 21
                                    targetSdkVersion 28
                                }
                            }

                            group = "guru.stefma"
                            version = "1"
                            androidArtifact {
                                artifactId = "umbrella"
                            }
                        """

        createAndroidManifest(tempDir)

        withGradleRunner(tempDir, buildScript, "tasks") {
            output.assertContainDebugArtifactsTasks()
            output.assertContainReleaseArtifactsTasks()
            output.assertDebugReleasePublicationTasks()
        }
    }

    @Test
    @ExtendWith(TempDirectory::class)
    fun `apply artifacts before android-library should generate tasks`(
            @TempDir tempDir: File
    ) {
        val buildScript = """
                            plugins {
                                id("guru.stefma.artifacts")
                                id("com.android.library")
                            }

                            android {
                                compileSdkVersion 27
                                defaultConfig {
                                    minSdkVersion 21
                                    targetSdkVersion 28
                                }
                            }

                            group = "guru.stefma"
                            version = "1"
                            androidArtifact {
                                artifactId = "umbrella"
                            }
                        """

        createAndroidManifest(tempDir)

        withGradleRunner(tempDir, buildScript, "tasks") {
            output.assertContainDebugArtifactsTasks()
            output.assertContainReleaseArtifactsTasks()
            output.assertDebugReleasePublicationTasks()
        }
    }

    private fun String.assertContainDebugArtifactsTasks() =
            assertThat(this).contains("androidArtifactDebug", "androidArtifactDebugJavadoc", "androidArtifactDebugSources")

    private fun String.assertContainReleaseArtifactsTasks() =
            assertThat(this).contains("androidArtifactRelease", "androidArtifactReleaseJavadoc", "androidArtifactReleaseSources")

    private fun String.assertDebugReleasePublicationTasks() =
            assertThat(this).contains("generatePomFileForDebugAarPublication", "generatePomFileForReleaseAarPublication")
}