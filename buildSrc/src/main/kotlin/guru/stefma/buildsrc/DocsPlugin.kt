package guru.stefma.buildsrc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.plugins.site.SitePluginExtension
import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URL

class DocsPlugin : Plugin<Project> {

    override fun apply(project: Project): Unit = with(project) {
        pluginManager.apply {
            apply("org.jetbrains.dokka")
            apply("com.github.gradle-guides.site")
        }

        // Create the extension
        val extension = extensions.create("docs", DocsExtension::class.java)

        afterEvaluate {
            // Set the vcsUrl from our extension to the SitePluginExtension
            extensions.getByType(SitePluginExtension::class.java).vcsUrl = extension.vcsUrl

            // Setup the DokkaTask to include the doc links...
            tasks.withType(DokkaTask::class.java) { task ->
                extension.externalDocLinks.forEach {
                    val externalLink = DokkaConfiguration.ExternalDocumentationLink.Builder()
                            .apply { url = URL(it) }.build()
                    task.externalDocumentationLinks.add(externalLink)
                }
            }
        }
    }

}

open class DocsExtension {

    var vcsUrl: String? = null

    val externalDocLinks = mutableListOf<String>()

}