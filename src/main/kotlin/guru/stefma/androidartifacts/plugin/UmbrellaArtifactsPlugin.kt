package guru.stefma.androidartifacts.plugin

import guru.stefma.androidartifacts.PluginIds
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.PluginManager

/**
 * This [Plugin] will just [apply][PluginManager.apply] either
 * the [**guru.stefma.androidartifacts**][AndroidArtifactsPlugin] plugin
 * **or** the [**guru.stefma.javaartifacts**][JavaArtifactsPlugin] plugin -
 * based on the already applied plugins.
 *
 * It make use of the [PluginManager.withPlugin] to detect if the
 * **com.android.library** plugin (for the **guru.stefma.androidartifacts** plugin)
 * or one of **java-library**, **kotlin** or
 * **org.jetbrains.kotlin.jvm** is applied.
 */
class UmbrellaArtifactsPlugin : Plugin<Project> {

    private var javaArtifactsAlreadyApplied = false

    override fun apply(project: Project) = with(project.pluginManager) {
        withPlugin(PluginIds.androidLibrary) {
            apply(PluginIds.androidArtifacts)
        }

        withPlugin(PluginIds.javaLibrary) {
            applyJavaArtfiacts()
        }

        withPlugin(PluginIds.kotlinJvm) {
            applyJavaArtfiacts()
        }

        withPlugin(PluginIds.kotlinJvmLegacy) {
            applyJavaArtfiacts()
        }

    }

    private fun PluginManager.applyJavaArtfiacts() {
        if (!javaArtifactsAlreadyApplied) apply(PluginIds.javaArtifacts)
        javaArtifactsAlreadyApplied = true
    }

}