package dev.ein.spigotgradle;

import org.gradle.api.Project;

public class HasProject {

    public final Project project;

    public HasProject(Project project) {
        this.project = project;
    }
}
