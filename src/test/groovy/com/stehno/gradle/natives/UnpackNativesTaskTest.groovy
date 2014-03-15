package com.stehno.gradle.natives
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class UnpackNativesTaskTest {

    @Rule public TemporaryFolder projectDir = new TemporaryFolder()

    @Test void 'identity'(){
        Project project = ProjectBuilder.builder().build()
        project.apply plugin:'natives'

        def task = project.tasks.unpackNatives

        assert task instanceof UnpackNativesTask
        assert task.name == 'unpackNatives'
        assert task.group == 'Natives'
        assert task.description == 'Unpacks native library files from project dependency jar files.'
    }

    @Test void 'usage:basic'(){
        Project project = ProjectBuilder.builder().withProjectDir( projectDir.root ).build()

        // Project define (start)

        project.apply plugin:'java'
        project.apply plugin:'natives'

        project.repositories {
            jcenter()
        }

        project.dependencies {
            compile 'org.lwjgl.lwjgl:lwjgl:2.9.1'
        }

        project.natives {
            jars = ['lwjgl-platform-2.9.1-natives-windows'] // FIXME: make platform independent
            libraryExtension = '.dll'
            targetPlatform = 'windows'
        }

        // Project define (end)

        def task = project.tasks.unpackNatives

        task.execute()

        assert new File( projectDir.root , 'build' ).exists()
        assert new File( projectDir.root , 'build/natives' ).exists()
        assert new File( projectDir.root , 'build/natives/windows' ).exists()

        def nativeFiles = new File( projectDir.root , 'build/natives/windows' ).listFiles()
        assert nativeFiles.size() == 4

        def expectedNatives = ['OpenAL32.dll', 'OpenAL64.dll', 'lwjgl.dll', 'lwjgl64.dll']
        nativeFiles.each { nf-> nf.name in expectedNatives }
    }
}
