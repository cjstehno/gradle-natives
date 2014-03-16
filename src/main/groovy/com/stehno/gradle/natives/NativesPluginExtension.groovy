package com.stehno.gradle.natives
/**
 * Configuration extension for the Natives plugin.
 */
class NativesPluginExtension {

    // FIXME: allow custom platforms as string format 'os:extension'

    /**
     * Defines the collection of jar name(s) (on the classpath) to be searched for native libraries.
     * Jar extension (.jar) will be added if omitted.
     */
    def jars

    /**
     * Used to specify the set of platform natives to be processed by the plugin. By default, if this
     * property is not specified, all supported library types are processed.
     *
     * Accepts a single or collection of Platform enum values, or a single or collection of Platform OS
     * strings (as from Platform.os property). Strings and Platform objects may be mixed in the
     * property definition.
     */
    def platforms

    /**
     * Retrieves the collection of configured jars normalized for use by the plugin.
     *
     * @return a collection of jar name strings
     */
    final Set<String> configuredJars(){
        def libs = [] as Set<String>

        if( jars && jars instanceof Collection ){
            libs = jars.collect( normalizeName )

        } else if( jars ){
            libs << normalizeName(jars as String)
        }

        return libs
    }

    /**
     * Retrieves the collection of configured platform values for use by the plugin.
     *
     * @return a collection of Platform enum values.
     */
    final Set<Platform> configuredPlatforms(){
        def plats = [] as Set<Platform>

        if( !platforms ){
            plats.addAll( Platform.values() )

        } else if( platforms instanceof Collection ){
            platforms.each { p->
                def plat = asPlatform( p )
                if( plat ) plats << plat
            }

        } else {
            def plat = asPlatform( platforms )
            if( plat ) plats << plat
        }

        return plats
    }

    private Platform asPlatform( final plat ){
        plat instanceof Platform ? plat : Platform.fromOs( plat as String )
    }

    private normalizeName = { j->
        (j.endsWith('.jar') ? j : "${j}.jar") as String
    }
}