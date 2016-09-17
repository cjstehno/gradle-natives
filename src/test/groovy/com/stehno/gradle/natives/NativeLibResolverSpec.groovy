package com.stehno.gradle.natives

import spock.lang.Specification
import spock.lang.Unroll

import static com.stehno.gradle.natives.Platform.*

class NativeLibResolverSpec extends Specification {

    @Unroll def 'findNatives: #platform'() {
        given:
        File file = new File(NativeLibResolverSpec.getResource("/lwjgl-platform-2.9.1-natives-${platform.os}.jar").toURI())

        when:
        def natives = NativeLibResolver.findNatives(platform, file) { jar -> jar.name }

        then:
        natives.size() == results.size()
        natives.containsAll(results)

        where:
        platform || results
        WINDOWS  || ['lwjgl.dll', 'OpenAL64.dll', 'OpenAL32.dll', 'lwjgl64.dll']
        LINUX    || ['libopenal64.so', 'liblwjgl.so', 'liblwjgl64.so', 'libopenal.so']
        MAC      || ['openal.dylib', 'liblwjgl.jnilib']
    }
}
