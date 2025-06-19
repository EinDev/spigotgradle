package dev.ein.spigotgradle.server.task

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile

interface ServerJarProvider {
    @get:OutputFile
    val serverJar: RegularFileProperty
    @get:Input
    val version: Property<String>
}