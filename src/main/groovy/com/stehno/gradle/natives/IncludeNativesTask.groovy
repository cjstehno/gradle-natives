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

import com.stehno.gradle.natives.ext.NativesExtension
import com.stehno.gradle.natives.ext.Platform
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import static com.stehno.gradle.natives.NativeLibResolver.resolveFiles

/**
 * Gradle task used to include the resolved native libraries in the project build based on the current configuration.
 */
@CompileStatic
class IncludeNativesTask extends DefaultTask {

    IncludeNativesTask() {
        group = 'Natives'
        description = 'Includes the resolved native libraries in the build artifacts.'
    }

    @TaskAction @SuppressWarnings('GroovyUnusedDeclaration') void includeNatives() {
        NativesExtension extension = project.extensions.findByType(NativesExtension) ?: new NativesExtension()

        logger.info "Including native libraries found for configurations (${extension.configurations.join(', ')})..."

        resolveFiles(project, extension).findAll { File art, List<NativeLibFile> libs -> libs }.each { File art, List<NativeLibFile> libs ->
            logger.info " - ${art.name}:"
            libs.each { lib ->
                File outputDir = outputDir(lib.platform, extension.outputDir)
                if (!outputDir.exists()) outputDir.mkdirs()

                logger.info "\t[${lib.platform.name()}] ${lib.entry.name} --> $outputDir"

                new File(outputDir, lib.entry.name).bytes = lib.jar.getInputStream(lib.entry).bytes
            }
        }
    }

    private File outputDir(final Platform platform, final String dir) {
        String outd = dir.replaceAll(':platform', platform.os)
        new File(outd.startsWith('/') ? outd : "${project.buildDir}/$outd" as String)
    }
}
