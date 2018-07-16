package guru.stefma.androidartifacts

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication

class JavaArtifactsPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.createJavaArtifactsExtension()
        project.applyMavenPublishPlugin()
        val publicationTasks = project.tasks.createListAvailablePublicationTask()
        val publicationName = "maven"
        project.tasks.createJavaArtifactsTask(publicationName)
        publicationTasks.publicationNames += publicationName
        createPublication(extension, project, publicationName, publicationTasks)
    }

    private fun Project.createJavaArtifactsExtension() =
            extensions.create("javaArtifact", ArtifactsExtension::class.java)

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
            if (extension.sources) it.addJavaSourcesArtifact(project, "maven")
            // Publish javadoc only if set to true
            if (extension.javadoc) {
                it.addJavaJavadocArtifact(project, "maven")
            }

            it.setupMetadata(project, extension)
        }
    }

}