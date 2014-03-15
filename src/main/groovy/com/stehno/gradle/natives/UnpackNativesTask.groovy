package com.stehno.gradle.natives

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * Task used to unpack the native library files from project dependency jar files.
 */
class UnpackNativesTask extends DefaultTask {

    UnpackNativesTask(){
        name = 'unpackNatives'
        group = 'Natives'
        description = 'Unpacks native library files from project dependency jar files.'
        dependsOn 'build'
    }

    // FIXME: depends on build
    @TaskAction void unpackNatives(){
        NativesPluginExtension natives = project.natives

        File platformDir = project.file("build/natives/${natives.targetPlatform}")

        project.mkdir platformDir
        logger.info 'Unpacking ({}) native libraries into {}...', natives.targetPlatform, platformDir

        // annoying side-effect of GStrings
        def nativeJars = natives.jars.collect { j->
            (j.endsWith('.jar') ? j : "${j}.jar") as String
        }

        project.files( project.configurations.compile ).findAll { jf-> jf.name in nativeJars }.each { njf->
            logger.info 'Unpacking {}...', njf

            JarFile jarFile = new JarFile(njf)
            jarFile.entries().findAll { JarEntry je-> je.name.endsWith(natives.libraryExtension) }.each { JarEntry jef->
                logger.info 'Unpacking: {}', jef.name
                project.file("build/natives/${natives.targetPlatform}/${jef.name}").bytes = jarFile.getInputStream(jef).bytes
            }
        }
    }
}