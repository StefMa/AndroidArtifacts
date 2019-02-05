package guru.stefma.androidartifacts.internal

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File

interface AbstractTest {

    val androidExtensionDefaults
        get() = """
                android {
                    compileSdkVersion 27
                    defaultConfig {
                        minSdkVersion 21
                        targetSdkVersion 28
                    }
                }
            """

    fun createAndroidManifest(projectDir: File) =
            File(projectDir, "/src/main/AndroidManifest.xml").apply {
                parentFile.mkdirs()
                writeText("<manifest package=\"guru.stefma.androidartifacts.test\"/>")
            }

    fun withBuildScript(projectDir: File, contentBlock: () -> String) =
            File(projectDir, "build.gradle").apply {
                parentFile.mkdirs()
                createNewFile()
                writeText(contentBlock())
            }

    fun withSettingsScript(projectDir: File, contentBlock: () -> String) =
            File(projectDir, "settings.gradle").apply {
                parentFile.mkdirs()
                createNewFile()
                writeText(contentBlock())
            }

    fun withGradleRunner(
            projectDir: File,
            pluginClasspath: Boolean = true,
            gradleVersion: String = "4.10.2",
            args: Array<String> = emptyArray(),
            fail: Boolean = false,
            block: BuildResult.() -> Unit
    ) {
        GradleRunner.create()
                .apply { if(pluginClasspath) withPluginClasspath() }
                .withGradleVersion(gradleVersion)
                .withProjectDir(projectDir)
                .withArguments(*args)
                .run { if (fail) buildAndFail() else build() }
                .also { block(it) }
    }

}