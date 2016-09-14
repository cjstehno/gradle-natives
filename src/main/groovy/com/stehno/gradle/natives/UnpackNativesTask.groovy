/*
 * Copyright (C) 2016 Christopher J. Stehno <chris@stehno.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stehno.gradle.natives

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * Task used to unpack the native library files from project dependency jar files.
 */
class UnpackNativesTask extends DefaultTask {

    @Input
    PlatformJars platformJars

    UnpackNativesTask(){
        name = 'unpackNatives'
        group = 'Natives'
        description = 'Unpacks native library files from project dependency jar files.'
        dependsOn 'build'

        println project.natives
    }

    @TaskAction void unpackNatives(){
        // specific getter required - odd Input-related requirement
        getPlatformJars()?.each { Platform platform, JarFile jarFile->
            jarFile.entries().findAll { JarEntry je-> platform.acceptsExtension( je.name ) }.each { JarEntry jef->
                logger.info 'Unpacking: {}', jef.name

                project.file("${project.buildDir}/natives/${platform.os}/${jef.name}").bytes = jarFile.getInputStream(jef).bytes
            }
        }
    }
}