package dev.ein.spigotgradle.server.task;

import dev.ein.spigotgradle.SpigotExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class SpigotBuildToolDownload extends DefaultTask {

    @Input
    public Property<String> buildToolUrl = getProject().getObjects().property(String.class).convention("https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar");

    @OutputFile
    public RegularFileProperty buildToolJar = getProject().getObjects().fileProperty().convention(SpigotExtension.get(getProject()).buildTool.buildToolJar);

    public SpigotBuildToolDownload() {
        setDescription("Downloads the spigot BuildTool");
        setGroup("spigot");
    }

    @TaskAction
    public void execute() {
        try {
            URL website = new URL(buildToolUrl.get());
            try (InputStream in = website.openStream()) {
                Files.copy(in, buildToolJar.get().getAsFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new GradleException("IOException when downloading BuildTool jar", e);
        }
    }

    public Property<String> getBuildToolUrl() {
        return buildToolUrl;
    }

    public RegularFileProperty getBuildToolJar() {
        return buildToolJar;
    }
}
