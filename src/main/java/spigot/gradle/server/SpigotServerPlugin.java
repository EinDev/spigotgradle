package spigot.gradle.server;

import org.gradle.api.*;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import spigot.gradle.SpigotBasePlugin;
import spigot.gradle.SpigotExtension;
import spigot.gradle.server.task.SpigotBuildToolBuild;
import spigot.gradle.server.task.SpigotBuildToolDownload;

import java.io.IOException;
import java.nio.file.Files;

@SuppressWarnings("CodeBlock2Expr")
@NonNullApi
public class SpigotServerPlugin implements Plugin<Project> {

    public static final String CONFIGURATION_SPIGOT_PLUGIN = "spigotPlugin";

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(SpigotBasePlugin.class);

        SpigotExtension spigotExtension = SpigotExtension.get(project);
        TaskContainer tasks = project.getTasks();

        //spigot buildTool
        TaskProvider<SpigotBuildToolDownload> taskBuildToolDownload = tasks.register("spigotBuildToolDownload", SpigotBuildToolDownload.class);
        TaskProvider<SpigotBuildToolBuild> taskBuildToolBuild = tasks.register("spigotBuildToolBuild", SpigotBuildToolBuild.class, t -> {
            t.dependsOn(taskBuildToolDownload);
        });

        //spigot server
        NamedDomainObjectProvider<Configuration> configurationSpigotPlugin = project.getConfigurations().register(CONFIGURATION_SPIGOT_PLUGIN);

        TaskProvider<Task> taskServerPrepare = project.getTasks().register("spigotServerPrepare", t -> {
            t.setDescription("prepares the server");
            t.setGroup("spigot server");
        });

        TaskProvider<Copy> taskServerJar = tasks.register("spigotServerJar", Copy.class, t -> {
            t.setDescription("copies the spigot server jar");
            t.setGroup("spigot server");
            t.dependsOn(taskBuildToolBuild);
            t.from(spigotExtension.buildTool.build);
            t.into(spigotExtension.server.spigotJar.map(f -> f.getAsFile().getParentFile()));
            t.include("spigot*.jar");
            t.rename(s -> spigotExtension.server.spigotJar.get().getAsFile().getName());
        });
        taskServerPrepare.configure(t -> t.dependsOn(taskServerJar));

        TaskProvider<Copy> taskServerPlugins = tasks.register("spigotServerPlugins", Copy.class, t -> {
            t.setDescription("copies the plugins");
            t.setGroup("spigot server");
            t.from(configurationSpigotPlugin);
            t.into(spigotExtension.server.spigotPlugins);
        });
        taskServerPrepare.configure(t -> t.dependsOn(taskServerPlugins));

        TaskProvider<Task> taskServerEluaAccept = tasks.register("spigotServerEulaAccept", t -> {
            t.setDescription("accepts the elua");
            t.setGroup("spigot server");
            t.getOutputs().file(spigotExtension.server.eulaTxt);
            t.doFirst(t2 -> {
                try {
                    Files.write(spigotExtension.server.eulaTxt.get().getAsFile().toPath(), "eula=true".getBytes());
                } catch (IOException e) {
                    throw new GradleException("Failed to auto-accept eula", e);
                }
            });
        });
        taskServerPrepare.configure(t -> t.dependsOn(taskServerEluaAccept));

        tasks.register("spigotServerRun", Exec.class, t -> {
            t.setDescription("runs the spigot server");
            t.setGroup("spigot server");
            t.dependsOn(taskServerPrepare);
            t.workingDir(spigotExtension.server.root);
            t.commandLine("java", "-jar", spigotExtension.server.spigotJar.get());
        });

        project.getGradle().projectsEvaluated(gradle -> {
            if (tasks.findByPath("run") == null) {
                tasks.register("run", t -> t.dependsOn("spigotServerRun"));
            }
        });
    }
}
