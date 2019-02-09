# Eclipse Import Projects Plug-in

This plug-in allows import of projects using an Eclipse command-line parameter.

## Installation

Export plug-in to jar. Place the eclipse-import-projects-plugin JAR file in the eclipse/dropins folder.

## Usage

When launching Eclipse, add the `-import <root project folder>` command line parameter. This will
cause Eclipse to recursively search the supplied folder for .project files and import them into
the workspace. If they are already present in the workspace, they will be refreshed. The path supplied
must be an absolute path.

If you want exclude some projects you can use `-exclude <project name>` command line parameter.

You can supply multiple `-import` or `-exclude` directives to import multiple folders or exclude multiple projects from import. E.g. `-import <folder 1> <folder 2> -exclude <project 1> <project 2>`.

The plugin will log activity and any errors.

### Headless

The aforementioned usage will import the projects on eclipse startup and will utimately place you in the IDE.
To perform a headless-import that does not actually start the IDE, run with `-application eclipse-import-projects-plugin.application`.
On Windows use `eclipsec` instead of `eclipse` launcher.

```
eclipsec.exe -consolelog -nosplash -application eclipse-import-projects-plugin.application -data <workspace-folder> -import <import-folder> [ -exclude <project name> ]
```

## Supported Configurations

This plugin has been tested with Eclipse 4.10 and Java 8. 
It will probably work with other configurations but they haven't been tested.
