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
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.artifacts.ResolvedDependency
import org.gradle.api.tasks.TaskAction

import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * TODO: document
 */
@CompileStatic
class ListNativesTask extends DefaultTask {

    /*
        FIXME: support
            - dependency includes
            - dependency excludes
            - lib include
            - lib exclude
     */

    ListNativesTask() {
        group = 'Natives'
        description = 'Lists all native libraries in the configured libraries.'
    }

    @TaskAction @SuppressWarnings('GroovyUnusedDeclaration') void listNatives() {
        NativesExtension extension = project.extensions.findByType(NativesExtension) ?: new NativesExtension()

        logger.lifecycle "Native libraries found for configurations (${extension.configurations.join(', ')})..."

        def foundLibs = [:]

        findDependencyArtifacts(extension.configurations).each { File artifactFile ->
            foundLibs[artifactFile.name] = []

            (extension.platforms as Collection<Platform>).each { Platform platform ->
                Set<String> nativeLibs = findNatives(platform, artifactFile) { JarEntry entry -> entry.name }
                nativeLibs.each { String lib ->
                    List<String> platLibs = foundLibs[artifactFile.name] as List<String>
                    platLibs << ("[${platform.name()}] $lib" as String)
                }
            }
        }

        foundLibs.findAll { k, v -> v }.each { art, libs ->
            logger.lifecycle " - $art:"
            libs.each { lib ->
                logger.lifecycle "\t$lib"
            }
        }
    }

    private static <T> Set<T> findNatives(final Platform platform, final File artifactFile, final Closure<T> extractor) {
        Set<T> libs = new HashSet<>()

        JarFile jar = new JarFile(artifactFile)
        jar.entries().findAll { JarEntry entry -> platform.acceptsExtension(entry.name) }.collect { entry ->
            libs << extractor.call(entry)
        }

        libs
    }

    private Set<File> findDependencyArtifacts(final Collection<String> configurations) {
        Set<File> coords = new HashSet<>()

        (configurations ?: project.configurations.names).each { String cname ->
            project.configurations.getByName(cname).resolvedConfiguration.firstLevelModuleDependencies.each { ResolvedDependency dep ->
                collectDependencies(coords, dep)
            }
        }

        coords
    }

    static void collectDependencies(final Set<File> found, final ResolvedDependency dep) {
        dep.moduleArtifacts.each { ResolvedArtifact artifact ->
            found << artifact.file
        }
        dep.children.each { child ->
            collectDependencies(found, child)
        }
    }
}
