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

import com.stehno.gradle.natives.ext.DependencyFilter
import spock.lang.Specification
import spock.lang.Unroll

import static com.stehno.gradle.natives.NativeLibResolver.findNatives
import static com.stehno.gradle.natives.Platform.*

class NativeLibResolverSpec extends Specification {

    @Unroll def 'findNatives: #platform (no filters)'() {
        given:
        File file = new File(NativeLibResolverSpec.getResource("/lwjgl-platform-2.9.1-natives-${platform.os}.jar").toURI())

        when:
        def natives = findNatives(platform, file, new DependencyFilter()) { jar -> jar.name }

        then:
        natives.size() == results.size()
        natives.containsAll(results)

        where:
        platform || results
        WINDOWS  || ['lwjgl.dll', 'OpenAL64.dll', 'OpenAL32.dll', 'lwjgl64.dll']
        LINUX    || ['libopenal64.so', 'liblwjgl.so', 'liblwjgl64.so', 'libopenal.so']
        MAC      || ['openal.dylib', 'liblwjgl.jnilib']
    }

    @Unroll def 'findNatives: #platform (includes)'() {
        given:
        File file = new File(NativeLibResolverSpec.getResource("/lwjgl-platform-2.9.1-natives-${platform.os}.jar").toURI())

        when:
        def natives = findNatives(platform, file, new DependencyFilter(
            include: ['libopenal64.so', 'openal.dylib', 'OpenAL64.dll']
        )) { jar -> jar.name }

        then:
        natives.size() == results.size()
        natives.containsAll(results)

        where:
        platform || results
        WINDOWS  || ['OpenAL64.dll']
        LINUX    || ['libopenal64.so']
        MAC      || ['openal.dylib']
    }

    @Unroll def 'findNatives: #platform (excludes)'() {
        given:
        File file = new File(NativeLibResolverSpec.getResource("/lwjgl-platform-2.9.1-natives-${platform.os}.jar").toURI())

        when:
        def natives = findNatives(platform, file, new DependencyFilter(
            exclude: ['lwjgl.dll', 'libopenal64.so', 'openal.dylib']
        )) { jar -> jar.name }

        then:
        natives.size() == results.size()
        natives.containsAll(results)

        where:
        platform || results
        WINDOWS  || ['OpenAL64.dll', 'OpenAL32.dll', 'lwjgl64.dll']
        LINUX    || ['liblwjgl.so', 'liblwjgl64.so', 'libopenal.so']
        MAC      || ['liblwjgl.jnilib']
    }
}
