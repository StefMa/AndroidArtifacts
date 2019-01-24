package guru.stefma.androidartifacts

import com.android.build.gradle.api.LibraryVariant
import com.android.builder.model.SourceProvider
import guru.stefma.androidartifacts.task.ListGeneratedPublicationNamesTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.jvm.tasks.Jar

private const val TASKS_GROUP = "Publishing"

/**
 * Create the "basic" **androidArtifacts** tasks which is basically just a shorthand
 * for the generated `publish${variantName.capitalize()}AarPublicationToMavenLocal` task.
 *
 * This should be used to publish the artifacts to the mavenLocal.
 */
internal fun TaskContainer.createAndroidArtifactsTask(variantName: String) {
    create(variantName.androidArtifactsTaskName) {
        it.dependsOn("publish${variantName.capitalize()}AarPublicationToMavenLocal")
        it.group = TASKS_GROUP
        it.description = "Publish an AAR for '$variantName' to the local Maven repository."
    }
}

/**
 * Create the "basic" **androidArtifacts** tasks which is basically just a shorthand
 * for the generated `publish${publicationName}PublicationToMavenLocal` task.
 *
 * This should be used to publish the artifacts to the mavenLocal.
 */
internal fun TaskContainer.createJavaArtifactsTask(publicationName: String) {
    create("java".androidArtifactsTaskName) {
        it.dependsOn("publish${publicationName.capitalize()}PublicationToMavenLocal")
        it.group = TASKS_GROUP
        it.description = "Publish an JAR for '$publicationName' to the local Maven repository."
    }
}

/**
 * Create a new [Task] from the typ [Jar] with the name [String.sourcesTaskName]
 * which will put all [SourceProvider.getJavaDirectories] from the given [LibraryVariant.getSourceSets]
 * into the generated Jar file.
 */
internal fun TaskContainer.createAndroidArtifactsSourcesTask(variant: LibraryVariant): Task {
    return create(variant.name.sourcesTaskName, Jar::class.java) { task ->
        task.classifier = "sources"
        variant.sourceSets.forEach {
            task.from(it.javaDirectories)
        }

        task.group = TASKS_GROUP
        task.description = "Package the sources for the 'androidArtifact${variant.name.capitalize()}' into a JAR."
    }
}

/**
 * Create a new [Task] from the typ [Jar] with the name [String.sourcesTaskName]
 * which will put all [org.gradle.api.tasks.SourceSet.getAllSource] from the given [JavaPluginConvention.getSourceSets]
 * into the generated Jar file.
 */
internal fun TaskContainer.createJavaArtifactsSourcesTask(javaConvention: JavaPluginConvention): Task {
    return create("java".sourcesTaskName, Jar::class.java) { task ->
        task.classifier = "sources"
        javaConvention.sourceSets.forEach {
            it.allSource
            task.from(it.allSource)
        }

        task.group = TASKS_GROUP
        task.description = "Package the sources for the 'androidArtifactJava' into a JAR."
    }
}

/**
 * Creates two tasks. One will create the real javadoc based on the
 * [SourceProvider.getJavaDirectories] from the given [LibraryVariant.getSourceSets].
 * This will also add the [Javadoc.classpath] so that package names got resolved
 * correctly.
 *
 * The second one [Task.dependsOn] the first one
 * and will then use the [Javadoc.destinationDir] and put it into a Jar file.
 */
internal fun TaskContainer.createAndroidArtifactsJavadocTask(
        project: Project,
        variant: LibraryVariant
): Task {
    val docHelperTask =
            create("androidArtifact${variant.name.capitalize()}JavadocHelper", Javadoc::class.java) {
                it.source = variant.safeJavaCompiler.source
                // The first two classpath(es) adds our own
                // and the Android code.
                variant.sourceSets.forEach { sourceProvider ->
                    it.classpath += project.files(sourceProvider.javaDirectories)
                }
                it.classpath += project.files(project.androidLibraryExtension.bootClasspath)
                // This will add all the dependencies to the classpath
                it.classpath += variant.safeJavaCompiler.classpath
                // excludes the from Android generated R and BuildConfig class
                it.exclude("**/R.*", "**/BuildConfig.*")
                it.isFailOnError = false
            }

    return create(variant.name.javadocTaskName, Jar::class.java) {
        it.dependsOn(docHelperTask)
        it.classifier = "javadocs"
        it.from(docHelperTask.outputs)

        it.group = TASKS_GROUP
        it.description = "Package the javadoc for the 'androidArtifact${variant.name.capitalize()}' into a JAR."
    }
}

private val LibraryVariant.safeJavaCompiler
    get() = try {
        val javaCompileProvider = this::class.java.getMethod("getJavaCompileProvider").invoke(this)
        (javaCompileProvider as TaskProvider<JavaCompile>).get()
    } catch (noMethodExe: NoSuchMethodException) {
        javaCompile
    }

/**
 * Creates a [Jar] task which will package the sources from the [org.gradle.api.tasks.javadoc.Javadoc] output.
 */
internal fun TaskContainer.createJavaArtifactsJavadocTask(): Task {
    val javadocTask = getByName("javadoc") as Javadoc
    javadocTask.isFailOnError = false

    return create("java".javadocTaskName, Jar::class.java) {
        it.dependsOn(javadocTask)
        it.classifier = "javadocs"
        it.from(javadocTask.outputs)

        it.group = TASKS_GROUP
        it.description = "Package the javadoc for the 'androidArtifactJava` into a JAR."
    }
}

/**
 * Creates a new [Jar] tasks which depends on the by the dokka generated `dokka` tasks and
 * put the outut from the `dokka` tasks into the generated Jar file.
 */
internal fun TaskContainer.createAndroidArtifactsDokkaTask(variantName: String): Task {
    return create(variantName.dokkaTaskName, Jar::class.java) {
        val dokkaTask = getByName("dokka")
        it.dependsOn(dokkaTask)
        it.classifier = "kdoc"
        it.from(dokkaTask.outputs)

        it.group = TASKS_GROUP
        it.description = "Package the kdoc for the 'androidArtifact${variantName.capitalize()}' into a JAR."
    }
}

/**
 * Creates a new [Jar] tasks which depends on the by the dokka generated `dokka` tasks and
 * put the outut from the `dokka` tasks into the generated Jar file.
 */
internal fun TaskContainer.createJavaArtifactsDokkaTask(): Task {
    return create("java".dokkaTaskName, Jar::class.java) {
        val dokkaTask = getByName("dokka")
        it.dependsOn(dokkaTask)
        it.classifier = "kdoc"
        it.from(dokkaTask.outputs)

        it.group = TASKS_GROUP
        it.description = "Package the kdoc for the 'androidArtifactJava' into a JAR."
    }
}

/**
 * Creates a task from the type [ListGeneratedPublicationNamesTask].
 */
internal fun TaskContainer.createListAvailablePublicationTask(): ListGeneratedPublicationNamesTask {
    return create("androidArtifactGeneratedPublications", ListGeneratedPublicationNamesTask::class.java) {
        it.group = TASKS_GROUP
        it.description = "Print a list of all generated publications."
    }
}

private val String.androidArtifactsTaskName
    get() = "androidArtifact${capitalize()}"

private val String.sourcesTaskName
    get() = "androidArtifact${capitalize()}Sources"

private val String.javadocTaskName
    get() = "androidArtifact${capitalize()}Javadoc"

private val String.dokkaTaskName
    get() = "androidArtifact${capitalize()}Kdoc"