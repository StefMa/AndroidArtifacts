package guru.stefma.androidartifacts.plugin

import guru.stefma.androidartifacts.*
import guru.stefma.androidartifacts.task.ListGeneratedPublicationNamesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication

/**
 * This Plugin will simplify the process to create a [publications][org.gradle.api.publish.Publication]
 * for your **Java or Kotlin Library**.
 *
 * It will generate a new [org.gradle.api.publish.Publication] with the name **maven**
 * and set up some tasks (e.g. for packaging the javadoc/kdoc or sources) for it.
 *
 * This makes is very easily to publish your Library to the local maven repository.
 *
 * > **Note:**  This will only do all the stuff when the `java-library`, `kotlin`
 *              **or** `org.jetbrains.kotlin.jvm` plugin is applied.
 *              Otherwise it does **nothing**!
 */
class JavaArtifactsPlugin : Plugin<Project> {

    private var alreadySetup = false

    override fun apply(project: Project) {
        project.pluginManager.withPlugin(PluginIds.javaLibrary) {
            setupArtifacts(project)
        }

        project.pluginManager.withPlugin(PluginIds.kotlinJvm) {
            setupArtifacts(project)
        }

        project.pluginManager.withPlugin(PluginIds.kotlinJvmLegacy) {
            setupArtifacts(project)
        }
    }

    private fun setupArtifacts(project: Project) {
        if (alreadySetup) return
        alreadySetup = true

        val extension = project.createJavaArtifactsExtension()
        project.applyMavenPublishPlugin()
        project.applyDokkaPlugin()
        val publicationTasks = project.tasks.createListAvailablePublicationTask()
        val publicationName = "maven"
        project.tasks.createJavaArtifactsTask(publicationName)
        // TODO: Think if we can do that better lazy somehow
        // see https://docs.gradle.org/current/userguide/lazy_configuration.html
        project.afterEvaluate {
            createPublication(extension, project, publicationName, publicationTasks)
        }
    }

    private fun Project.createJavaArtifactsExtension() =
            extensions.create("javaArtifact", ArtifactsExtension::class.java)

    /**
     * Applies the "org.jetbrains.dokka" plugin if we have already
     * applied the "kotlin" or "org.jetbrains.kotlin.jvm" plugin...
     *
     * @see PluginIds
     */
    private fun Project.applyDokkaPlugin() = with(pluginManager) {
        if (hasKotlinJvmPluginApplied) apply(PluginIds.dokkaJvm)
    }

    private fun createPublication(
            extension: ArtifactsExtension,
            project: Project,
            publicationName: String,
            publicationNames: ListGeneratedPublicationNamesTask
    ) {
        publicationNames.publicationNames += publicationName
        project.publishingExtension.publications.create(publicationName, MavenPublication::class.java) {
            it.from(project.components.getByName("java"))
            // Publish sources only if set to true
            if (extension.sources) it.addJavaSourcesArtifact(project)
            // Publish javadoc only if set to true
            if (extension.javadoc) {
                it.addJavaJavadocArtifact(project)

                // Add dokka artifact if the kotlin plugin is applied...
                if (project.hasKotlinJvmPluginApplied) it.addJavaDokkaArtifact(project)
            }

            it.setupMetadata(project, extension)

            it.pom { pom ->
                // Add the license if available and Gradle version
                // is better than 4.7
                if (GradleVersionComparator(project.gradle.gradleVersion).betterThan("4.7")) {
                    extension.licenseSpec?.apply {
                        pom.licenses {
                            it.license {
                                it.name.set(name)
                                it.url.set(url)
                                it.comments.set(comments)
                                it.distribution.set(distribution)
                            }
                        }
                    }
                }


                pom.url.set(extension.url)
                pom.description.set(extension.description)
                pom.name.set(extension.name)
            }

        }
    }

}