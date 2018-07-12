package guru.stefma.androidartifacts

import org.gradle.testkit.runner.GradleRunner
import java.io.File

/**
 * Setup the [GradleRunner] with some "defaults" to reduce a little bit boilerplate...
 */
fun GradleRunner.default(
        projectDir: File,
        gradleVersion: String = "4.8.1"
) = this
        .withPluginClasspath()
        .withGradleVersion(gradleVersion)
        .withProjectDir(projectDir)
