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

import org.gradle.api.Plugin
import org.gradle.api.Project

import java.util.jar.JarFile

/**
 * Gradle plugin providing assistance with managing the native components of Java-based libraries.
 */
class NativesPlugin implements Plugin<Project> {

    private static final String EXTENSION_NAME = 'natives'

    void apply( final Project project ){
        NativesPluginExtension extension = project.extensions.create(EXTENSION_NAME, NativesPluginExtension)

        // FIXME: do I need this?
        project.task 'unpackNatives', type:UnpackNativesTask

        configureUnpackNativesTask project, extension
    }

    private void configureUnpackNativesTask( final Project project, NativesPluginExtension extension ){
        project.tasks.withType(UnpackNativesTask){
            conventionMapping.map('platformJars'){
                def inputJars = new PlatformJars()

                def configuredJars = extension.configuredJars()
                if( configuredJars ){
                    extension.configuredPlatforms().each { platform->
                        File platformDir = project.file("build/natives/${platform.os}")

                        project.mkdir platformDir

                        project.files( project.configurations.compile ).findAll { jf-> jf.name in configuredJars }.each { njf ->
                            inputJars[platform] = new JarFile(njf)
                        }
                    }
                }

                return inputJars
            }
        }
    }
}

