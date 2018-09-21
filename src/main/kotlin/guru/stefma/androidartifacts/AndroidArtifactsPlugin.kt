package guru.stefma.androidartifacts

import com.android.build.gradle.api.LibraryVariant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.maven.MavenPublication

/**
 * This Plugin will simplify the process to create [publications][org.gradle.api.publish.Publication]
 * for your **Android Library**.
 *
 * It will generate for all available **build types** a new [org.gradle.api.publish.Publication]
 * and set up some tasks (e.g. for packaging the javadoc or sources) for it.
 *
 * This makes is very easily to publish your Android Library to the local maven repository.
 */
class AndroidArtifactsPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.createAndroidArtifactsExtension()
        project.applyMavenPublishPlugin()
        project.applyDokkaPlugin()
        val publicationContainer = project.publishingExtension.publications
        val publicationTasks = project.tasks.createListAvailablePublicationTask()
        project.androidLibraryExtension.libraryVariants.all {
            project.tasks.createAndroidArtifactsTask(it.name)
            project.createPublication(extension, publicationContainer, it, publicationTasks)
        }
    }

    private fun Project.createAndroidArtifactsExtension() =
            extensions.create("androidArtifact", ArtifactsExtension::class.java)

    /**
     * Applies the "org.jetbrains.dokka-android" plugin if we have already
     * applied the "kotlin-android" or "org.jetbrains.kotlin.android" plugin...
     */
    private fun Project.applyDokkaPlugin() = with(pluginManager) {
        if (hasKotlinAndroidPluginApplied) apply("org.jetbrains.dokka-android")
    }

    private fun Project.createPublication(
            extension: ArtifactsExtension,
            publishingContainer: PublicationContainer,
            variant: LibraryVariant,
            publicationNames: ListGeneratedPublicationNamesTask
    ) {
        val aarPublicationName = variant.name.aarPublicationName
        publicationNames.publicationNames += aarPublicationName
        publishingContainer.create(aarPublicationName, MavenPublication::class.java) {
            it.addAarArtifact(this, variant.name)
            // Publish sources only if set to true
            if (extension.sources) it.addAndroidSourcesArtifact(this, variant)
            // Publish javadoc only if set to true
            if (extension.javadoc) {
                it.addAndroidJavadocArtifact(this, variant)
                // Add dokka artifact if the kotlin plugin is applied...
                if (hasKotlinAndroidPluginApplied) it.addAndroidDokkaArtifact(this, variant)
            }

            it.setupMetadata(this, extension)

            it.pom {
                it.packaging = "aar"
                it.addConfigurations(configurations)
            }
        }
    }

    /**
     * Creates a publication name for **this** [variantName]
     */
    private val String.aarPublicationName
        get() = "${this}Aar"
}
