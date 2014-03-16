package com.stehno.gradle.natives

import org.junit.Before
import org.junit.Test

class NativesPluginExtensionTest {

    private NativesPluginExtension pluginExtension

    @Before void before(){
        pluginExtension = new NativesPluginExtension()
    }

    @Test void 'empty'(){
        assert pluginExtension.configuredJars().empty
        assert pluginExtension.configuredPlatforms().containsAll( Platform.values() )
    }

    @Test void 'jars: single-string (no jar)'(){
        pluginExtension.jars = 'some-library'
        assertJars( 'some-library.jar' )
    }

    @Test void 'jars: single-string (with jar)'(){
        pluginExtension.jars = 'some-library.jar'
        assertJars( 'some-library.jar' )
    }

    @Test void 'jars: single-collection (no jar)'(){
        pluginExtension.jars = ['some-library']
        assertJars( 'some-library.jar' )
    }

    @Test void 'jars: single-collection (with jar)'(){
        pluginExtension.jars = ['some-library.jar']
        assertJars( 'some-library.jar' )
    }

    @Test void 'platforms: single-string'(){
        pluginExtension.platforms = 'windows'
        assertPlatforms( Platform.WINDOWS )
    }

    @Test void 'platforms: single-platform'(){
        pluginExtension.platforms = Platform.WINDOWS
        assertPlatforms( Platform.WINDOWS )
    }

    @Test void 'platforms: collection-string'(){
        pluginExtension.platforms = ['windows','osx']
        assertPlatforms( Platform.WINDOWS, Platform.MAC )
    }

    @Test void 'platforms: collection-platform'(){
        pluginExtension.platforms = [Platform.WINDOWS, Platform.MAC]
        assertPlatforms( Platform.WINDOWS, Platform.MAC )
    }

    @Test void 'platforms: collection-mixed'(){
        pluginExtension.platforms = ['windows', Platform.MAC, 'linux']
        assertPlatforms( Platform.WINDOWS, Platform.MAC, Platform.LINUX )
    }

    private void assertPlatforms( Platform... platforms ){
        def plats = pluginExtension.configuredPlatforms()
        assert plats.size() == platforms.size()
        assert plats.containsAll( platforms )
    }

    private void assertJars( String... jarNames ){
        def jars = pluginExtension.configuredJars()
        assert jars.size() == jarNames.size()
        assert jars.containsAll( jarNames )
    }
}
