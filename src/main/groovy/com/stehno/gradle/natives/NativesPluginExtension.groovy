package com.stehno.gradle.natives

/**
 * Configuration extension for the Natives plugin.
 */
class NativesPluginExtension {

    /**
     * Defines the collection of jar name(s) (on the classpath) to be searched for native libraries.
     * Jar extension (.jar) will be added if omitted.
     */
    def jars

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