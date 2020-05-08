package spigot.gradle.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import spigot.gradle.SpigotExtension;

public class SpigotBuildToolBuild extends DefaultTask {

    @Input
    public Property<String> version = getProject().getObjects().property(String.class).convention(SpigotExtension.get(getProject()).version);
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
