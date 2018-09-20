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
internal val Project.hasKotlinAndroidPluginApplied
    get() = pluginManager.hasPlugin("kotlin-android") || pluginManager.hasPlugin("org.jetbrains.kotlin.android")

/**
 * Is true if either the `kotlin` or the `org.jetbrains.kotlin.jvm` plugin
 * is applied...
 */
internal val Project.hasKotlinJvmPluginApplied
    get() = pluginManager.hasPlugin("kotlin") || pluginManager.hasPlugin("org.jetbrains.kotlin.jvm")

/**
 * Applies the **maven-publish** plugin
 */
internal fun Project.applyMavenPublishPlugin() = pluginManager.apply("maven-publish")