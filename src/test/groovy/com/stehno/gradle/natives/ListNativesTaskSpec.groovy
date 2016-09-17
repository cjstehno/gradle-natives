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

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class ListNativesTaskSpec extends Specification {

    // TODO: test with different gradle versions: .withGradleVersion()

    @Rule public TemporaryFolder projectDir = new TemporaryFolder()

    def 'listNatives with no dependencies should succeed'() {
        given: 'an build file with no native configuration and no native dependencies'
        buildFile()

        when: 'the task is run'
        BuildResult result = gradleRunner(['listNatives']).build()

        then: 'the build should pass'
        totalSuccess result
    }

    def 'listNatives with native dependencies should list them (default config)'() {
        given:
        buildFile([
            dependencies: [/compile 'org.lwjgl.lwjgl:lwjgl:2.9.1'/]
        ])

        when: 'the task is run'
        BuildResult result = gradleRunner(['listNatives']).build()

        then:
        result.output.stripIndent().contains('''
            Native libraries found for configurations (compile, runtime)...
             - jinput-platform-2.0.5-natives-linux.jar:
            \t[LINUX] libjinput-linux.so
            \t[LINUX] libjinput-linux64.so
             - lwjgl-platform-2.9.1-natives-linux.jar:
            \t[LINUX] libopenal64.so
            \t[LINUX] liblwjgl.so
            \t[LINUX] liblwjgl64.so
            \t[LINUX] libopenal.so
        '''.stripIndent())
    }

    def 'listNatives with native dependencies should list them (windows)'() {
        given:
        buildFile([
            dependencies: [/compile 'org.lwjgl.lwjgl:lwjgl:2.9.1'/],
            natives:'''
                natives {
                    platforms = [Platform.WINDOWS]
                }
            '''.stripIndent()
        ])

        when: 'the task is run'
        BuildResult result = gradleRunner(['listNatives']).build()

        then:
        result.output.stripIndent().contains('''
            Native libraries found for configurations (compile, runtime)...
             - lwjgl-platform-2.9.1-natives-windows.jar:
            \t[WINDOWS] lwjgl.dll
            \t[WINDOWS] OpenAL64.dll
            \t[WINDOWS] OpenAL32.dll
            \t[WINDOWS] lwjgl64.dll
             - jinput-platform-2.0.5-natives-windows.jar:
            \t[WINDOWS] jinput-dx8_64.dll
            \t[WINDOWS] jinput-dx8.dll
            \t[WINDOWS] jinput-wintab.dll
            \t[WINDOWS] jinput-raw_64.dll
            \t[WINDOWS] jinput-raw.dll
        '''.stripIndent())
    }

    def 'listNatives with native dependencies should list them (windows,linux)'() {
        given:
        buildFile([
            dependencies: [/compile 'org.lwjgl.lwjgl:lwjgl:2.9.1'/],
            natives:'''
                natives {
                    platforms = [Platform.WINDOWS, Platform.LINUX]
                }
            '''.stripIndent()
        ])

        when: 'the task is run'
        BuildResult result = gradleRunner(['listNatives']).build()

        then:
        println result.output.stripIndent()
        result.output.stripIndent().contains('''
            Native libraries found for configurations (compile, runtime)...
             - lwjgl-platform-2.9.1-natives-windows.jar:
            \t[WINDOWS] lwjgl.dll
            \t[WINDOWS] OpenAL64.dll
            \t[WINDOWS] OpenAL32.dll
            \t[WINDOWS] lwjgl64.dll
             - jinput-platform-2.0.5-natives-linux.jar:
            \t[LINUX] libjinput-linux.so
            \t[LINUX] libjinput-linux64.so
             - lwjgl-platform-2.9.1-natives-linux.jar:
            \t[LINUX] libopenal64.so
            \t[LINUX] liblwjgl.so
            \t[LINUX] liblwjgl64.so
            \t[LINUX] libopenal.so
             - jinput-platform-2.0.5-natives-windows.jar:
            \t[WINDOWS] jinput-dx8_64.dll
            \t[WINDOWS] jinput-dx8.dll
            \t[WINDOWS] jinput-wintab.dll
            \t[WINDOWS] jinput-raw_64.dll
            \t[WINDOWS] jinput-raw.dll
        '''.stripIndent())
    }

    private void buildFile(final Map<String, Object> config = [:]) {
        File buildFile = projectDir.newFile('build.gradle')
        buildFile.text = """
            import com.stehno.gradle.natives.Platform

            plugins {
                id 'com.stehno.natives'
                id 'java'
            }
            repositories {
                jcenter()
            }
            dependencies {
                ${config.dependencies?.join('\\n') ?: ''}
            }
            ${config.natives ?: ''}
        """.stripIndent()
    }

    private GradleRunner gradleRunner(final List<String> args) {
        GradleRunner.create().withPluginClasspath().withProjectDir(projectDir.root).withArguments(args)
    }

    private static boolean totalSuccess(final BuildResult result) {
        result.tasks.every { BuildTask task ->
            task.outcome == TaskOutcome.SUCCESS
        }
    }
}
