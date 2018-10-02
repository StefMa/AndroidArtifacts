package guru.stefma.androidartifacts

import org.gradle.util.VersionNumber

/**
 * Compares the given [gradleVersion] with the given
 * Gradle versions in [betterThan] resp. [smallerThan].
 */
internal class GradleVersionComparator(
        private val gradleVersion: String
) {

    /**
     * Returns true when the [GradleVersionComparator.gradleVersion]
     * (from the constructor) is better (higher) than the given [gradleVersion].
     */
    fun betterThan(gradleVersion: String): Boolean {
        return VersionNumber.parse(this.gradleVersion) > VersionNumber.parse(gradleVersion)
    }

    /**
     * Returns true when the [GradleVersionComparator.gradleVersion]
     * (from the constructor) is smaller than the given [gradleVersion].
     */
    fun smallerThan(gradleVersion: String): Boolean {
        return VersionNumber.parse(this.gradleVersion) < VersionNumber.parse(gradleVersion)
    }

}