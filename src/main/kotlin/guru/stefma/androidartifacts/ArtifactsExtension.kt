package guru.stefma.androidartifacts

import org.gradle.api.Action

open class ArtifactsExtension {

    /**
     * The artifactId for the artifact.
     */
    var artifactId: String? = null

    /**
     * Set to false when you don't want to publish
     * the sources of your artifact.
     *
     * Default is true.
     */
    var sources: Boolean = true

    /**
     * Set to false when you don't want to publish
     * the javadoc/kdoc of your artifact.
     *
     * Default is true.
     */
    var javadoc: Boolean = true

    internal var licenseSpec: LicenseSpec? = null

    /**
     * Set a license to the POM file.
     *
     * Default is null. Means there will be no <license>-Tag
     * inside the POM.
     */
    fun license(action: Action<LicenseSpec>) {
        licenseSpec = LicenseSpec()
        action.execute(licenseSpec!!)
    }
}

class LicenseSpec {

    /**
     * The name of the license.
     */
    var name: String? = null

    /**
     * The url of the license.
     */
    var url: String? = null

    /**
     * The distribution type where your artifact
     * will be mainly consumed.
     *
     * E.g. "repo" or "manually".
     * See also [https://maven.apache.org/pom.html#Licenses][https://maven.apache.org/pom.html#Licenses]
     */
    var distribution: String? = null

    /**
     * Some comments about why you have choosen this
     * license.
     *
     * E.g.
     * > A business-friendly OSS license
     */
    var comments: String? = null
}