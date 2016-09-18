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

import com.stehno.gradle.natives.ext.LibraryFilter
import com.stehno.gradle.natives.ext.NativesExtension
import groovy.transform.CompileStatic
import groovy.transform.Immutable
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.artifacts.ResolvedDependency

import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * FIXME: document
 */
@CompileStatic
class NativeLibResolver {
    // TODO: this could do with some refactoring/cleanup

    static Map<File, List<NativeLibName>> resolveNames(final Project project, final NativesExtension extension) {
        Map<File, List<NativeLibName>> foundLibs = [:]

        findDependencyArtifacts(project, extension.configurations).each { File artifactFile ->
            foundLibs[artifactFile] = [] as List<NativeLibName>

            (extension.platforms as Collection<Platform>).each { Platform platform ->
                Set<String> nativeLibs = findNatives(platform, artifactFile, extension.libraries) { JarEntry entry -> entry.name }
                nativeLibs.each { String lib ->
                    (foundLibs[artifactFile] as List<NativeLibName>) << new NativeLibName(platform, lib)
                }
            }
        }

        foundLibs
    }

    static <T> Set<T> findNatives(final Platform platform, final File artifactFile, final LibraryFilter filter, final Closure<T> extractor) {
        Set<T> libs = new HashSet<>()

        JarFile jar = new JarFile(artifactFile)
        jar.entries().findAll { JarEntry entry ->
            platform.acceptsExtension(entry.name)
        }.findAll { entry ->
            !filter.include || (entry as JarEntry).name in filter.include
        }.findAll { entry ->
            !filter.exclude || !((entry as JarEntry).name in filter.exclude)
        }.collect { entry ->
            libs << extractor.call(entry)
        }

        libs
    }

    static Set<File> findDependencyArtifacts(final Project project, final Collection<String> configurations) {
        Set<File> coords = new HashSet<>()

        (configurations ?: project.configurations.names).each { String cname ->
            project.configurations.getByName(cname).resolvedConfiguration.firstLevelModuleDependencies.each { ResolvedDependency dep ->
                collectDependencies(coords, dep)
            }
        }

        coords
    }

    private static void collectDependencies(final Set<File> found, final ResolvedDependency dep) {
        dep.moduleArtifacts.each { ResolvedArtifact artifact ->
            found << artifact.file
        }
        dep.children.each { child ->
            collectDependencies(found, child)
        }
    }
}

@Immutable
class NativeLibName {

    Platform platform
    String name
}