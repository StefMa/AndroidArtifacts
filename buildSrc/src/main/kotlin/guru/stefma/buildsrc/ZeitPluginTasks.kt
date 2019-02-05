package guru.stefma.buildsrc

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.api.tasks.options.Option
import java.io.File

/**
 * This task could be used to attach the Zeit token via the command line.
 *
 * Something like
 * ```
 * $ gradle zeitTask --zeitToken=1234
 * ```
 */
open class DefaultZeitTask : DefaultTask() {

    @Option(description = "Set the token to the now command")
    var zeitToken: String? = null

}

/**
 * This task will move the $rootProject/buildDir/dokka and $rootProject/buildDir/docs/site directories
 * into the $rootProject/buildDir/now directory.
 */
@CacheableTask
open class MoveDokkaAndGradleSiteToNow : DefaultTask() {

    @OutputDirectory
    val nowDirectory = File(project.rootProject.buildDir, "now/")

    @InputDirectory
    val dokkaDirectory = File(project.rootProject.buildDir, "dokka/")

    @InputDirectory
    val gradleSiteDirectory = File(project.rootProject.buildDir, "docs/site/")

    @TaskAction
    fun moveDirectories() {
        dokkaDirectory.copyRecursively(File(nowDirectory.absolutePath), true)
        gradleSiteDirectory.copyRecursively(File(nowDirectory, "gradleSite/"), true)
    }
}

@CacheableTask
open class CreateNowEntrypointIndexHtml : DefaultTask() {

    @Input
    val indexHtmlContent = """
        <!DOCTYPE HTML>
        <html>
            <body>
                <a href='androidartifacts/'>Dokka</a>.
                <a href='gradleSite/'>Gradle Site</a>.
            </body>
        </html>
    """.trimIndent()

    @OutputFile
    val indexHtmlFile = File(project.rootProject.buildDir, "now/index.html")

    @TaskAction
    fun createEntrypointIndexHtml() {
        indexHtmlFile.writeText(indexHtmlContent)
    }

}

@CacheableTask
open class CreateNowJson : DefaultTask() {

    @Input
    val nowJsonContent = """
        {
            "version": 2,
            "alias" : "androidartifacts",
            "builds": [{ "src": "**", "use": "@now/static" }]
        }
    """.trimIndent()

    @OutputFile
    val nowJsonFile = File(project.rootProject.buildDir, "now/now.json")

    @TaskAction
    fun createNowJson() {
        nowJsonFile.writeText(nowJsonContent)
    }

}