import com.vanniktech.maven.publish.SonatypeHost

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
    id("signing")
    id("com.vanniktech.maven.publish") version "0.30.0"
    kotlin("jvm") version "2.0.21"
}

fun property(key: String): String = project.extra[key] as String

// Set in "../gradle.properties".
val authorName = property("author.username")
val authorEmail = property("author.email")
val projectName = property("project.name")

group = property("project.group_id")
val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra
var details = versionDetails()
var verPre = details.lastTag.substring(1, details.lastTag.lastIndexOf('.') + 1)
var verNumber =
    Integer.parseInt(details.lastTag.substring(details.lastTag.lastIndexOf('.') + 1, details.lastTag.length))
var verPost = if (details.branchName == null || details.branchName == "master") "" else "-" + details.branchName
if (!details.isCleanTag) {
    verPost = verPost + "-SNAPSHOT"
    verNumber++
}
version = verPre + verNumber + verPost

gradlePlugin {
    isAutomatedPublishing = true
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

signing {
    useGpgCmd()
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    pom {
        val projectUrl = "github.com/$authorName/$projectName"
        name.set(projectName)
        description.set("a gradle plugin to automate spigot plugin development")
        inceptionYear.set("2024")
        url.set("https://$projectUrl")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/mit-license.php")
            }
        }
        developers {
            developer {
                name.set(authorName)
                email.set(authorEmail)
            }
        }

        scm {
            url.set("https://$projectUrl/tree/main")
            connection.set("scm:git:git://$projectUrl.git")
            developerConnection.set("scm:git:ssh://$projectUrl.git")
        }
    }
}
