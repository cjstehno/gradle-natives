# Gradle Natives Plugin

A Gradle plugin to aid in working with Java-based projects that provide supporting native libraries.

## Build

    ./gradlew clean build

## Installation

To add the plugin to your project, add the following to your `build.gradle` file (or update the `buildscript` block if it already exists):

```groovy
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "gradle.plugin.com.stehno:gradle-natives:0.3.0"
  }
}

apply plugin: "com.stehno.natives"
```

Alternately, you can use the new plug definition block in Gradle 2.1 and beyond.

```groovy
plugins {
	id 'com.stehno.natives' version '0.3.0'
}
```

The plugin is compiled on Java 7.

## Usage

Without any additional configuration, the plugin will find all native libraries in all `compile` and `runtime` dependency configurations, for all platforms, and unpack them into 
the `build/natives` directory of your project. You can configure this behavior by adding a `natives` block to your `build.gradle` file. The default behavior has the following configuration:

```groovy
natives {
    configurations = ['compile', 'runtime']
    platforms = Platform.all()
    outputDir = 'natives'
}
```

A `libraries` Closure may also be added to filter the resolved libraries, such as:

```groovy
natives {
    configurations = ['compile', 'runtime']
    platforms = Platform.all()
    outputDir = 'natives'
    libraries {
        exclude = ['somelib.dll']
    }
}
```

There are two tasks provided by the plguin:

* `listNatives` - lists all of the native libraries resolved by the current configuration.
* `includeNatives` - includes (copies) the resolved native libraries into the configured output directory.

## Warning

This plugin only resolves native libraries that are on the project classpath as dependencies of the project (Gradle dependencies, either direct or transitive).

## References

* Site: http://cjstehno.github.io/gradle-natives
* Blog Post: http://coffeaelectronica.com/blog/2014/going-native-with-gradle.html


[![Build Status](https://drone.io/github.com/cjstehno/gradle-natives/status.png)](https://drone.io/github.com/cjstehno/gradle-natives/latest)
