package guru.stefma.androidartifacts

import org.gradle.api.artifacts.Configuration
import org.gradle.api.publish.maven.MavenPom

/**
 * Add the given [configuration] to this [MavenPom] via [MavenPom.withXml].
 *
 * It will generate a new **node** called **dependencies** and add each
 * [Configuration.getAllDependencies] to a new **node** called **dependency**
 */
internal fun MavenPom.addDependenciesForConfiguration(configuration: Configuration) = withXml {
    val dependenciesNode = it.asNode().appendNode("dependencies")

    configuration.allDependencies.forEach {
        val dependencyNode = dependenciesNode.appendNode("dependency")
        dependencyNode.appendNode("groupId", it.group)
        dependencyNode.appendNode("artifactId", it.name)
        dependencyNode.appendNode("version", it.version)
        dependencyNode.appendNode("scope", "compile")
    }
}