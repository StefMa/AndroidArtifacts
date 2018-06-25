package guru.stefma.androidartifacts

import com.android.build.gradle.api.LibraryVariant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.maven.MavenPublication

class AndroidArtifactsPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.createAndroidArtifactsExtension()
        project.applyMavenPublishPlugin()
        project.applyDokkaPlugin()
        val publicationContainer = project.publishingExtension.publications
        project.androidLibraryExtension.libraryVariants.all {
            project.tasks.createAndroidArtifactsTask(it.name)
            project.createPublication(extension, publicationContainer, it)
        }
    }

    private fun Project.createAndroidArtifactsExtension() =
            extensions.create("androidArtifact", AndroidArtifactsExtension::class.java)

    private fun Project.applyMavenPublishPlugin() = pluginManager.apply("maven-publish")

    /**
     * Applies the "org.jetbrains.dokka-android" plugin if we have already
     * applied the "kotlin-android" or "org.jetbrains.kotlin.android" plugin...
     */
    private fun Project.applyDokkaPlugin() = with(pluginManager) {
        if (hasKotlinPluginApplied) apply("org.jetbrains.dokka-android")
    }

    private fun Project.createPublication(
            extension: AndroidArtifactsExtension,
            publishingContainer: PublicationContainer,
            variant: LibraryVariant
    ) {
        publishingContainer.create(variant.name.aarPublicationName, MavenPublication::class.java) {
            it.addAarArtifact(this, variant.name)
            // Publish sources only if set to true
            if (extension.sources) it.addSourcesArtifact(this, variant)
            // Publish javadoc only if set to true
            if (extension.javadoc) {
                it.addJavadocArtifact(this, variant)
                // Add dokka artifact if the kotlin plugin is applied...
                if (hasKotlinPluginApplied) it.addDokkaArtifact(this, variant)
            }

            it.setupMetadata(extension)

            it.pom {
            val implementationConfig = configurations.getByName("implementation")
                it.addDependenciesForConfiguration(implementationConfig)
                it.packaging = "aar"
            }
        }
    }

    /**
     * Creates a publication name for **this** [variantName]
     */
    private val String.aarPublicationName
        get() = "${this}Aar"
}
