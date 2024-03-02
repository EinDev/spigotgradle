package dev.ein.spigotgradle;

import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.internal.provider.AbstractReadOnlyProvider;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SpigotExtension extends HasProject {

    public static SpigotExtension get(Project project) {
        return project.getConvention().getByType(SpigotExtension.class);
    }

    public final Property<ServerVersionFlavor> flavor = project.getObjects().property(ServerVersionFlavor.class);
    public final Property<String> version = project.getObjects().property(String.class);
    public final Plugin plugin = new Plugin();
    public final BuildTool buildTool = new BuildTool();
    public final Server server = new Server();

    public SpigotExtension(Project project) {
        super(project);
    }

    public class Plugin {

        public Property<Boolean> repos = project.getObjects().property(Boolean.class).convention(true);
        public Property<Boolean> api = project.getObjects().property(Boolean.class).convention(true);

        public boolean getRepos() {
            return repos.get();
        }

        public void setRepos(boolean value) {
            repos.set(value);
        }

        public void setAddRepo(Provider<? extends Boolean> provider) {
            repos.set(provider);
        }

        public boolean getApi() {
            return api.get();
        }

        public void setApi(boolean value) {
            api.set(value);
        }

        public void setAddApi(Provider<? extends Boolean> provider) {
            api.set(provider);
        }
    }

    public class BuildTool {

        public final DirectoryProperty root = project.getObjects().directoryProperty().convention(project.getLayout().getBuildDirectory().dir("spigotBuildTool"));
        public final RegularFileProperty buildToolJar = project.getObjects().fileProperty().convention(root.file("BuildTool.jar"));
        public final DirectoryProperty build = project.getObjects().directoryProperty().convention(root.dir("build"));
        public final Property<Boolean> quiet = project.getObjects().property(Boolean.class).convention(true);

        public Directory getRoot() {
            return root.get();
        }

        public void setRoot(@Nullable Directory value) {
            root.set(value);
        }

        public void setRoot(Provider<? extends Directory> provider) {
            root.set(provider);
        }

        public RegularFile getBuildToolJar() {
            return buildToolJar.get();
        }

        public void setBuildToolJar(@Nullable RegularFile value) {
            buildToolJar.set(value);
        }

        public void setBuildToolJar(Provider<? extends RegularFile> provider) {
            buildToolJar.set(provider);
        }

        public Directory getBuild() {
            return build.get();
        }

        public void setBuild(@Nullable Directory value) {
            build.set(value);
        }

        public void setBuild(Provider<? extends Directory> provider) {
            build.set(provider);
        }

        public boolean getQuiet() {
            return quiet.get();
        }

        public void setQuiet(boolean value) {
            quiet.set(value);
        }

        public void setQuiet(Provider<? extends Boolean> provider) {
            quiet.set(provider);
        }
    }

    public class Server {

        public final DirectoryProperty root = project.getObjects().directoryProperty().convention(project.getLayout().getProjectDirectory().dir("server"));
        public final DirectoryProperty spigotPlugins = project.getObjects().directoryProperty().convention(root.dir("plugins"));
        public final RegularFileProperty spigotJar = project.getObjects().fileProperty().convention(root.file(version.map(v -> String.format("%s-%s.jar", flavor.get(), v))));
        public final RegularFileProperty eulaTxt = project.getObjects().fileProperty().convention(root.file("eula.txt"));
        public Property<String> memory = project.getObjects().property(String.class).convention("1G");
        public Property<Boolean> nogui = project.getObjects().property(Boolean.class).convention(false);

        public Directory getRoot() {
            return root.get();
        }

        public void setRoot(@Nullable Directory value) {
            root.set(value);
        }

        public void setRoot(Provider<? extends Directory> provider) {
            root.set(provider);
        }

        public Directory getSpigotPlugins() {
            return spigotPlugins.get();
        }

        public void setSpigotPlugins(@Nullable Directory value) {
            spigotPlugins.set(value);
        }

        public void setSpigotPlugins(Provider<? extends Directory> provider) {
            spigotPlugins.set(provider);
        }

        public RegularFile getSpigotJar() {
            return spigotJar.get();
        }

        public void setSpigotJar(@Nullable RegularFile value) {
            spigotJar.set(value);
        }

        public void setSpigotJar(Provider<? extends RegularFile> provider) {
            spigotJar.set(provider);
        }

        public RegularFile getEulaTxt() {
            return eulaTxt.get();
        }

        public void setEulaTxt(@Nullable RegularFile value) {
            eulaTxt.set(value);
        }

        public void setEulaTxt(Provider<? extends RegularFile> provider) {
            eulaTxt.set(provider);
        }

        public String getMemory() {
            return memory.get();
        }

        public void setMemory(@Nullable String value) {
            memory.set(value);
        }

        public void setMemory(Provider<? extends String> provider) {
            memory.set(provider);
        }

        public boolean getNogui() {
            return nogui.get();
        }

        public void setNogui(boolean value) {
            nogui.set(value);
        }

        public void setNogui(Provider<? extends Boolean> provider) {
            nogui.set(provider);
        }
    }

    public String getFlavor() {
        return flavor.toString();
    }

    public void setFlavor(@Nullable ServerVersionFlavor value) {
        this.flavor.set(value);
    }

    public void setFlavor(Provider<? extends ServerVersionFlavor> provider) {
        this.flavor.set(provider);
    }

    public String getVersion() {
        return version.get();
    }

    public void setVersion(@Nullable String value) {
        version.set(value);
    }

    public void setVersion(Provider<? extends String> provider) {
        version.set(provider);
    }

    public enum ServerVersionFlavor {
        SPIGOT(),
        PAPER();

        ServerVersionFlavor() {}
    }
}
