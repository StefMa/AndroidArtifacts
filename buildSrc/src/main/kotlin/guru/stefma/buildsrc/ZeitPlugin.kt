package guru.stefma.buildsrc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer

class ZeitPlugin : Plugin<Project> {

    override fun apply(project: Project): Unit = with(project) {
        tasks.register("moveDocsToNow", MoveDokkaAndGradleSiteToNow::class.java) {
            it.dependsOn(tasksNamed("dokka", "generateSite"))
        }
        tasks.register("createNowDockerfile", CreateNowDockerfile::class.java)
        tasks.register("createNowEntrypoint", CreateNowEntrypointIndexHtml::class.java)
        tasks.register("createNowJson", CreateNowJson::class.java)

        // This task requires a valid now-cli installation...
        // Alternatively you can put a now token via gradle properties in.
        tasks.register("publishDocsToNow") {
            it.dependsOn(tasksNamed("moveDocsToNow", "createNowDockerfile", "createNowEntrypoint", "createNowJson"))
            it.executeWithNowCommand(project, "--public")
        }

        tasks.register("createNowAlias") {
            it.dependsOn(tasksNamed("publishDocsToNow"))
            it.executeWithNowCommand(project, "alias")
        }
    }

}

/**
 * This will call [Project.exec] inside a [Task.doLast] action.
 *
 * The given [command] will be chained into the `now` command.
 */
private fun Task.executeWithNowCommand(project: Project, command: String) = doLast {
    project.exec {
        it.workingDir("${project.rootProject.buildDir}/now")
        val token = project.findProperty("nowToken")
        if (token != null) {
            it.commandLine("now", command, "--token", token)
        } else {
            // Try to run without token...
            it.commandLine("now", command)
        }
    }
}

/**
 * A shorthand over [TaskContainer.named].
 *
 * Will return an array for each given [taskNames].
 */
private fun Project.tasksNamed(vararg taskNames: String) =
        taskNames.map { tasks.named(it) }.toTypedArray()