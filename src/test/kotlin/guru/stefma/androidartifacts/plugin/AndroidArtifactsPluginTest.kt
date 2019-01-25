package guru.stefma.androidartifacts.plugin

import guru.stefma.androidartifacts.internal.AbstractTest
import guru.stefma.androidartifacts.internal.default
import guru.stefma.androidartifacts.internal.junit.AndroidBuildScript
import guru.stefma.androidartifacts.internal.junit.AndroidTempDirectory
import guru.stefma.androidartifacts.internal.junit.TempDir
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File

@ExtendWith(AndroidTempDirectory::class)
class AndroidArtifactsPluginTest : AbstractTest {

    @Test
    fun `test apply should generate tasks`(
            @TempDir tempDir: File,
            @AndroidBuildScript buildScript: File
    ) {
        withBuildScript(tempDir) {
            """
                plugins {
                    id "com.android.library"
                    id "guru.stefma.androidartifacts"
                }

                $androidExtensionDefaults

                group = "guru.stefma"
                version = "1.0"
                androidArtifact {
                    artifactId = "androidartifacts"
                }
            """
        }

        withGradleRunner(tempDir, args = arrayOf("tasks")) {
            output.assertContainDebugTasks()
            output.assertContainReleaseTasks()
            output.assertMavenPublicationTasks()
        }
    }

    private fun String.assertContainDebugTasks() =
            assertThat(this).contains("androidArtifactDebug", "androidArtifactDebugJavadoc", "androidArtifactDebugSources")

    private fun String.assertContainReleaseTasks() =
            assertThat(this).contains("androidArtifactRelease", "androidArtifactReleaseJavadoc", "androidArtifactReleaseSources")

    private fun String.assertMavenPublicationTasks() =
            assertThat(this).contains("generatePomFileForDebugAarPublication", "generatePomFileForReleaseAarPublication")

