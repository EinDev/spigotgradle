package spigot.gradle;

import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;

public class SpigotExtension extends HasProject {

    public static SpigotExtension get(Project project) {
        return project.getConvention().getByType(SpigotExtension.class);
    }

    public final Property<String> version = project.getObjects().property(String.class);
    public final BuildTool buildTool = new BuildTool();
    public final Server server = new Server();

    public SpigotExtension(Project project) {
        super(project);
    }

    public class BuildTool {

        public DirectoryProperty root = project.getObjects().directoryProperty().convention(project.getLayout().getBuildDirectory().dir("spigotBuildTool"));
        public RegularFileProperty buildToolJar = project.getObjects().fileProperty().convention(root.file("BuildTool.jar"));
        public DirectoryProperty build = project.getObjects().directoryProperty().convention(root.dir("build"));

    }

    public class Server {

        public DirectoryProperty root = project.getObjects().directoryProperty().convention(project.getLayout().getBuildDirectory().dir("spigotServer"));
        public DirectoryProperty spigotPlugins = project.getObjects().directoryProperty().convention(root.dir("plugins"));
        public RegularFileProperty spigotJar = project.getObjects().fileProperty().convention(root.file("spigot.jar"));
        public RegularFileProperty eulaTxt = project.getObjects().fileProperty().convention(root.file("eula.txt"));

    }
}
