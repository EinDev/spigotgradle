package dev.ein.spigotgradle.server

import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.ein.spigotgradle.ServerVersionFlavor
import dev.ein.spigotgradle.SpigotExtension
import dev.ein.spigotgradle.server.task.EulaAcceptTask
import dev.ein.spigotgradle.server.task.PaperDownloadTask
import dev.ein.spigotgradle.server.task.SpigotBuildToolBuild
import dev.ein.spigotgradle.server.task.SpigotBuildToolDownload
import org.gradle.api.*
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.*
import org.gradle.api.tasks.compile.JavaCompile
import shadow.org.apache.commons.io.FileUtils
import java.io.IOException
import java.nio.charset.StandardCharsets

@NonNullApi
class SpigotServerPlugin : Plugin<Project> {
    private val CONFIGURATION_SPIGOT_PLUGIN: String = "spigotPlugin"

    override fun apply(project: Project) {
        setupJavaPlugin(project)
        val taskShadowJar = setupShadowPlugin(project)

        val tasks = project.tasks

        //spigot server
        val configurationSpigotPlugin =
            project.configurations.register(CONFIGURATION_SPIGOT_PLUGIN) { c: Configuration ->
                c.isCanBeConsumed = false
                c.isCanBeResolved = true
                c.setTransitive(false)
            }

        val taskServerPlugins = tasks.register("spigotServerPlugins", Sync::class.java) { t: Sync ->
            t.description = "copies the plugins and dependencies"
            t.group = "spigot server"
            t.from(project.layout.projectDirectory.dir("libs")).include("*.jar")
            t.from(configurationSpigotPlugin)
            t.from(taskShadowJar)
            t.preserve.exclude("*.jar")
            t.preserve.include(project.name + "/config.yml")
        }

        val taskServerJarCopy = tasks.register("spigotServerJarCopy", Copy::class.java) { t ->
            t.description = "prepares the server"
            t.group = "spigot server"
        }
        val taskServerEulaAccept = tasks.register("spigotServerEulaAccept", EulaAcceptTask::class.java) { t ->
            t.eulaFile.fileProvider(taskServerPlugins.map { it2 -> it2.outputs.files.singleFile.resolve("eula.txt") })
        }

        val taskServerPrepare = tasks.register("spigotServerPrepare", Copy::class.java) { t ->
            t.dependsOn(taskServerPlugins)
            t.dependsOn(taskServerEulaAccept)
            t.dependsOn(taskServerJarCopy)
        }

        val taskSpigotServerRun = tasks.register("spigotServerRun", JavaExec::class.java) { t: JavaExec ->
            t.description = "runs the spigot server"
            t.group = "spigot server"
            t.dependsOn(taskServerPrepare)
            t.classpath(taskServerJarCopy)
        }



        if (tasks.findByPath("run") == null) {
            tasks.register("run") { t: Task -> t.dependsOn(taskSpigotServerRun) }
        }

        val ext: SpigotExtension = project.extensions.create("spigot", SpigotExtension::class.java)

        project.afterEvaluate {
            if (!ext.version.isPresent) throw GradleException("Variable 'spigot.version' is not set! Please set it to a valid version like 1.13, 1.14 or 1.15.")
            if (!ext.flavor.isPresent) ext.flavor.set(ServerVersionFlavor.SPIGOT)
            if (ext.manageDependencies.get()) {
                setupDependencies(project)
            }

            val serverJarProvider = when (ext.flavor.get()) {
                ServerVersionFlavor.SPIGOT -> configureSpigotDownload(tasks, project)
                ServerVersionFlavor.PAPER -> configurePaperDownload(tasks)
            }
            serverJarProvider.map { it.version.set(ext.version) }
            taskServerEulaAccept.map {
                it.onlyIf { ext.acceptEula.get() }
            }

            val serverJar = ext.serverRoot.file("server.jar")

            taskServerPlugins.map {
                it.into(ext.serverRoot.dir("plugins"))
            }

            taskServerJarCopy.map {
                it.from(serverJarProvider.map { sjp -> sjp.outputs.files.singleFile })
                it.rename(".*", serverJar.map { sj -> sj.asFile.name }.get())
            }

            taskSpigotServerRun.map {
                it.workingDir(ext.serverRoot)
                it.maxHeapSize = ext.memory.get()
                if (ext.nogui.get()) {
                    it.args("nogui")
                }
            }
        }
    }

    private fun setupDependencies(project: Project) {
        project.dependencies.add("compileOnly", "org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
        project.dependencies.add("compileOnly", "org.spigotmc:plugin-annotations:1.2.3-SNAPSHOT")
        project.dependencies.add("annotationProcessor", "org.spigotmc:plugin-annotations:1.2.3-SNAPSHOT")

        project.repositories.mavenCentral()
        project.repositories.maven { it.setUrl("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    }

    private fun setupJavaPlugin(project: Project): TaskProvider<JavaCompile> {
        project.pluginManager.apply(JavaPlugin::class.java)

        return project.tasks.named("compileJava", JavaCompile::class.java) { t: JavaCompile ->
            t.options.encoding = "UTF-8"
            t.sourceCompatibility = "17"
            t.targetCompatibility = "17"
            t.doLast {
                val file = project.layout.buildDirectory.file("classes/java/main/plugin.yml").get().asFile
                try {
                    var content = FileUtils.readFileToString(file, StandardCharsets.UTF_8)
                    content = content.replace("@@name@@", project.name)
                    content = content.replace("@@version@@", project.version.toString())
                    FileUtils.write(file, content, StandardCharsets.UTF_8)
                } catch (e: IOException) {
                    throw GradleException(e.message!!)
                }
            }
        }
    }

    private fun setupShadowPlugin(project: Project): TaskProvider<ShadowJar> {
        project.pluginManager.apply(ShadowPlugin::class.java)

        return project.tasks.named("shadowJar", ShadowJar::class.java) { t: ShadowJar ->
            t.exclude("META-INF/**")
            t.archiveFileName.set(String.format("%s-%s.jar", project.name, project.version))
        }
    }

    private fun configureSpigotDownload(
        tasks: TaskContainer,
        project: Project
    ): TaskProvider<SpigotBuildToolBuild> {
        val root = project.layout.buildDirectory.dir("spigotBuildTool")
        val buildToolJar = root.map { it.file("BuildTool.jar") }
        val taskBuildToolDownload =
            tasks.register("spigotBuildToolDownload", SpigotBuildToolDownload::class.java) { t ->
                t.buildToolJar.set(buildToolJar)
            }
        val workingDir = root.map { it.dir("build") }
        val taskBuildToolBuild =
            tasks.register("spigotBuildToolBuild", SpigotBuildToolBuild::class.java) { t ->
                t.workingDir.set(workingDir)
                t.buildToolJar.set(buildToolJar)
                t.dependsOn(taskBuildToolDownload)
            }
        return taskBuildToolBuild
    }

    private fun configurePaperDownload(
        tasks: TaskContainer
    ): TaskProvider<PaperDownloadTask> {
        return tasks.register("downloadPaperJar", PaperDownloadTask::class.java) { t: PaperDownloadTask ->
            t.description = "downloads the Paper jar"
            t.group = "spigot flavor paper"
        }
    }
}