    @Test
    fun `test apply should generate pom correctly`(
            @TempDir tempDir: File,
            @AndroidBuildScript buildScript: File
    ) {
        withBuildScript(tempDir) {
            """
                plugins {
                    id "com.android.library"
                    id "guru.stefma.androidartifacts"
                }

                $androidExtensionDefaults

                group = "guru.stefma"
                version = "1.0"
                androidArtifact {
                    artifactId = "androidartifacts"
                }

                dependencies {
                    implementation("guru.stefma.androidartifacts:androidartifacts:1.0.0")
                    api("guru.stefma.artifactorypublish:artifactorypublish:1.0.0")
                    compileOnly("guru.stefma.bintrayrelease:bintrayrelease:1.0.0")
                }
            """
        }

        withGradleRunner(tempDir, args = arrayOf("generatePomFileForReleaseAarPublication")) {
            val pomFile = File(tempDir, "/build/publications/releaseAar/pom-default.xml")
            pomFile.assertContainsAndroidArtifactsDependency()
            pomFile.assertContainsArtifactoryPublishDependency()
            pomFile.assertNotContainBintrayReleaseDependency()
        }
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
    fun `test exclusion of dependencies`(
            @TempDir tempDir: File,
            @AndroidBuildScript buildScript: File
    ) {
        withBuildScript(tempDir) {
            """
                plugins {
                    id "com.android.library"
                    id "guru.stefma.androidartifacts"
                }

                $androidExtensionDefaults

                group = "guru.stefma"
                version = "1.0"
                androidArtifact {
                    artifactId = "androidartifacts"
                }

                dependencies {
                    implementation("guru.stefma.androidartifacts:androidartifacts:1.0.0") {
                        exclude group: "org.jetbrains.dokka" // exclude both dokka dependencies
                        exclude module: "kotlin-stdlib-jdk8" // exclude kotlin jvm
                    }
                    api("guru.stefma.artifactorypublish:artifactorypublish:1.0.0") {
                        exclude group: "org.jfrog.buildinfo", module: "build-info-extractor-gradle"
                    }
                }
            """
        }

        withGradleRunner(tempDir, args = arrayOf("generatePomFileForReleaseAarPublication")) {
            val pomFile = File(tempDir, "/build/publications/releaseAar/pom-default.xml")
            pomFile.assertContainsAndroidArtifactsWithExclusionsDependency()
            pomFile.assertContainsArtifactoryPublishWithExclusionsDependency()
        }
    }

    private fun File.assertContainsAndroidArtifactsWithExclusionsDependency() =
            assertThat(readText()).contains(
                    """    <dependency>
      <groupId>guru.stefma.artifactorypublish</groupId>
      <artifactId>artifactorypublish</artifactId>
      <version>1.0.0</version>
      <scope>compile</scope>
      <exclusions>
        <exclusion>
          <groupId>org.jfrog.buildinfo</groupId>
          <artifactId>build-info-extractor-gradle</artifactId>
        </exclusion>
      </exclusions>
    </dependency>"""
            )

    private fun File.assertContainsArtifactoryPublishWithExclusionsDependency() =
            assertThat(readText()).contains(
                    """    <dependency>
      <groupId>guru.stefma.androidartifacts</groupId>
      <artifactId>androidartifacts</artifactId>
      <version>1.0.0</version>
      <scope>runtime</scope>
      <exclusions>
        <exclusion>
          <groupId>org.jetbrains.dokka</groupId>
          <artifactId>*</artifactId>
        </exclusion>
        <exclusion>
          <groupId>*</groupId>
          <artifactId>kotlin-stdlib-jdk8</artifactId>
        </exclusion>
      </exclusions>
    </dependency>"""
            )

    @Test
    fun `test apply with unknown dependencies should ignore it in pom`(
            @TempDir tempDir: File,
            @AndroidBuildScript buildScript: File
    ) {
        withBuildScript(tempDir) {
            """
                plugins {
                    id "com.android.library"
                    id "guru.stefma.androidartifacts"
                }

                $androidExtensionDefaults

                group = "guru.stefma"
                version = "1.0"
                androidArtifact {
                    artifactId = "androidartifacts"
                }

                dependencies {
                    implementation(fileTree(include: ['*.jar'], dir: 'libs'))
                }
            """
        }

        withGradleRunner(tempDir, args = arrayOf("generatePomFileForReleaseAarPublication", "-i")) {
            val pomFile = File(tempDir, "/build/publications/releaseAar/pom-default.xml")
            // Empty dependencies tag
            assertThat(pomFile.readText()).contains("<dependencies/>")
            assertThat(output)
                    .contains("One of your dependency has either: 'no group', 'no version' or 'no artifactId")
        }
    }

    @ParameterizedTest(
            name = "test name desc and url in pom with Gradle version {arguments}"
    )
    @ValueSource(strings = ["4.7", "4.8"])
    fun `test name desc and url in pom`(
            gradleVersion: String,
            @TempDir tempDir: File,
            @AndroidBuildScript buildScript: File
    ) {
        withBuildScript(tempDir) {
            """
                plugins {
                    id "com.android.library"
                    id "guru.stefma.androidartifacts"
                }

                $androidExtensionDefaults

                group = "guru.stefma"
                version = "1.0"
                androidArtifact {
                    artifactId = "androidartifacts"

                    name = "AndroidFacts"
                    description = "An awesome Gradle plugin for Android publishing"
                    url = "https://github.com/StefMa/AndroidArtifacts"
                }
            """
        }

        withSettingsScript(tempDir) {
            """
                pluginManagement {
                    repositories {
                        mavenLocal()
                        google()
                        jcenter()
                    }
                    resolutionStrategy {
                        eachPlugin {
                            if (requested.id.id.startsWith("com.android")) {
                                useModule("com.android.tools.build:gradle:3.2.1")
                            }
                            if (requested.id.id.startsWith("guru.stefma")) {
                                useModule("guru.stefma.androidartifacts:androidartifacts:${System.getProperty("pluginVersion")}")
                            }
                        }
                    }
                }
            """
        }

        withGradleRunner(
                tempDir,
                gradleVersion = gradleVersion,
                pluginClasspath = false,
                args = arrayOf("generatePomFileForReleaseAarPublication")
        ) {
            val pomFile = File(tempDir, "/build/publications/releaseAar/pom-default.xml")
            assertThat(pomFile.readText()).contains("<name>AndroidFacts</name>")
            assertThat(pomFile.readText()).contains("<description>An awesome Gradle plugin for Android publishing</description>")
            assertThat(pomFile.readText()).contains("<url>https://github.com/StefMa/AndroidArtifacts</url>")
        }
    }

    @Test
    fun `test apply with license should generate pom correctly for Gradle version 4dot8 (and up)`(
            @TempDir tempDir: File,
            @AndroidBuildScript buildScript: File
    ) {
        withBuildScript(tempDir) {
            """
                plugins {
                    id "com.android.library"
                    id "guru.stefma.androidartifacts"
                }

                $androidExtensionDefaults

                group = "guru.stefma"
                version = "1.0"
                androidArtifact {
                    artifactId = "androidartifacts"

                    license {
                        name = "Apache License, Version 2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                        distribution = "repo"
                        comments = "A business-friendly OSS license"
                    }
                }
            """
        }

        withSettingsScript(tempDir) {
            """
                pluginManagement {
                    repositories {
                        mavenLocal()
                        google()
                        jcenter()
                    }
                    resolutionStrategy {
                        eachPlugin {
                            if (requested.id.id.startsWith("com.android")) {
                                useModule("com.android.tools.build:gradle:3.2.1")
                            }
                            if (requested.id.id.startsWith("guru.stefma")) {
                                useModule("guru.stefma.androidartifacts:androidartifacts:${System.getProperty("pluginVersion")}")
                            }
                        }
                    }
                }
            """
        }

        withGradleRunner(
                tempDir,
                gradleVersion = "4.8",
                pluginClasspath = false,
                args = arrayOf("generatePomFileForReleaseAarPublication")
        ) {
            val pomFile = File(tempDir, "/build/publications/releaseAar/pom-default.xml")
            pomFile.assertContainsLicenses()
        }
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
            @AndroidBuildScript buildScript: File
    ) {
        withBuildScript(tempDir) {
            """
                plugins {
                    id "com.android.library"
                    id "guru.stefma.androidartifacts"
                }

                $androidExtensionDefaults

                group = "guru.stefma"
                version = "1.0"
                androidArtifact {
                    artifactId = "androidartifacts"

                    license {
                        name = "Apache License, Version 2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                        distribution = "repo"
                        comments = "A business-friendly OSS license"
                    }
                }
            """
        }

        withSettingsScript(tempDir) {
            """
                pluginManagement {
                    repositories {
                        mavenLocal()
                        google()
                        jcenter()
                    }
                    resolutionStrategy {
                        eachPlugin {
                            if (requested.id.id.startsWith("com.android")) {
                                useModule("com.android.tools.build:gradle:3.2.1")
                            }
                            if (requested.id.id.startsWith("guru.stefma")) {
                                useModule("guru.stefma.androidartifacts:androidartifacts:${System.getProperty("pluginVersion")}")
                            }
                        }
                    }
                }
            """
        }

        withGradleRunner(
                tempDir,
                gradleVersion = "4.7",
                pluginClasspath = false,
                args = arrayOf("generatePomFileForReleaseAarPublication")
        ) {
            val pomFile = File(tempDir, "/build/publications/releaseAar/pom-default.xml")
            pomFile.assertDoesNotContainLicenses()
        }
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
            @AndroidBuildScript buildScript: File
    ) {
        withBuildScript(tempDir) {
            """
                plugins {
                    id "com.android.library"
                    id "guru.stefma.androidartifacts"
                }

                $androidExtensionDefaults

                group = "guru.stefma"
                version = "1.0"
                androidArtifact {
                    artifactId = "androidartifacts"
                }

                dependencies {
                    implementation(project(":awesome"))
                }
            """
        }

        withSettingsScript(tempDir) {
            """
                include(":awesome")
            """
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

        withGradleRunner(tempDir, args = arrayOf("generatePomFileForReleaseAarPublication")) {
            val pomFile = File(tempDir, "/build/publications/releaseAar/pom-default.xml")
            pomFile.assertContainsProjectAwesomeDependency()
        }
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
    fun `test task androidArtifactRelease should generate jars`(
            gradleVersion: String,
            @TempDir tempDir: File,
            @AndroidBuildScript buildScript: File
    ) {
        withBuildScript(tempDir) {
            """
                plugins {
                    id "com.android.library"
                    id "guru.stefma.androidartifacts"
                }

                $androidExtensionDefaults

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
        }

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

        val agpVersion = when {
            listOf("4.4", "4.5", "4.5.1").contains(gradleVersion) -> "3.1.0"
            "4.10.2" == gradleVersion -> "3.3.0"
            else -> "3.2.1"
        }
        withSettingsScript(tempDir) {
            """
                pluginManagement {
                    repositories {
                        mavenLocal()
                        google()
                        jcenter()
                    }
                    resolutionStrategy {
                        eachPlugin {
                            if (requested.id.id.startsWith("com.android")) {
                                useModule("com.android.tools.build:gradle:$agpVersion")
                            }
                            if (requested.id.id.startsWith("guru.stefma")) {
                                useModule("guru.stefma.androidartifacts:androidartifacts:${System.getProperty("pluginVersion")}")
                            }
                        }
                    }
                }
            """
        }

        withGradleRunner(
                tempDir,
                pluginClasspath = false,
                gradleVersion = gradleVersion,
                args = arrayOf("androidArtifactRelease")
        ) {
            assertThat(File(tempDir, "/build/outputs/aar/${tempDir.name}-release.aar")).exists()
            assertThat(File(tempDir, "/build/libs/${tempDir.name}-1.0-sources.jar")).exists()
            assertThat(File(tempDir, "/build/libs/${tempDir.name}-1.0-javadocs.jar")).exists()
        }
    }

    @ParameterizedTest(
            name = "test task androidArtifactRelease without sources, javadoc with Gradle version {arguments}"
    )
    @ValueSource(strings = ["4.4", "4.5", "4.5.1", "4.6", "4.7", "4.8", "4.8.1", "4.9", "4.10.2"])
    fun `test task androidArtifactRelease without sources, javadoc`(
            gradleVersion: String,
            @TempDir tempDir: File,
            @AndroidBuildScript buildScript: File
    ) {
        withBuildScript(tempDir) {
            """
                plugins {
                    id "com.android.library"
                    id "guru.stefma.androidartifacts"
                }

                $androidExtensionDefaults

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
        }

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

        val agpVersion = when {
            listOf("4.4", "4.5", "4.5.1").contains(gradleVersion) -> "3.1.0"
            "4.10.2" == gradleVersion -> "3.3.0"
            else -> "3.2.1"
        }
        withSettingsScript(tempDir) {
            """
                pluginManagement {
                    repositories {
                        mavenLocal()
                        google()
                        jcenter()
                    }
                    resolutionStrategy {
                        eachPlugin {
                            if (requested.id.id.startsWith("com.android")) {
                                useModule("com.android.tools.build:gradle:$agpVersion")
                            }
                            if (requested.id.id.startsWith("guru.stefma")) {
                                useModule("guru.stefma.androidartifacts:androidartifacts:${System.getProperty("pluginVersion")}")
                            }
                        }
                    }
                }
            """
        }

        withGradleRunner(
                tempDir,
                pluginClasspath = false,
                gradleVersion = gradleVersion,
                args = arrayOf("androidArtifactRelease")
        ) {
            assertThat(File(tempDir, "/build/outputs/aar/${tempDir.name}-release.aar")).exists()
            assertThat(File(tempDir, "/build/libs/").listFiles()).isNull()
        }
    }

    @Test
    fun `test AGP 3dot2dot1 should not print warning with Gradle version 4dot10dot2`(
            @TempDir tempDir: File,
            @AndroidBuildScript buildScript: File
    ) {
        withBuildScript(tempDir) {
            """
                plugins {
                    id "com.android.library"
                    id "guru.stefma.androidartifacts"
                }

                $androidExtensionDefaults

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
        }

        withSettingsScript(tempDir) {
            """
                pluginManagement {
                    repositories {
                        mavenLocal()
                        google()
                        jcenter()
                    }
                    resolutionStrategy {
                        eachPlugin {
                            if (requested.id.id.startsWith("com.android")) {
                                useModule("com.android.tools.build:gradle:3.2.1")
                            }
                            if (requested.id.id.startsWith("guru.stefma")) {
                                useModule("guru.stefma.androidartifacts:androidartifacts:${System.getProperty("pluginVersion")}")
                            }
                        }
                    }
                }
            """
        }

        withGradleRunner(
                tempDir,
                gradleVersion = "4.10.2",
                pluginClasspath = false,
                args = arrayOf("androidArtifactRelease")
        ) {
            assertThat(output).doesNotContain("API 'variant.getJavaCompiler()' is obsolete")
        }
    }

    @Test
    fun `test AGP 3dot3dot0 should not print warning with Gradle version 4dot10dot2`(
            @TempDir tempDir: File,
            @AndroidBuildScript buildScript: File
    ) {
        withBuildScript(tempDir) {
            """
                plugins {
                    id "com.android.library"
                    id "guru.stefma.androidartifacts"
                }

                $androidExtensionDefaults

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
        }

        withGradleRunner(
                tempDir,
                gradleVersion = "4.10.2",
                args = arrayOf("androidArtifactRelease")
        ) {
            assertThat(output).doesNotContain("API 'variant.getJavaCompiler()' is obsolete")
        }
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
