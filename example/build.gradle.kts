import dev.ein.spigotgradle.ServerVersionFlavor

plugins {
    id("dev.ein.spigotgradle") // version "<your-version>"
}

group = "org.example"
version = "1.0-SNAPSHOT"

spigot {
    version = "1.20.4"
    flavor = ServerVersionFlavor.PAPER
    manageDependencies.set(true)
    acceptEula.set(true)
}
