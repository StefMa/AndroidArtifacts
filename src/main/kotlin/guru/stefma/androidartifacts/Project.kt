package guru.stefma.androidartifacts

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension

/**
 * The [LibraryExtension] from the applied **com.android.library** plugin
 */
internal val Project.androidLibraryExtension
    get() = extensions.getByType(LibraryExtension::class.java)

/**
 * The [PublishingExtension] from the applied **maven-publish** plugin
 */
internal val Project.publishingExtension
    get() = extensions.getByType(PublishingExtension::class.java)

/**
 * Is true if either the `kotlin-android` or the `org.jetbrains.kotlin.android` plugin
 * is applied...
 */
internal val Project.hasKotlinPluginApplied
    get() = pluginManager.hasPlugin("kotlin-android") || pluginManager.hasPlugin("org.jetbrains.kotlin.android")

/**
 * Applies the **maven-publish** plugin
 */
internal fun Project.applyMavenPublishPlugin() = pluginManager.apply("maven-publish")