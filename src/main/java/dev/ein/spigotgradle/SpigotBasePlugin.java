package dev.ein.spigotgradle;

import org.gradle.api.GradleException;
import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

@NonNullApi
public class SpigotBasePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        SpigotExtension spigotExtension = project.getExtensions().create("spigot", SpigotExtension.class, project);

        project.getGradle().projectsEvaluated(gradle -> {
            if (!spigotExtension.version.isPresent())
                throw new GradleException("Variable 'spigot.version' is not set! Please set it to a valid version like 1.13, 1.14 or 1.15.");
            if(!spigotExtension.flavor.isPresent())
                spigotExtension.flavor.set(SpigotExtension.ServerVersionFlavor.SPIGOT);
        });
    }
}
