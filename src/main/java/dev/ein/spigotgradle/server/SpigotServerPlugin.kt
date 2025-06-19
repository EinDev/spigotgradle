package dev.ein.spigotgradle.server;

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import de.undercouch.gradle.tasks.download.Download;
import dev.ein.spigotgradle.server.api.PaperListBuildsResponse;
import dev.ein.spigotgradle.server.task.SpigotBuildToolBuild;
import dev.ein.spigotgradle.server.task.SpigotBuildToolDownload;
import dev.ein.spigotgradle.SpigotBasePlugin;
import dev.ein.spigotgradle.SpigotExtension;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.gradle.api.*;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.*;
import com.google.gson.Gson;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import org.gradle.api.tasks.compile.JavaCompile;
import org.jetbrains.annotations.NotNull;
import shadow.org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Comparator;

@SuppressWarnings("CodeBlock2Expr")
@NonNullApi
public class SpigotServerPlugin implements Plugin<Project> {

    public static final String CONFIGURATION_SPIGOT_PLUGIN = "spigotPlugin";

    @Override
    public void apply(Project project) {

        project.getPlugins().apply(SpigotBasePlugin.class);

        SpigotExtension spigotExtension = SpigotExtension.get(project);
        TaskContainer tasks = project.getTasks();

        //spigot server
        NamedDomainObjectProvider<Configuration> configurationSpigotPlugin = project.getConfigurations().register(CONFIGURATION_SPIGOT_PLUGIN, c -> {
            c.setCanBeConsumed(false);
            c.setCanBeResolved(true);
            c.setTransitive(false);
        });

        TaskProvider<Task> taskServerPrepare = tasks.register("spigotServerPrepare", t -> {
            t.setDescription("prepares the server");
            t.setGroup("spigot server");
        });

        TaskProvider<Copy> taskSpigotJar = configureSpigotDonwload(tasks, spigotExtension);
        TaskProvider<Download> taskDownloadPaperJar = configurePaperDownload(tasks, spigotExtension);



        TaskProvider<Sync> taskServerPlugins = tasks.register("spigotServerPlugins", Sync.class, t -> {
            t.setDescription("copies the plugins and dependencies");
            t.setGroup("spigot server");
            t.from(project.getLayout().getProjectDirectory().dir("libs")).include("*.jar");
            t.from(configurationSpigotPlugin);
            t.into(spigotExtension.server.spigotPlugins);
            t.getPreserve().exclude("*.jar");
            t.getPreserve().include(project.getName() + "/config.yml");
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

        tasks.register("spigotServerRun", JavaExec.class, t -> {
            t.setDescription("runs the spigot server");
            t.setGroup("spigot server");
            t.dependsOn(taskServerPrepare);
            t.workingDir(spigotExtension.server.root);
            t.classpath(spigotExtension.server.spigotJar);
            t.setMaxHeapSize(spigotExtension.server.memory.get());
            if(spigotExtension.server.nogui.get()) {
                t.args("nogui");
            }
        });

        project.getGradle().projectsEvaluated(gradle -> {
            if (tasks.findByPath("run") == null) {
                tasks.register("run", t -> t.dependsOn("spigotServerRun"));
            }
            SpigotExtension.ServerVersionFlavor flavor = spigotExtension.flavor.getOrElse(SpigotExtension.ServerVersionFlavor.SPIGOT);
            if(flavor == SpigotExtension.ServerVersionFlavor.SPIGOT) {
                taskServerPrepare.configure(t -> t.dependsOn(taskSpigotJar));
            } else if (flavor == SpigotExtension.ServerVersionFlavor.PAPER) {
                taskServerPrepare.configure(t -> t.dependsOn(taskDownloadPaperJar));
            }
        });
    }

    @NotNull
    private static TaskProvider<Copy> configureSpigotDonwload(TaskContainer tasks, SpigotExtension spigotExtension) {
        TaskProvider<SpigotBuildToolDownload> taskBuildToolDownload = tasks.register("spigotBuildToolDownload", SpigotBuildToolDownload.class);
        TaskProvider<SpigotBuildToolBuild> taskBuildToolBuild = tasks.register("spigotBuildToolBuild", SpigotBuildToolBuild.class, t -> {
            t.dependsOn(taskBuildToolDownload);
        });
        TaskProvider<Copy> taskServerJar = tasks.register("spigotBuildToolCopy", Copy.class, t -> {
            t.setDescription("copies the spigot server jar");
            t.setGroup("spigot flavor spigot");
            t.dependsOn(taskBuildToolBuild);
            t.from(spigotExtension.buildTool.build);
            t.into(spigotExtension.server.spigotJar.map(f -> f.getAsFile().getParentFile()));
            t.include("spigot*.jar");
            t.rename(s -> spigotExtension.server.spigotJar.get().getAsFile().getName());
        });
        return taskServerJar;
    }

    @NotNull
    private static TaskProvider<Download> configurePaperDownload(TaskContainer tasks, SpigotExtension spigotExtension) {
        return tasks.register("downloadPaperJar", Download.class, (Download t) -> {
            t.setDescription("downloads the Paper jar");
            t.setGroup("spigot flavor paper");
            t.dest(spigotExtension.server.spigotJar);
            t.onlyIfModified(true);
            t.doFirst(task -> {
                try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    String url = "https://api.papermc.io/v2/projects/paper/versions/" + spigotExtension.version.get() + "/builds";
                    try(CloseableHttpResponse res = httpClient.execute(new HttpGet(url))) {
                        assert res.getCode() == 200;
                        String content = EntityUtils.toString(res.getEntity());
                        Gson gson = new GsonBuilder()
                                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                .create();
                        PaperListBuildsResponse response = gson.fromJson(content, PaperListBuildsResponse.class);
                        PaperListBuildsResponse.BuildInfo build = response.builds.stream()
                                .max(Comparator.comparing(c -> c.build))
                                .orElseThrow(() -> new GradleException("No builds found for version"));
                        String buildUrl = String.format("%s/%s/downloads/%s", url, build.build, build.downloads.application.name);
                        System.out.printf("Downloading from %s", buildUrl);
                        t.src(buildUrl);
                    }
                } catch (IOException|ParseException e) {
                    throw new GradleException("Unable to fetch Paper version: " + e.getMessage());
                }
            });
        });
    }
}
