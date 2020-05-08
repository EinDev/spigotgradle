package spigot.gradle.plugin;

import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPlugin;
import spigot.gradle.SpigotBasePlugin;
import spigot.gradle.SpigotExtension;
import spigot.gradle.server.SpigotServerPlugin;

@SuppressWarnings("CodeBlock2Expr")
@NonNullApi
public class SpigotPluginPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(SpigotBasePlugin.class);
        project.getPlugins().apply(JavaLibraryPlugin.class);

        SpigotExtension spigotExtension = SpigotExtension.get(project);

        project.getGradle().projectsEvaluated(gradle -> {
            if (spigotExtension.plugin.addRepo.get()) {
                project.getRepositories().maven(mvn -> mvn.setUrl("https://hub.spigotmc.org/nexus/content/repositories/snapshots/"));
                project.getRepositories().maven(mvn -> mvn.setUrl("https://hub.spigotmc.org/nexus/content/groups/public/"));
            }

            if(spigotExtension.plugin.addApi.get()) {
                project.getDependencies().add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, "org.spigotmc:spigot-api:" + spigotExtension.version.get() + "-R0.1-SNAPSHOT");
            }

            project.getPlugins().withId("spigot-server", plugin -> {
                project.getDependencies().add(SpigotServerPlugin.CONFIGURATION_SPIGOT_PLUGIN, project);
            });
        });
    }
}
