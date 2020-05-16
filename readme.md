# SpigotGradle - a gradle plugin to automate spigot plugin development

This plugin completely automates:
- building of your plugin
- building the spigot server jar
- putting together a server
- starting the server

## How to use (basic)

1. create a new gradle project. Either use your favorite IDE or get gradle at [gradle.org](https://gradle.org/install/), execute `gradle init` and just use the default selection (press enter until it's done).

2. copy this into your `build.gradle`. Insert your own configuration into the `<brackets>`, though keep the `'` around it as these are strings.
```Groovy
plugins {
    id 'com.gitlab.firestar99.spigotgradle.plugin'
    id 'com.gitlab.firestar99.spigotgradle.server'
}

group '<your groupname here>'
version '<your version here>'

spigot {
    version '<spigot / minecraft version, like 1.15.2>'
}
```

3. add this to your `.gitignore`. This prevents you from committing your local spigot server.
```
/server
```

   If you want the server world and settings to be committed, add this to your `.gitignore` to prevent conflicts:
```
/server/spigot.jar
/server/plugins
/server/eula.txt
```

4. Start developing! Here are some final notes:
    - execute `./gradlew run` to build everything and start the spigot server
    - your java source code goes into `/src/main/java` just like in any other gradle or maven project
    - most IDEs have support for importing gradle projects, this usually allows for code recommendations and running the server directly from IDE

## How to use (advanced)

There are two plugins available:
- `Plugin` for projects in which you develop plugins in
- `Server` for projects where a spigot server should be started with plugins

Both need the spigot version configured:
```Groovy
spigot {
    version '<spigot / minecraft version, like 1.15.2>'
}
```

### the 'plugin' plugin
The 'plugin' plugin automatically configures the repositories and dependencies needed for spigot plugin development.

Apply the plugin with:
```Groovy
plugins {
    id 'com.gitlab.firestar99.spigotgradle.plugin'
}
```

You can configurate the plugin with:
```Groovy
spigot {
    version = '<version>' //spigot version

    plugin {
        repos = true //configurate spigot repos
        api = true //configurate spigot api dependency
    }
}
```

### the 'server' plugin:
The 'server' plugin builds the spigot jar, puts together a minecraft spigot server with your plugins and starts it up.

Apply the plugin with:
```Groovy
plugins {
    id 'com.gitlab.firestar99.spigotgradle.server'
}
```

You can configurate the plugin with:
```Groovy
spigot {
    version = '<version>' //spigot version

    buildTool {
        root = '$buildDir/spigotBuildTool' //root directory for building
        buildToolJar = '$root/BuildTool.jar' //where BuildTool jar should be put
        build = '$root/build' //the working directory of the BuildTool
        quiet = true //whether the BuiltTool output should be silenced
    }

    server {
        root = '$projectDir/server' //the root directory of the server
        spigotPlugins = '$root/plugins' //plugins directory of the server
        spigotJar = '$root/spigot.jar' //spigot jar of the server
        eulaTxt = '$root/eula.txt' //the eula.txt file to auto-accept
        memory = '4G' //the memory of the server, default is undefined (platform specific, usually 4G)
        nogui = true //pass the nogui parameter
    }
}
```
