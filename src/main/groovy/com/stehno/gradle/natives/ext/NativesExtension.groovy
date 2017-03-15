/*
 * Copyright (C) 2017 Christopher J. Stehno <chris@stehno.com>
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
package com.stehno.gradle.natives.ext

import groovy.transform.TypeChecked

/**
 * DSL Extension for configuring the native library resolution.
 */
@TypeChecked
class NativesExtension {

    private LibraryFilter libraries = new LibraryFilter()

    /**
     * Specifies the build configurations which will be scanned for native libraries. The default is to search the "compile" and "runtime"
     * configurations.
     */
    Collection<String> configurations = ['compile', 'runtime']

    /**
     * Used to specify the OS platforms whose libraries will be resolved. All platforms are retrieved by default.
     */
    Collection<Platform> platforms = Platform.all()

    /**
     * Used to specify the native library output directory. Defaults to <code>natives</code>.
     *
     * A relative path will be based on the project build directory.
     *
     * A replacement token <code>:platform</code> may be added which will be replaced by the platform value for the each native library.
     */
    String outputDir = 'natives'

    /**
     * Used to apply a filter to the search. No filtering by default.
     */
    void libraries(@DelegatesTo(LibraryFilter) Closure closure) {
        def filter = new LibraryFilter()
        closure.delegate = filter
        closure.call()
        libraries = filter
    }

    LibraryFilter getLibraries() {
        libraries
    }
}
