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

import org.junit.Test

import java.util.jar.JarFile

import static com.stehno.gradle.natives.Platform.LINUX
import static com.stehno.gradle.natives.Platform.WINDOWS
import static org.mockito.Mockito.mock

class PlatformJarsTest {

    @Test void 'put & each'(){
        def platformJars = new PlatformJars()

        def winJars = [jarFile('a.jar'), jarFile('b.jar')]
        def linJars = [jarFile('c.jar')]

        platformJars[WINDOWS] = winJars[0]
        platformJars[WINDOWS] = winJars[1]
        platformJars[LINUX] = linJars[0]

        assert platformJars.jars.size() == 2
        assert platformJars.jars[WINDOWS].size() == 2
        assert platformJars.jars[LINUX].size() == 1

        platformJars.each { Platform p, JarFile j ->
            if( p == WINDOWS ){
                assert j in winJars
            } else if( p == LINUX ){
                assert j in linJars
            } else {
                assert false
            }
        }
    }

    private JarFile jarFile( final String name ){
        mock(JarFile, name)
    }
}
