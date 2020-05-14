package firestar99.spigotgradle.plugin;

import firestar99.spigotgradle.SpigotBasePlugin;
import firestar99.spigotgradle.SpigotExtension;
import firestar99.spigotgradle.server.SpigotServerPlugin;
import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPlugin;

@SuppressWarnings("CodeBlock2Expr")
@NonNullApi
public class SpigotPluginPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(SpigotBasePlugin.class);
        project.getPlugins().apply(JavaLibraryPlugin.class);

        SpigotExtension spigotExtension = SpigotExtension.get(project);

        project.getGradle().projectsEvaluated(gradle -> {
            if (spigotExtension.plugin.repos.get()) {
                project.getRepositories().maven(mvn -> mvn.setUrl("https://hub.spigotmc.org/nexus/content/repositories/snapshots/"));
                project.getRepositories().maven(mvn -> mvn.setUrl("https://hub.spigotmc.org/nexus/content/groups/public/"));
            }

            if (spigotExtension.plugin.api.get()) {
                project.getDependencies().add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, "org.spigotmc:spigot-api:" + spigotExtension.version.get() + "-R0.1-SNAPSHOT");
            }

            project.getPlugins().withType(SpigotServerPlugin.class, plugin -> {
                project.getDependencies().add(SpigotServerPlugin.CONFIGURATION_SPIGOT_PLUGIN, project);
            });
        });
    }
}
