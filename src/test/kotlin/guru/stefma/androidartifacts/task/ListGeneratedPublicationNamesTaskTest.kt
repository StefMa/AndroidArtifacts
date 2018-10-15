package guru.stefma.androidartifacts.task

import guru.stefma.androidartifacts.internal.default
import guru.stefma.androidartifacts.internal.junit.*
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File

class ListGeneratedPublicationNamesTaskTest {

    @Test
    @ExtendWith(AndroidTempDirectory::class)
    fun `test generated publication task android should print generated publications`(
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

    @Test
    @ExtendWith(JavaTempDirectory::class)
    fun `test generated publication task java should print generated publications`(
            @TempDir tempDir: File,
            @JavaBuildScript buildScript: File
    ) {
        buildScript.appendText(
                """
                        group = "guru.stefma"
                        version = "1.0"
                        javaArtifact {
                            artifactId = "javaartifact"
                        }
                    """
        )

        val buildResult = GradleRunner.create()
                .default(tempDir)
                .withArguments("androidArtifactGeneratedPublications")
                .build()

        assertThat(buildResult.output).contains(
                "This is a list of all generated publications by the",
                "[maven]"
        )
    }
}