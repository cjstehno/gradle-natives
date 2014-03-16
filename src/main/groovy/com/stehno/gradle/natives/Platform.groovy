package com.stehno.gradle.natives

import groovy.transform.ToString

/**
 * Defines the scope of platforms supported by the plugin.
 */
@ToString
enum Platform {
    WINDOWS( 'windows', ['.dll'] ),
    LINUX( 'linux', ['.so'] ),
    MAC( 'osx', ['.jnilib', '.dylib'] )

    final String os
    final Set<String> extensions = [] as Set<String>

    private Platform( final String os, final Collection<String> extensions ){
        this.os = os
        this.extensions.addAll( extensions )
    }

    /**
     * Used to determine whether or not the provided file name has an extension acceptable to the platform.
     *
     * @param name the file name
     * @return true, if the file extension is accepted by the platform
     */
    boolean acceptsExtension( final String name ){
        extensions.any {
            name.toLowerCase().endsWith( it )
        }
    }

    /**
     * Finds the Platform value for the specified OS, which must be the same name as the
     * OS value for the Platform.
     *
     * @param os the os name to be converted to a Plaform
     * @return the Platform value or null if not found.
     */
    static Platform fromOs( final String os ){
        values().find { it.os == os }
    }

    /**
     * Used to determine the current platform being run by the build.
     *
     * @return the platform for the current OS or null if not directly supported.
     */
    static Platform current(){
        Platform currPlat = null

        String osName = System.getProperty( 'os.name' ).toLowerCase()
        if( osName.startsWith( Platform.WINDOWS.os ) ){
            currPlat = Platform.WINDOWS

        } else if( osName.contains( Platform.MAC.os ) || osName.contains( 'mac' ) ){
            currPlat = Platform.MAC

        } else if( osName.contains( Platform.LINUX.os ) ){
            currPlat = Platform.LINUX
        }

        return currPlat
    }
}
