package dev.ein.spigotgradle.server.task;

import dev.ein.spigotgradle.SpigotExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;

public class SpigotBuildToolBuild extends DefaultTask {

    @Input
    public Property<String> version = getProject().getObjects().property(String.class).convention(SpigotExtension.get(getProject()).version);
    public Property<Boolean> quiet = getProject().getObjects().property(Boolean.class).convention(SpigotExtension.get(getProject()).buildTool.quiet);
    @InputFile
    public RegularFileProperty buildToolJar = getProject().getObjects().fileProperty().convention(SpigotExtension.get(getProject()).buildTool.buildToolJar);

    @OutputDirectory
    public DirectoryProperty buildToolBuild = getProject().getObjects().directoryProperty().convention(SpigotExtension.get(getProject()).buildTool.build);

    public SpigotBuildToolBuild() {
        setDescription("Uses the spigot BuildTool to build a server jar");
        setGroup("spigot");
    }

    @TaskAction
    public void execute() {
        getProject().exec(e -> {
            e.workingDir(buildToolBuild);
            e.commandLine("java", "-jar", buildToolJar.get().getAsFile().getPath(), "--rev", version.get());
            if (quiet.get()) {
                getLogger().warn("Spigot BuildTool output is silenced. Building may take a couple minutes just wait patiently...");
                e.setStandardOutput(new OutputStream() {
                    @Override
                    public void write(int b) {

                    }

                    @Override
                    public void write(@NotNull byte[] b) {

                    }

                    @Override
                    public void write(@NotNull byte[] b, int off, int len) {

                    }

                    @Override
                    public void flush() {

                    }

                    @Override
                    public void close() {

                    }
                });
            }
        }).assertNormalExitValue().rethrowFailure();
    }

    public Property<String> getVersion() {
        return version;
    }

    public RegularFileProperty getBuildToolJar() {
        return buildToolJar;
    }

    public DirectoryProperty getBuildToolBuild() {
        return buildToolBuild;
    }
}
