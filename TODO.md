- spock tests
- use gradle testing api
- test against 2.3 and later versions for compatibility
- bug fixes

- user guide/site with version updates and publishing
- code coverage and reporting
- new updated version of the blog post

- see about downloading the test libs during build ratehr than including in project

- new DSL

uses classpath scanning to find all the native files in the specified dependencies (or all) for the specified 
 platform

natives {
    dependencies.include = ['some-library']
    dependencies.exclude = ['some-bad-lib']
    libs.include = []
    libs.exclude = []
    platforms = ['windows', LINUX]
    outputDir = '/build/natives' (allows :platform replacement : /build/natives/:platform )
}

windows: '.dll'
mac: '.so'
linux: '.jnilib', '.dylib'

https://github.com/ronmamo/reflections