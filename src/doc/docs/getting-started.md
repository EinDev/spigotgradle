# Getting started

## Installation

[![Maven Central](https://img.shields.io/maven-central/v/dev.ein/spigotgradle.svg)](https://maven-badges.herokuapp.com/maven-central/dev.ein/spigotgradle)


=== ":simple-apachegroovy: Groovy"
    ```groovy
    plugins {
        id "dev.ein.spigotgradle" version "{{ gradle.version }}"
    }
    ```

=== ":simple-kotlin: Kotlin"
    ```kotlin
    plugins {
        id("dev.ein.spigotgradle") version "{{ gradle.version }}"
    }
    ```

## Basic usage

The simplest possible project is the following:

=== ":simple-apachegroovy: Groovy"
    ```groovy
    import dev.ein.spigotgradle.ServerVersionFlavor
    
    spigot {
        version.set('1.20.4')
        flavor.set(ServerVersionFlavor.PAPER)
        manageDependencies.set(true)
        acceptEula.set(true)
    }
    ```

=== ":simple-kotlin: Kotlin"
    ```kotlin
    import dev.ein.spigotgradle.ServerVersionFlavor
    
    spigot {
        version = "1.20.4"
        flavor = ServerVersionFlavor.PAPER
        manageDependencies.set(true)
        acceptEula.set(true)
    }
    ```

Then you can add your Plugin class:

````java
package example.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Plugin;

@Plugin(name = "@@name@@", version = "@@version@@")
@ApiVersion(ApiVersion.Target.v1_20)
public class ExamplePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        
    }
}
````

And your plugin is ready to use. To spin up a server with that plugin, run

````
./gradlew spigotServerRun
````
