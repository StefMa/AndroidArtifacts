package guru.stefma.androidartifacts.plugin

import com.android.build.gradle.api.LibraryVariant
import guru.stefma.androidartifacts.*
import guru.stefma.androidartifacts.task.ListGeneratedPublicationNamesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.util.GradleVersion
import org.gradle.util.VersionNumber

/**
 * This Plugin will simplify the process to create [publications][org.gradle.api.publish.Publication]
 * for your **Android Library**.
 *
 * It will generate for all available **build types** a new [org.gradle.api.publish.Publication]
 * and set up some tasks (e.g. for packaging the javadoc or sources) for it.
 *
 * This makes is very easily to publish your Android Library to the local maven repository.
 *
 * > **Note:**  This will only do all the stuff when the `com.android.library` plugin
 *              is applied. Otherwise it does **nothing**!
 */
class AndroidArtifactsPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager.withPlugin(PluginIds.androidLibrary) {
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
    }

    private fun Project.createAndroidArtifactsExtension() =
            extensions.create("androidArtifact", ArtifactsExtension::class.java)

    /**
     * Applies the "org.jetbrains.dokka-android" plugin if we have already
     * applied the "kotlin-android" or "org.jetbrains.kotlin.android" plugin...
     *
     * @see PluginIds
     */
    private fun Project.applyDokkaPlugin() = with(pluginManager) {
        if (hasKotlinAndroidPluginApplied) apply(PluginIds.dokkaAndroid)
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

            it.pom { pom ->
                pom.packaging = "aar"
                pom.addConfigurations(configurations)

                // Add the license if available and Gradle version
                // is better than 4.7
                if (GradleVersionComparator(gradle.gradleVersion).betterThan("4.7")) {
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

    /**
     * Creates a publication name for **this** [variantName]
     */
    private val String.aarPublicationName
        get() = "${this}Aar"
}
