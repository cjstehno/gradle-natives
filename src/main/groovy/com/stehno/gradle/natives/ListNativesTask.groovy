/*
 * Copyright (C) 2017 Christopher J. Stehno <chris@stehno.com>
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

import com.stehno.gradle.natives.ext.NativesExtension
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import static com.stehno.gradle.natives.NativeLibResolver.resolveNames

/**
 * Gradle task used to list the native libraries resolved by the applied configuration.
 */
@CompileStatic
class ListNativesTask extends DefaultTask {

    ListNativesTask() {
        group = 'Natives'
        description = 'Lists all native libraries in the configured libraries.'
        dependsOn 'build'
    }

    @TaskAction @SuppressWarnings('GroovyUnusedDeclaration') void listNatives() {
        NativesExtension extension = project.extensions.findByType(NativesExtension) ?: new NativesExtension()

        logger.lifecycle "Native libraries found for configurations (${extension.configurations.join(', ')})..."

        resolveNames(project, extension).findAll { File art, List<NativeLibName> libs -> libs }.each { File art, List<NativeLibName> libs ->
            logger.lifecycle " - ${art.name}:"
            libs.each { lib ->
                logger.lifecycle "\t[${lib.platform.name()}] ${lib.name}"
            }
        }
    }
}
