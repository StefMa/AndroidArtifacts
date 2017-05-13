package guru.stefma.androidartifacts

import org.gradle.api.Project

interface Artifacts {

    def all(String publicationName, Project project)

    def from(Project project)

}