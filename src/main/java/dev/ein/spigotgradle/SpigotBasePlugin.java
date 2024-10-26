package dev.ein.spigotgradle;

import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin;
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import org.gradle.api.GradleException;
import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.Sync;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.compile.JavaCompile;
import shadow.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
            if (spigotExtension.dependencies.usePaper.get() && spigotExtension.dependencies.useSpigot.get())
                throw new GradleException("You should never use both paper and spigot");
            if (spigotExtension.dependencies.useSpigot.get()) {
                Dependency spigotDep = project.getDependencies().add("compileOnly", "org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT");
                project.getConfigurations().named("compileOnly", Configuration.class).configure(c -> {
                    c.getDependencies().add(spigotDep);
                });
            }
            project.getPluginManager().apply(JavaPlugin.class);
            project.getTasks().named("compileJava", JavaCompile.class, t -> {
                t.getOptions().setEncoding("UTF-8");
                t.setSourceCompatibility("17");
                t.setTargetCompatibility("17");
                t.doLast(t2 -> {
                    File pluginYml = project.getBuildDir().toPath().resolve("classes/java/main/plugin.yml").toFile();
                    try {
                        String content = FileUtils.readFileToString(pluginYml, StandardCharsets.UTF_8);
                        content = content.replace("@@name@@", project.getName());
                        content = content.replace("@@version@@", project.getVersion().toString());
                        FileUtils.write(pluginYml, content, StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        throw new GradleException(e.getMessage());
                    }
                });
            });
            project.getPluginManager().apply(ShadowPlugin.class);
            TaskProvider<ShadowJar> taskShadowJar = project.getTasks().named("shadowJar", ShadowJar.class, t -> {
                t.exclude("META-INF/**");
                t.getArchiveFileName().set(String.format("%s-%s.jar", project.getName(), project.getVersion()));
            });
            project.getTasks().named("spigotServerPlugins", Sync.class, t -> {
                t.from(taskShadowJar);
            });
        });
    }
}
