buildscript {
    dependencies {
        classpath("com.google.code.gson:gson:2.10.1")
    }
}

plugins {
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish") version "1.3.0"
    id("com.palantir.git-version") version "3.1.0"
    id("maven-publish")
}


group = "dev.ein"
version = project.findProperty("spigotgradle.version") ?: System.getenv("VERSION")

gradlePlugin {
    plugins {
        create("SpigotBase") {
            id = "dev.ein.spigotgradle.base"
            displayName = "Base plugin for SpigotGradle"
            description = "Base plugin with shared code of SpigotGradle plugin and server"
            implementationClass = "dev.ein.spigotgradle.SpigotBasePlugin"
        }
        create("SpigotPlugin") {
            id = "dev.ein.spigotgradle.plugin"
            displayName = "Setup spigot repos and dependencies for plugin development"
            description = "This Plugin sets up the spigot repos and adds the spigot api dependency to the project automatically"
            implementationClass = "dev.ein.spigotgradle.plugin.SpigotPluginPlugin"
        }
        create("SpigotServer") {
            id = "dev.ein.spigotgradle.server"
            displayName = "Setup a spigot server for development"
            description = "Automatically builds the spigot jar using the spigot BuildTool, sets up a spigot server in /server and puts the plugins in the correct place"
            implementationClass = "dev.ein.spigotgradle.server.SpigotServerPlugin"
            dependencies {}
        }
    }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains:annotations:16.0.3")
    implementation("de.undercouch:gradle-download-task:5.6.0")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.4-alpha1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.github.johnrengelman.shadow:com.github.johnrengelman.shadow.gradle.plugin:7.1.2")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/zerjin-de-Ops/SpigotGradlePlugin")
            credentials {
                username = findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = findProperty("gpr.key")  as String? ?: System.getenv("TOKEN")
            }
        }
    }
}
