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
    public final Plugin plugin = new Plugin();
    public final BuildTool buildTool = new BuildTool();
    public final Server server = new Server();

    public SpigotExtension(Project project) {
        super(project);
    }

    public class Plugin {

        public Property<Boolean> addRepo = project.getObjects().property(Boolean.class).convention(true);
        public Property<Boolean> addApi = project.getObjects().property(Boolean.class).convention(true);

    }

    public class BuildTool {

        public final DirectoryProperty root = project.getObjects().directoryProperty().convention(project.getLayout().getBuildDirectory().dir("spigotBuildTool"));
        public final RegularFileProperty buildToolJar = project.getObjects().fileProperty().convention(root.file("BuildTool.jar"));
        public final DirectoryProperty build = project.getObjects().directoryProperty().convention(root.dir("build"));

    }

    public class Server {

        public final DirectoryProperty root = project.getObjects().directoryProperty().convention(project.getLayout().getBuildDirectory().dir("spigotServer"));
        public final DirectoryProperty spigotPlugins = project.getObjects().directoryProperty().convention(root.dir("plugins"));
        public final RegularFileProperty spigotJar = project.getObjects().fileProperty().convention(root.file("spigot.jar"));
        public final RegularFileProperty eulaTxt = project.getObjects().fileProperty().convention(root.file("eula.txt"));

    }
}
