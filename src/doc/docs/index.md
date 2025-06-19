# SpigotGradle

SpigotGradle is a plugin which automates the development of a spigot plugin.
Its main purpose is to save experienced developers the time of downloading server versions, configuring them and the necessary dependencies.

Due to how complex something like this can get, this plugin is highly opinionated.
Therefore, it is not recommended to migrate existing projects to it.
The main purpose of this plugin is bootstrapping small plugins quickly, it is not (yet?) suitable for complex plugins.


## Features

- Bootstrap a project with [minimal configuration](getting-started#basic-usage)
- Designed with CI/CD-Pipelines in mind, providing a solid base for automation
- Set up a fresh development environment in minutes
- Quick dependency setup even for plugins using NMS (coming soon)
- Automatic download os spigot using Build Tools
- Providing a local development server, fully integrated into gradle tasks

The goal for this tool is to let the developer never leave the environment of gradle tasks.
