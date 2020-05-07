package spigot.gradle.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Copy;
import spigot.gradle.SpigotExtension;

public class SpigotServerPrepare extends DefaultTask {

    public SpigotServerPrepare() {
        setDescription("prepares the server");
        setGroup("spigot server");

        SpigotExtension spigotExtension = SpigotExtension.get(getProject());
        dependsOn(getProject().getTasks().register("spigotServerJar", Copy.class, c -> {
            c.from(spigotExtension.buildTool.build);
            c.into(spigotExtension.server.spigotJar.map(f -> f.getAsFile().getParentFile()));
            c.include("spigot*.jar");
            c.rename(s -> spigotExtension.server.spigotJar.get().getAsFile().getName());
        }));
        dependsOn(getProject().getTasks().register("spigotServerExtraPlugins", Copy.class, c -> {
            c.from(spigotExtension.buildTool.build);
            c.into(spigotExtension.server.spigotJar.map(f -> f.getAsFile().getParentFile()));
            c.include("spigot*.jar");
            c.rename(s -> spigotExtension.server.spigotJar.get().getAsFile().getName());
        }));
    }
}
