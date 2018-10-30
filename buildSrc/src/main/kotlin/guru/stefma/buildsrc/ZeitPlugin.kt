package guru.stefma.buildsrc

import org.gradle.api.Plugin
import org.gradle.api.Project

class ZeitPlugin : Plugin<Project> {

    override fun apply(project: Project): Unit = with(project) {
        tasks.create("moveDocsToNow", MoveDokkaAndGradleSiteToNow::class.java) {
            it.dependsOn("dokka", "generateSite")
        }
        tasks.create("createNowDockerfile", CreateNowDockerfile::class.java)
        tasks.create("createNowEntrypoint", CreateNowEntrypointIndexHtml::class.java)
        tasks.create("createNowJson", CreateNowJson::class.java)

        // This task requires a valid now-cli installation...
        // Alternatively you can put a now token via gradle properties in.
        tasks.create("publishDocsToNow") {
            it.dependsOn("moveDocsToNow", "createNowDockerfile", "createNowEntrypoint", "createNowJson")

            it.doLast {
                project.exec {
                    it.workingDir("${project.buildDir}/now")
                    val token = project.findProperty("nowToken")
                    if (token != null) {
                        it.commandLine("now", "--public", "--token", token)
                    } else {
                        // Try to run without token...
                        it.commandLine("now", "--public")
                    }
                }
            }
        }

        tasks.create("createNowAlias") {
            it.dependsOn("publishDocsToNow")

            it.doLast {
                project.exec {
                    it.workingDir("${project.buildDir}/now")
                    val token = project.findProperty("nowToken")
                    if (token != null) {
                        it.commandLine("now", "alias", "--token", token)
                    } else {
                        // Try to run without token...
                        it.commandLine("now", "alias")
                    }
                }
            }
        }
    }

}