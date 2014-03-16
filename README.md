# Gradle Natives Plugin

A Gradle plugin to aid in working with Java-based project that provide supporting native libraries.

> WARNING: not ready for general use yet.


## Build

`gradle clean build`

## Installation

TBD

## Usage

To use the native plugin you first need to add it to your build:

```groovy
buildscript {
    repositories {
    }

    dependencies {
        // TBD
    }
}

apply plugin:'natives'
```

Then, to do anything useful with it, you need to configure it using the `natives` extension configuration, for example:

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
