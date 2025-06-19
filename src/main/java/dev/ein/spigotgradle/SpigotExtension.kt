package dev.ein.spigotgradle

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

open class SpigotExtension @Inject constructor(objects: ObjectFactory, projectLayout: ProjectLayout) {
    val flavor: Property<ServerVersionFlavor> = objects.property(ServerVersionFlavor::class.java)
    val version: Property<String> = objects.property(String::class.java)
    val serverRoot: DirectoryProperty = objects.directoryProperty().convention(projectLayout.projectDirectory.dir("server"))
    val acceptEula: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    val eulaTxt: RegularFileProperty = objects.fileProperty().convention(serverRoot.file("eula.txt"))
    var memory: Property<String> = objects.property(String::class.java).convention("1G")
    var nogui: Property<Boolean> = objects.property(Boolean::class.java).convention(false)

    var manageDependencies: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
}
