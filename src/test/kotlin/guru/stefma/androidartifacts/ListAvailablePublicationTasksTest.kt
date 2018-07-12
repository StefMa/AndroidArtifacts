package guru.stefma.androidartifacts

import guru.stefma.androidartifacts.junit.AndroidBuildScript
import guru.stefma.androidartifacts.junit.AndroidTempDirectory
import guru.stefma.androidartifacts.junit.TempDir
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File

@ExtendWith(AndroidTempDirectory::class)
class ListAvailablePublicationTasksTest {

    @Test
    fun `test executing task should print generated publications`(
            @TempDir tempDir: File,
            @AndroidBuildScript buildScript: File
    ) {
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
                .withArguments("androidArtifactGeneratedPublications")
                .build()

        assertThat(buildResult.output).contains(
                "This is a list of all generated publications by the",
                "[debugAar, releaseAar]"
        )
    }
}