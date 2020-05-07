package spigot.gradle;

import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;

public class SpigotExtension {

    public static SpigotExtension get(Project project) {
        return project.getConvention().getByType(SpigotExtension.class);
    }

    private final Project project;
    public final Property<String> version;
    public final BuildTool buildTool;

    public SpigotExtension(Project project) {
        this.project = project;

        this.version = project.getObjects().property(String.class);
        this.buildTool = new BuildTool();
    }

    public class BuildTool {

        public DirectoryProperty buildTool = project.getObjects().directoryProperty().convention(project.getLayout().getBuildDirectory().dir("spigotBuildTool"));
        public RegularFileProperty buildToolJar = project.getObjects().fileProperty().convention(buildTool.file("BuildTool.jar"));
        public DirectoryProperty buildToolBuild = project.getObjects().directoryProperty().convention(buildTool.dir("build"));

    }
}
