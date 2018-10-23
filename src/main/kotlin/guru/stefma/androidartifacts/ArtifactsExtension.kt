package guru.stefma.androidartifacts

import org.gradle.api.Action
import org.gradle.api.publish.maven.MavenPom

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

    /**
     * The human readable name of this artifact.
     *
     * This might differ from the [artifactId]. Example:
     * - name: Material Components for Android
     * - artifactId: (com.android.support:design)
     */
    var name: String? = null
    /**
     * The url of the project.
     *
     * This is a nice to have property and a nice gesture for projects users
     * that they know where the project lives.
     */
    var url: String? = null
    /**
     * A short description about this artifact
     *
     * What is it good for, how does it differ from other artifacts in the same group? Example
     * - artifactId: org.reactivestreams:reactive-streams
     * - description: A Protocol for Asynchronous Non-Blocking Data Sequence
     */
    var description: String? = null

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

    internal var customPomConfiguration: (Action<MavenPom>)? = null

    /**
     * Add additional field to the pom by using the [MavenPom] API
     *
     * ```
     * javaArtifact {
     *    artifactId = '$artifactId'
     *    pom {
     *        name = "Awesome library"
     *        description = "Make your project great again"
     *        url = 'https://github.com/$user/$projectname'
     *        licenses {
     *            license {
     *                name = 'The Apache License, Version 2.0'
     *                url = 'http://www.apache.org/licenses/LICENSE-2.0.txt'
     *            }
     *        }
     *
     *        developers {
     *            developer {
     *                id = '$user'
     *                name = '$fullname'
     *                email = 'dev@eloper.com'
     *                url = 'https://github.com/$user'
     *            }
     *        }
     *     }
     * }
     * ```
     */
    fun pom(action: Action<MavenPom>) {
        customPomConfiguration = action
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