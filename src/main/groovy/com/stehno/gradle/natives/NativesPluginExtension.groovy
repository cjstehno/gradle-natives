package com.stehno.gradle.natives

/**
 * Configuration extension for the Natives plugin.
 */
class NativesPluginExtension {

    /**
     * Defines the collection of jar names (on the classpath) to be searched for native libraries.
     */
    Collection<String> jars

    /**
     * The file extension of the native libraries for the platform.
     */
    String libraryExtension

    /**
     * The target build platform for which to find native libraries.
     */
    String targetPlatform
}

// FIXME: need to determine runtime platform type as default (os.name) = "Windows 7"
// FIXME: also lib ext
// FIXME: allow jars to be a string or collection