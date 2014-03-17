/*
 * Copyright (c) 2014 Christopher J. Stehno
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    @TaskAction void unpackNatives(){
        NativesPluginExtension natives = project.natives

        def nativeJars = natives.configuredJars()
        if( nativeJars ){
            natives.configuredPlatforms().each { platform->
                unpack( nativeJars, platform )
            }
        }
    }

    // TODO: figure out why this does not work unless method is public
    void unpack( final Collection<String> jars, final Platform platform ){
        File platformDir = project.file("build/natives/${platform.os}")

        project.mkdir platformDir
        logger.info 'Unpacking ({}) native libraries into {}...', platform, platformDir

        project.files( project.configurations.compile ).findAll { jf-> jf.name in jars }.each { njf->
            logger.info 'Unpacking {}...', njf

            inputs.file( njf )

            JarFile jarFile = new JarFile(njf)
            jarFile.entries().findAll { JarEntry je-> platform.acceptsExtension( je.name ) }.each { JarEntry jef->
                logger.info 'Unpacking: {}', jef.name

                String builtPath = "build/natives/${platform.os}/${jef.name}"

                outputs.file(builtPath)
                project.file(builtPath).bytes = jarFile.getInputStream(jef).bytes
            }
        }
    }
}