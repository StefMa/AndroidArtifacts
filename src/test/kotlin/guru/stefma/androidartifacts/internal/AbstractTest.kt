package guru.stefma.androidartifacts.internal

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File

interface AbstractTest {

    fun createAndroidManifest(projectDir: File) =
            File(projectDir, "/src/main/AndroidManifest.xml").apply {
                parentFile.mkdirs()
                writeText("<manifest package=\"guru.stefma.androidartifacts.test\"/>")
            }

    fun withGradleRunner(
            projectDir: File,
            buildScriptContent: String,
            vararg args: String,
            fail: Boolean = false,
            block: BuildResult.() -> Unit
    ) {
        createBuildScript(projectDir, buildScriptContent)

        GradleRunner.create()
                .default(projectDir)
                .withArguments(*args)
                .run { if (fail) buildAndFail() else build() }
                .also { block(it) }
    }

    private fun createBuildScript(projectDir: File, content: String) =
            File(projectDir, "build.gradle").apply {
                parentFile.mkdirs()
                createNewFile()
                writeText(content)
            }
}