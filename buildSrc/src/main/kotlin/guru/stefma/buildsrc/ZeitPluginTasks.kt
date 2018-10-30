package guru.stefma.buildsrc

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import java.io.File


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

/**
 * This task will create a Dockerfile inside the $rootProject/buildDir/now directory
 * to prepare it for the now publishing...
 */
@CacheableTask
open class CreateNowDockerfile : DefaultTask() {

    @Input
    val dockerfileContent = """
        FROM httpd:2.4-alpine
        COPY . /usr/local/apache2/htdocs/
    """.trimIndent()

    @OutputFile
    val dockerFile = File(project.rootProject.buildDir, "now/Dockerfile")

    @TaskAction
    fun createDockerFile() {
        dockerFile.writeText(dockerfileContent)
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
            "alias" : "androidartifacts"
        }
    """.trimIndent()

    @OutputFile
    val nowJsonFile = File(project.rootProject.buildDir, "now/now.json")

    @TaskAction
    fun createNowJson() {
        nowJsonFile.writeText(nowJsonContent)
    }

}