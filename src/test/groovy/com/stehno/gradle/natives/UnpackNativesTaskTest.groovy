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
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class UnpackNativesTaskTest {

    @Rule public TemporaryFolder projectDir = new TemporaryFolder()

    private Project project
    private File projectRoot

    @Before void before(){
        projectRoot = projectDir.newFolder()
        project = ProjectBuilder.builder().withProjectDir( projectRoot ).build()

        project.apply plugin:'java'
        project.apply plugin:NativesPlugin

        project.repositories {
            jcenter()
        }

        project.dependencies {
            compile 'org.lwjgl.lwjgl:lwjgl:2.9.1'
        }
    }

    @Test void 'identity'(){
        def task = project.tasks.unpackNatives

        assert task instanceof UnpackNativesTask
        assert task.name == 'unpackNatives'
        assert task.group == 'Natives'
        assert task.description == 'Unpacks native library files from project dependency jar files.'
    }

    @Test void 'usage: without config'(){
        def task = project.tasks.unpackNatives

        task.execute()

        assertNotExists 'build/natives/'
    }

    @Test void 'usage: single platform'(){
        project.natives {
            jars = ['lwjgl-platform-2.9.1-natives-windows']
            platforms = Platform.WINDOWS
        }

        def task = project.tasks.unpackNatives
        task.execute()

        assertUnpacked( 'windows', ['OpenAL32.dll', 'OpenAL64.dll', 'lwjgl.dll', 'lwjgl64.dll'] )

        assertNotExists 'build/natives/linux'
        assertNotExists 'build/natives/osx'
    }

    @Test void 'usage: undefined platform (all)'(){
        project.natives {
            jars = [
                'lwjgl-platform-2.9.1-natives-windows',
                'lwjgl-platform-2.9.1-natives-osx',
                'lwjgl-platform-2.9.1-natives-linux'
            ]
        }

        def task = project.tasks.unpackNatives
        task.execute()

        assertUnpacked( 'windows', ['OpenAL32.dll', 'OpenAL64.dll', 'lwjgl.dll', 'lwjgl64.dll'] )
        assertUnpacked( 'linux', ['liblwjgl.so', 'liblwjgl64.so', 'libopenal.so', 'libopenal64.so'] )
        assertUnpacked( 'osx', ['liblwjgl.jnilib','openal.dylib'] )
    }

    private void assertNotExists( final String relPath ){
        assert !new File( projectRoot, relPath ).exists()
    }

    private void assertUnpacked( final String platform, final Collection libs ){
        assert new File( projectRoot , 'build' ).exists()
        assert new File( projectRoot , 'build/natives' ).exists()

        def platDir = new File( projectRoot , "build/natives/$platform" )
        assert platDir.exists()

        def nativeFiles = platDir.listFiles()
        assert nativeFiles.size() == libs.size()
        assert nativeFiles.every { nf-> nf.name in libs }
    }
}
