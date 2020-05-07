package spigot.gradle;

import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;
import spigot.gradle.task.SpigotBuildToolBuild;
import spigot.gradle.task.SpigotBuildToolDownload;

@SuppressWarnings("CodeBlock2Expr")
@NonNullApi
public class SpigotPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getExtensions().create("spigot", SpigotExtension.class, project);

        //spigot buildTool
        TaskProvider<SpigotBuildToolDownload> taskBuildToolDownload = project.getTasks().register("spigotBuildToolDownload", SpigotBuildToolDownload.class);

        TaskProvider<SpigotBuildToolBuild> taskBuildToolBuild = project.getTasks().register("spigotBuildToolBuild", SpigotBuildToolBuild.class, t -> {
            t.dependsOn(taskBuildToolDownload);
        });
    }
}
