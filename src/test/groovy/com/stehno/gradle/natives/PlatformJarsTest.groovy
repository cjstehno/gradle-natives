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
