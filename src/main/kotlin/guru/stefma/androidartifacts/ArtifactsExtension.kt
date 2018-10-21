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
    fun pom(block: Action<MavenPom>) {
        customPomConfiguration = block
    }

}