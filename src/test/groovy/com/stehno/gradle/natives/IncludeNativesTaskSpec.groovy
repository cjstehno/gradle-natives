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

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

class IncludeNativesTaskSpec extends Specification {

    @Rule public TemporaryFolder projectDir = new TemporaryFolder()

    def 'includeNatives with no dependencies should succeed'() {
        given:
        buildFile()

        when:
        BuildResult result = gradleRunner(['includeNatives']).build()

        then:
        totalSuccess result
    }

    @Unroll 'includeNatives (all platforms): v#version'() {
        given:
        buildFile([
            dependencies: '''
                compile 'org.lwjgl:lwjgl:3.0.0'
                compile 'org.lwjgl:lwjgl-platform:3.0.0:natives-windows'
                compile 'org.lwjgl:lwjgl-platform:3.0.0:natives-linux'
                compile 'org.lwjgl:lwjgl-platform:3.0.0:natives-osx'
            '''.stripIndent(),
            natives     : '''
                natives {
                    platforms = Platform.all()
                }
            '''.stripIndent()
        ])

        when:
        BuildResult result = gradleRunner(['includeNatives'], version).build()

        then:
        totalSuccess(result)
        nativeFilesExist projectDir.root, [
            'build/natives/lwjgl.dll',
            'build/natives/lwjgl32.dll',
            'build/natives/OpenAL.dll',
            'build/natives/jemalloc.dll',
            'build/natives/glfw.dll',
            'build/natives/glfw32.dll',
            'build/natives/jemalloc32.dll',
            'build/natives/OpenAL32.dll',
            'build/natives/liblwjgl.dylib',
            'build/natives/libjemalloc.dylib',
            'build/natives/libglfw.dylib',
            'build/natives/libopenal.dylib',
            'build/natives/libjemalloc.so',
            'build/natives/liblwjgl.so',
            'build/natives/libglfw.so',
            'build/natives/libopenal.so'
        ]

        where:
        version << ['3.4.1', '4.2.1']
    }

    @Unroll 'includeNatives (all by platforms): v#version'() {
        given:
        buildFile([
            dependencies: '''
                compile 'org.lwjgl:lwjgl:3.0.0'
                compile 'org.lwjgl:lwjgl-platform:3.0.0:natives-windows'
                compile 'org.lwjgl:lwjgl-platform:3.0.0:natives-linux'
                compile 'org.lwjgl:lwjgl-platform:3.0.0:natives-osx'
            '''.stripIndent(),
            natives     : '''
                natives {
                    platforms = Platform.all()
                    outputDir = 'natives/:platform'
                }
            '''.stripIndent()
        ])

        when:
        BuildResult result = gradleRunner(['includeNatives'], version).build()

        then:
        totalSuccess(result)
        nativeFilesExist projectDir.root, [
            'build/natives/windows/lwjgl.dll',
            'build/natives/windows/lwjgl32.dll',
            'build/natives/windows/OpenAL.dll',
            'build/natives/windows/jemalloc.dll',
            'build/natives/windows/glfw.dll',
            'build/natives/windows/glfw32.dll',
            'build/natives/windows/jemalloc32.dll',
            'build/natives/windows/OpenAL32.dll',
            'build/natives/osx/liblwjgl.dylib',
            'build/natives/osx/libjemalloc.dylib',
            'build/natives/osx/libglfw.dylib',
            'build/natives/osx/libopenal.dylib',
            'build/natives/linux/libjemalloc.so',
            'build/natives/linux/liblwjgl.so',
            'build/natives/linux/libglfw.so',
            'build/natives/linux/libopenal.so'
        ]

        where:
        version << ['3.4.1', '4.2.1']
    }

    @Unroll 'includeNatives (all by platforms, exclude): v#version'() {
        given:
        buildFile([
            dependencies: '''
                compile 'org.lwjgl:lwjgl:3.0.0'
                compile 'org.lwjgl:lwjgl-platform:3.0.0:natives-windows'
                compile 'org.lwjgl:lwjgl-platform:3.0.0:natives-linux'
                compile 'org.lwjgl:lwjgl-platform:3.0.0:natives-osx'
            '''.stripIndent(),
            natives     : '''
                natives {
                    platforms = Platform.all()
                    outputDir = 'natives/:platform'
                    libraries {
                        exclude = ['lwjgl.dll', 'liblwjgl.dylib']
                    }
                }
            '''.stripIndent()
        ])

        when:
        BuildResult result = gradleRunner(['includeNatives'], version).build()

        then:
        totalSuccess(result)
        nativeFilesExist projectDir.root, [
            'build/natives/windows/lwjgl32.dll',
            'build/natives/windows/OpenAL.dll',
            'build/natives/windows/jemalloc.dll',
            'build/natives/windows/glfw.dll',
            'build/natives/windows/glfw32.dll',
            'build/natives/windows/jemalloc32.dll',
            'build/natives/windows/OpenAL32.dll',
            'build/natives/osx/libjemalloc.dylib',
            'build/natives/osx/libglfw.dylib',
            'build/natives/osx/libopenal.dylib',
            'build/natives/linux/libjemalloc.so',
            'build/natives/linux/liblwjgl.so',
            'build/natives/linux/libglfw.so',
            'build/natives/linux/libopenal.so'
        ]
        nativeFilesDontExist projectDir.root, [
            'build/natives/windows/lwjgl.dll',
            'build/natives/osx/liblwjgl.dylib'
        ]

        where:
        version << ['3.4.1', '4.2.1']
    }

    private static boolean nativeFilesExist(final File root, Collection<String> paths) {
        paths.every { path -> new File(root, path).exists() }
    }

    private static boolean nativeFilesDontExist(final File root, Collection<String> paths) {
        paths.every { path -> !new File(root, path).exists() }
    }

    // TODO: use my Gradle-Testing project for this stuff

    private void buildFile(final Map<String, Object> config = [:]) {
        File buildFile = projectDir.newFile('build.gradle')
        buildFile.text = """
            import com.stehno.gradle.natives.ext.Platform

            plugins {
                id 'com.stehno.natives'
                id 'java'
            }
            repositories {
                jcenter()
            }
            dependencies {
                ${config.dependencies ?: ''}
            }
            ${config.natives ?: ''}
        """.stripIndent()
    }

    private GradleRunner gradleRunner(final List<String> args, final String version='3.4.1') {
        GradleRunner.create().withGradleVersion(version).withPluginClasspath().withDebug(true).withProjectDir(projectDir.root).withArguments(args)
    }

    private static boolean totalSuccess(final BuildResult result) {
        result.tasks.every { BuildTask task ->
            task.outcome == TaskOutcome.SUCCESS
        }
    }
}
