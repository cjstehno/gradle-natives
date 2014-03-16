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
        project.apply plugin:'natives'

        project.repositories {
            jcenter()
        }

        project.dependencies {
            compile 'org.lwjgl.lwjgl:lwjgl:2.9.1'
        }
    }

    @Test void 'identity'(){
        project.apply plugin:'natives'

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
