# Gradle Natives Plugin

A Gradle plugin to aid in working with Java-based project that provide supporting native libraries.

## Build

`gradle clean build`

## Installation

To add the plugin to your project, add the following to your `build.gradle` file (or update the `buildscript` block if it already exists):

```groovy
buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath 'com.stehno:gradle-natives:0.2.1'
    }
}

apply plugin:'com.stehno.natives'
```

Alternately, you can use the new plug definition block in Gradle 2.1 and beyond.

```groovy
plugins {
	id 'com.stehno.natives' version '0.2.1'
}
```

The plugin is compiled on Java 7.

## Usage

To do anything useful with it, you need to configure it using the `natives` extension configuration, for example:

```groovy
natives {
    jars = [ 'lwjgl-platform-2.9.1-natives-windows' ]
    platforms = 'windows'
}
```

Which will find the specified jar in the project and extract the `.dll` files contained in it when the `unpackNatives` task is executed.

The `natives.jars` property accepts a single string or collection of strings representing names of jar files configured
on the project classpath (from other dependencies). If the string does not end with ".jar" the extension will be added.

The `platforms` property accepts a single string or single Platform enum value, as well as a collection of either (or both mixed). If no platforms
are specified (value left null), all supported platforms will be assumed.

Then to add the native libraries to the build, simply run:

```
gradle unpackNatives
```

Which will add the native libraries to the build under the directory `build/natives/PLATFORM` (where PLATFORM is the name
of the configured platform).

## References

* http://cjstehno.github.io/gradle-natives
* https://github.com/cjstehno/coffeaelectronica/wiki/Going-Native-with-Gradle


[![Build Status](https://drone.io/github.com/cjstehno/gradle-natives/status.png)](https://drone.io/github.com/cjstehno/gradle-natives/latest)

[ ![Download](https://api.bintray.com/packages/cjstehno/public/gradle-natives/images/download.svg) ](https://bintray.com/cjstehno/public/gradle-natives/_latestVersion)