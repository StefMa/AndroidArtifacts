package guru.stefma.androidartifacts.plugin

import guru.stefma.androidartifacts.default
import guru.stefma.androidartifacts.junit.JavaBuildScript
import guru.stefma.androidartifacts.junit.JavaTempDirectory
import guru.stefma.androidartifacts.junit.TempDir
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File

@ExtendWith(JavaTempDirectory::class)
class JavaArtifactsPluginTest {

    @Test
    fun `test apply should generate tasks`(@TempDir tempDir: File, @JavaBuildScript buildScript: File) {
        buildScript.appendText(
                """
                        group = "guru.stefma"
                        version = "1.0"
                        javaArtifact {
                            artifactId = "androidrtifacts"
                        }

                """
        )

        val buildResult = GradleRunner.create()
                .default(tempDir)
                .withArguments("tasks")
                .build()

        buildResult.output.assertContainArtifactsTasks()
        buildResult.output.assertMavenPublicationTasks()
    }

    private fun String.assertContainArtifactsTasks() =
            assertThat(this).contains("androidArtifactJava", "androidArtifactJavaJavadoc", "androidArtifactJavaSources")

    private fun String.assertMavenPublicationTasks() =
            assertThat(this).contains("generatePomFileForMavenPublication")

    @Test
    fun `test apply should generate pom correctly`(@TempDir tempDir: File, @JavaBuildScript buildScript: File) {
        buildScript.appendText(
                """
                        group = "guru.stefma"
                        version = "1.0"
                        javaArtifact {
                            artifactId = "androidartifacts"
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
                .withArguments("generatePomFileForMavenPublication")
                .build()

        val pomFile = File(tempDir, "/build/publications/maven/pom-default.xml")
        pomFile.assertContainsAndroidArtifactsDependency()
        pomFile.assertContainsArtifactoryPublishDependency()
        pomFile.assertNotContainBintrayReleaseDependency()
    }

    private fun File.assertContainsAndroidArtifactsDependency() =
            assertThat(readText()).contains(
                """    <dependency>
      <groupId>guru.stefma.androidartifacts</groupId>
      <artifactId>androidartifacts</artifactId>
      <version>1.0.0</version>
      <scope>runtime</scope>
    </dependency>"""
        )

    private fun File.assertContainsArtifactoryPublishDependency() =
            assertThat(readText()).contains(
                """    <dependency>
      <groupId>guru.stefma.artifactorypublish</groupId>
      <artifactId>artifactorypublish</artifactId>
      <version>1.0.0</version>
      <scope>compile</scope>
    </dependency>"""
            )

    private fun File.assertNotContainBintrayReleaseDependency() =
            assertThat(readText()).doesNotContain(
                    "<groupId>guru.stefma.bintrayrelease</groupId>",
                    "<artifactId>bintrayrelease</artifactId>",
                    "<scope>provided</scope>"
            )

    @Test
    fun `test apply with license should generate pom correctly for Gradle version 4dot8 (and up)`(
            @TempDir tempDir: File,
            @JavaBuildScript buildScript: File
    ) {
        buildScript.appendText(
                """
                        group = "guru.stefma"
                        version = "1.0"
                        javaArtifact {
                            artifactId = "androidartifacts"
                            license {
                                name = "Apache License, Version 2.0"
                                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                                distribution = "repo"
                                comments = "A business-friendly OSS license"
                            }
                        }
                """
        )

        GradleRunner.create()
                .default(tempDir, "4.8")
                .withArguments("generatePomFileForMavenPublication")
                .build()

        val pomFile = File(tempDir, "/build/publications/maven/pom-default.xml")
        pomFile.assertContainsLicenses()
    }

    private fun File.assertContainsLicenses() =
            assertThat(readText()).contains(
                    """  <licenses>
    <license>
      <name>Apache License, Version 2.0</name>
      <url>https://www.apache.org/licenses/LICENSE-2.0.txt</url>
      <distribution>repo</distribution>
      <comments>A business-friendly OSS license</comments>
    </license>
  </licenses>"""
            )

    @Test
    fun `test apply with license should ignore license in pom for Gradle version 4dot7 (and below)`(
            @TempDir tempDir: File,
            @JavaBuildScript buildScript: File
    ) {
        buildScript.appendText(
                """
                        group = "guru.stefma"
                        version = "1.0"
                        javaArtifact {
                            artifactId = "androidartifacts"
                            license {
                                name = "Apache License, Version 2.0"
                                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                                distribution = "repo"
                                comments = "A business-friendly OSS license"
                            }
                        }
                """
        )

        GradleRunner.create()
                .default(tempDir, "4.7")
                .withArguments("generatePomFileForMavenPublication")
                .build()

        val pomFile = File(tempDir, "/build/publications/maven/pom-default.xml")
        pomFile.assertDoesNotContainLicenses()
    }

    private fun File.assertDoesNotContainLicenses() =
            assertThat(readText()).doesNotContain(
                    "<name>Apache License, Version 2.0</name>",
                    " <url>https://www.apache.org/licenses/LICENSE-2.0.txt</url>",
                    "<distribution>repo</distribution>",
                    "<comments>A business-friendly OSS license</comments>"
            )

    @Test
    fun `test apply should generate pom for project correctly`(
            @TempDir tempDir: File,
            @JavaBuildScript buildScript: File
    ) {
        buildScript.appendText(
                """
                        group = "guru.stefma"
                        version = "1.0"
                        javaArtifact {
                            artifactId = "androidartifacts"
                        }

                        dependencies {
                            implementation(project(":awesome"))
                        }
                """
        )
        File(tempDir, "settings.gradle").apply {
            parentFile.mkdirs()
            writeText(
                    """
                       include(":awesome")
                    """
            )
        }
        File(tempDir, "awesome/build.gradle").apply {
            parentFile.mkdirs()
            writeText(
                    """
                        group = "guru.stefma"
                        version = "1.0"
                    """
            )
        }

        GradleRunner.create()
                .default(tempDir)
                .withArguments("generatePomFileForMavenPublication")
                .build()

        val pomFile = File(tempDir, "/build/publications/maven/pom-default.xml")
        pomFile.assertContainsProjectAwesomeDependency()
    }

    private fun File.assertContainsProjectAwesomeDependency() =
            assertThat(readText()).contains(
                    """    <dependency>
      <groupId>guru.stefma</groupId>
      <artifactId>awesome</artifactId>
      <version>1.0</version>
      <scope>runtime</scope>
    </dependency>"""
            )

    @ParameterizedTest(
            name = "test task androidArtifactRelease should generate jars with Gradle version {arguments}"
    )
    @ValueSource(strings = ["4.4", "4.5", "4.5.1", "4.6", "4.7", "4.8", "4.8.1", "4.9", "4.10.2"])
    fun `test task androidArtifactJava should generate jars`(
            gradleVersion: String,
            @TempDir tempDir: File,
            @JavaBuildScript buildScript: File
    ) {
        buildScript.appendText(
                """
                        group = "guru.stefma"
                        version = "1.0"
                        javaArtifact {
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
                .default(tempDir, gradleVersion)
                .withArguments("androidArtifactJava")
                .build()

        assertThat(File(tempDir, "/build/libs/${tempDir.name}-1.0.jar")).exists()
        assertThat(File(tempDir, "/build/libs/${tempDir.name}-1.0-sources.jar")).exists()
        assertThat(File(tempDir, "/build/libs/${tempDir.name}-1.0-javadocs.jar")).exists()
    }

    @ParameterizedTest(
            name = "test task androidArtifactRelease without sources, javadoc with Gradle version {arguments}"
    )
    @ValueSource(strings = ["4.4", "4.5", "4.5.1", "4.6", "4.7", "4.8", "4.8.1", "4.9", "4.10.2"])
    fun `test task androidArtifactJava without sources, javadoc`(
            gradleVersion: String,
            @TempDir tempDir: File,
            @JavaBuildScript buildScript: File
    ) {
        buildScript.appendText(
                """
                        group = "guru.stefma"
                        version = "1.0"
                        javaArtifact {
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
                .default(tempDir, gradleVersion)
                .withArguments("androidArtifactJava")
                .build()

        assertThat(File(tempDir, "/build/libs/${tempDir.name}-1.0.jar")).exists()
        assertThat(File(tempDir, "/build/libs/${tempDir.name}-1.0-sources.jar")).doesNotExist()
        assertThat(File(tempDir, "/build/libs/${tempDir.name}-1.0-javadocs.jar")).doesNotExist()
    }

    @Test
    fun `test apply kotlin should create dokka task`(@TempDir tempDir: File, @JavaBuildScript buildScript: File) {
        buildScript.writeText(
                """
                        plugins {
                            id "org.jetbrains.kotlin.jvm" version "1.2.50"
                            id "guru.stefma.javaartifacts"
                        }

                        group = "guru.stefma"
                        version = "1.0"
                        javaArtifact {
                            artifactId = "androidartifacts"
                        }
                """
        )

        val buildResult = GradleRunner.create()
                .default(tempDir)
                .withArguments("tasks")
                .build()

        assertThat(buildResult.output).contains("androidArtifactJavaKdoc")
        assertThat(buildResult.output).contains("androidArtifactJavaKdoc")
    }

}