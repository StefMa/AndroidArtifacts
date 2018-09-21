package guru.stefma.androidartifacts.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * List all generated [publications][org.gradle.api.publish.Publication]
 * by name ([org.gradle.api.publish.Publication.getName]).
 *
 * The [publicationNames] will be set by the [AndroidArtifactsPlugin] and the [JavaArtifactsPlugin].
 */
open class ListGeneratedPublicationNamesTask : DefaultTask() {

    internal val publicationNames = mutableListOf<String>()

    @TaskAction
    fun printPublications() {
        println("""
            This is a list of all generated publications by the 'guru.stefma.androidartifacts' or 'guru.stefma.javaartifacts' plugin:
            $publicationNames
        """.trimIndent())
    }

}