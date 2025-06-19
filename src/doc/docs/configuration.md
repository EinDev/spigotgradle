# Configuration

````kotlin
spigot {
    // Can be PAPER or SPIGOT (your flavor is missing? Open an issue!)
    flavor = ServerVersionFlavor.PAPER
    // The server version to use, nothing else
    version = "1.20.4"
    // The root folder of the server
    serverRoot.set(project.layout.projectDirectory.dir("server"))
    // The file where the 
}
````

val spigotJar: RegularFileProperty = objects.fileProperty().convention(serverRoot.file(version.map { v: String? -> String.format("%s-%s.jar", flavor.get(), v) }))
val acceptEula: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
val eulaTxt: RegularFileProperty = objects.fileProperty().convention(serverRoot.file("eula.txt"))
var memory: Property<String> = objects.property(String::class.java).convention("1G")
var nogui: Property<Boolean> = objects.property(Boolean::class.java).convention(false)

    var manageDependencies: Property<Boolean> = objects.property(Boolean::class.java).convention(false)