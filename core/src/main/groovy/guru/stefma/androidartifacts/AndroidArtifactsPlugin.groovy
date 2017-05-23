package guru.stefma.androidartifacts

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.util.GradleVersion

class AndroidArtifactsPlugin implements Plugin<Project> {

    ArtifactsExtension mArtifactsExtension

    @Override
    void apply(Project project) {
        mArtifactsExtension = getArtifactsExtensions(project)
        project.afterEvaluate {
            project.apply([plugin: 'maven-publish'])
            attachArtifacts(project)
        }
    }

    ArtifactsExtension getArtifactsExtensions(Project project) {
        mPublishExtension = project.extensions.create('androidArtifacts', ArtifactsExtension)
    }

    void attachArtifacts(Project project) {
        if (project.plugins.hasPlugin('com.android.library')) {

            if (outdatedGradleVersion()) {
                showOutdatedGradleVersionError()
                return
            }

            project.android.libraryVariants.each { variant ->
                addArtifact(project, (String) variant.name, new AndroidArtifacts(variant))
            }
        } else {
            addArtifact(project, 'maven', new JavaArtifacts())
        }
    }

    static def showOutdatedGradleVersionError() {
        System.err.println("""
                        |**************************************
                        |WARNING: Your gradle version is not supported by AndroidArtifacts plugin. Update required!
                        |
                        |The AndroidArtifacts plugin doesn't support version of Gradle below 3.4 for Android libraries. Please upgrade to Gradle 3.4 or later.
                        |
                        |Upgrade Gradle:
                        |./gradlew wrapper --gradle-version 3.4 --distribution-type all
                        |
                        |The AndroidArtifacts plugin can't create a Publication for your Android Library!
                        |**************************************
                        """.stripMargin())
    }

    static boolean outdatedGradleVersion() {
        def current = GradleVersion.current()
        def version3_3 = GradleVersion.version("3.3")

        return (current <=> version3_3) <= 0
    }

    void addArtifact(Project project, String name, Artifacts artifacts) {
        project.publishing.publications.create(name, MavenPublication) {
            groupId mArtifactsExtension.groupId
            artifactId mArtifactsExtension.artifactId
            version = mArtifactsExtension.publishVersion

            artifacts.all(it.name, project).each {
                delegate.artifact it
            }
            from artifacts.from(project)
        }
    }

}
