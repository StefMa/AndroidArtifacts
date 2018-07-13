package guru.stefma.androidartifacts

import groovy.util.Node
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.publish.maven.MavenPom

private const val DEPENDENCIES_NODE_NAME = "dependencies"

/**
 * Add the given [configuration] to this [MavenPom] via [MavenPom.withXml].
 *
 * If not already available it  will generate a new [Node] called **dependencies** and add each
 * [dependencies][Configuration.getAllDependencies] to a [sub-node][Node] called **dependency**
 * with the given [scope].
 *
 * For mew information about the [scope] see [MavenPom](https://maven.apache.org/pom.html#Dependencies)
 */
internal fun MavenPom.addDependenciesForConfiguration(configuration: Configuration, scope: String) = withXml {
    val node = it.asNode()
    val dependenciesNode = node.depthFirst()
            .find { it?.toString() == DEPENDENCIES_NODE_NAME }
            .run {
                if (this == null) {
                    node.appendNode(DEPENDENCIES_NODE_NAME)
                } else {
                    this as Node
                }
            }

    configuration.allDependencies.forEach { dependenciesNode.addDependency(it, scope) }
}

private fun Node.addDependency(dependency: Dependency, scope: String) {
    val dependencyNode = appendNode("dependency")
    with(dependency) {
        dependencyNode.appendNode("groupId", group)
        dependencyNode.appendNode("artifactId", name)
        dependencyNode.appendNode("version", version)
    }
    dependencyNode.appendNode("scope", scope)
}