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
