package dev.ein.spigotgradle.server.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class EulaAcceptTask @Inject constructor(objects: ObjectFactory) : DefaultTask() {
    init {
        group = "spigot server"
        description = "Accepts the EULA.txt"
    }

    @get:OutputFile
    val eulaFile: RegularFileProperty = objects.fileProperty()

    @TaskAction
    fun acceptEula() {
        eulaFile.asFile.map { it.writeText("eula=true") }
    }
}