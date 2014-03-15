package com.stehno.gradle.natives
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Gradle plugin providing assistance with managing the native components of Java-based libraries.
 */
class NativesPlugin implements Plugin<Project> {

    void apply( final Project project ){
        project.extensions.create( 'natives', NativesPluginExtension)

        project.task 'unpackNatives', type:UnpackNativesTask
    }
}