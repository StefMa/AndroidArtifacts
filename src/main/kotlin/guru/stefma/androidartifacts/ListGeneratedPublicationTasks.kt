package guru.stefma.androidartifacts

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * List all generated [publications][org.gradle.api.publish.Publication].
 *
 * The [publicationNames] will be set by the [AndroidArtifactsPlugin] and the [JavaArtifactsPlugin].
 */
open class ListGeneratedPublicationTasks : DefaultTask() {

    init {
        group = "Publishing"
        description = "Print a list of all generated publications"
    }

    internal val publicationNames = mutableListOf<String>()

    @TaskAction
    fun printPublications() {
        println("""
            This is a list of all generated publications by the ${AndroidArtifactsPlugin::class.java.simpleName}:
            $publicationNames
        """.trimIndent())
    }

}