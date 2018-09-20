package guru.stefma.androidartifacts

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication

class JavaArtifactsPlugin : Plugin<Project> {

    override fun apply(project: Project) {
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
     */
    private fun Project.applyDokkaPlugin() = with(pluginManager) {
        if (hasKotlinJvmPluginApplied) apply("org.jetbrains.dokka")
    }

    private fun createPublication(
            extension: ArtifactsExtension,
            project: Project,
            publicationName: String,
            publicationTasks: ListGeneratedPublicationTasks
    ) {
        publicationTasks.publicationNames += publicationName
        project.publishingExtension.publications.create(publicationName, MavenPublication::class.java) {
            it.from(project.components.getByName("java"))
            // Publish sources only if set to true
            if (extension.sources) it.addJavaSourcesArtifact(project, publicationName)
            // Publish javadoc only if set to true
            if (extension.javadoc) {
                it.addJavaJavadocArtifact(project, publicationName)

                // Add dokka artifact if the kotlin plugin is applied...
                if (project.hasKotlinJvmPluginApplied) it.addJavaDokkaArtifact(project, publicationName)
            }

            it.setupMetadata(project, extension)
        }
    }

}