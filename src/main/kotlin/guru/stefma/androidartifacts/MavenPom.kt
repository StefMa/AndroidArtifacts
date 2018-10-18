package guru.stefma.androidartifacts

import groovy.util.Node
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency
import org.gradle.api.publish.maven.MavenPom

/**
 * This [Map] contains "pairs" with the **Gradle** [configurations][Configuration]
 * which should be mapped to the respective maven pom scope.
 *
 * For more more info see:
 * * [Maven docu](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Dependency_Scope)
 * * [GitHub issue](https://github.com/StefMa/AndroidArtifacts/issues/32)
 */
private val gradleConfigurationsToMavenPomScopes = mapOf(
        "compileOnly" to null,
        "runtimeOnly" to "runtime",
        "implementation" to "runtime",
        "api" to "compile"
)

/**
 * Adds each [Configuration] from the given [ConfigurationContainer] to the
 * pom via [addDependenciesForConfiguration].
 *
 * Requirement is that the [Configuration.getName] should be available inside
 * the [gradleConfigurationsToMavenPomScopes] as `key`.
 */
internal fun MavenPom.addConfigurations(configurations: ConfigurationContainer) {
    configurations.forEach { configuration ->
        if (gradleConfigurationsToMavenPomScopes.containsKey(configuration.name)) {
            gradleConfigurationsToMavenPomScopes[configuration.name]?.let {
                addDependenciesForConfiguration(configuration, it)
            }
        }
    }
}

/**
 * The name of the **dependencies** [Node] inside the pom.
 */
private const val DEPENDENCIES_NODE_NAME = "dependencies"

/**
 * Add the given [configuration] to this [MavenPom] via [MavenPom.withXml].
 *
 * If not already available it  will generate a new [Node] called **dependencies** and add each
 * [dependencies][Configuration.getDependencies] to a [sub-node][Node] called **dependency**
 * with the given [scope].
 *
 * For mew information about the [scope] see [MavenPom](https://maven.apache.org/pom.html#Dependencies)
 */
private fun MavenPom.addDependenciesForConfiguration(configuration: Configuration, scope: String) = withXml {
    val node = it.asNode()
    val dependenciesNode = node.depthFirst()
            .find { (it as? Node)?.name() == DEPENDENCIES_NODE_NAME }
            .run {
                if (this == null) {
                    node.appendNode(DEPENDENCIES_NODE_NAME)
                } else {
                    this as Node
                }
            }

    configuration.dependencies.forEach { dependenciesNode.addDependency(it, scope) }
}

private fun Node.addDependency(dependency: Dependency, scope: String) {
    if (dependency.group == null
            || dependency.version == null
            || dependency.name == "unspecified") {
        logger.info("One of your dependency has either: 'no group', 'no version' or 'no artifactId'\n" +
                "We ignore it!"
        )
        return
    }

    val dependencyNode = appendNode("dependency")
    dependency.apply {
        dependencyNode.appendNode("groupId", group)
        dependencyNode.appendNode("artifactId", name)
        dependencyNode.appendNode("version", version)
    }
    dependencyNode.appendNode("scope", scope)
}
