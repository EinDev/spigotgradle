package spigot.gradle;

import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

@NonNullApi
public class SpigotBasePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        SpigotExtension spigotExtension = project.getExtensions().create("spigot", SpigotExtension.class, project);
    }
}
