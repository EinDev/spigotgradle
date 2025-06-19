package dev.ein.spigotgradle.server.task

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import javax.inject.Inject

abstract class SpigotBuildToolDownload @Inject constructor(objects: ObjectFactory) : DefaultTask() {
    @Input
    val buildToolUrl: Property<String> = objects.property(
        String::class.java
    ).convention("https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar")

    @OutputFile
    val buildToolJar: RegularFileProperty = objects.fileProperty()

    init {
        group = "spigot flavor spigot"
        description = "Downloads the spigot BuildTool"
    }

    @TaskAction
    fun execute() {
        try {
            val website = URL(buildToolUrl.get())
            website.openStream().use { `in` ->
                Files.copy(`in`, buildToolJar.get().asFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        } catch (e: IOException) {
            throw GradleException("IOException when downloading BuildTool jar", e)
        }
    }
}
