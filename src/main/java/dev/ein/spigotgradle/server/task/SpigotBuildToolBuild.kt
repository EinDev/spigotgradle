package dev.ein.spigotgradle.server.task

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecSpec
import javax.inject.Inject

abstract class SpigotBuildToolBuild @Inject constructor(objects: ObjectFactory) : Exec(), ServerJarProvider {
    @InputFile
    val buildToolJar: RegularFileProperty = objects.fileProperty()

    @OutputDirectory
    val workingDir: DirectoryProperty = objects.directoryProperty()

    init {
        group = "spigot flavor spigot"
        description = "Uses the spigot BuildTool to build a server jar"
    }

    @TaskAction
    fun execute() {
        serverJar.set(workingDir.map { it.file("server.jar") })
        val serverJarName = serverJar.map { it.asFile.name }
        val serverJarFolder = serverJar.map { it.asFile.parent }
        project.exec { e: ExecSpec ->
            e.workingDir(workingDir)
            e.commandLine(
                "java",
                "-jar", buildToolJar.map { it.asFile.path },
                "--rev", version.get(),
                "--final-name", serverJarName,
                "--output-dir", serverJarFolder
            )
        }.assertNormalExitValue().rethrowFailure()
    }
}
