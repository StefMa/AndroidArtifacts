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
        val publicationContainer = project.publishingExtension.publications
        publicationTasks.publicationNames += publicationName
        publicationContainer.create(publicationName, MavenPublication::class.java) {
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


    private fun Project.createJavaArtifactsExtension() =
            extensions.create("javaArtifact", ArtifactsExtension::class.java)

}