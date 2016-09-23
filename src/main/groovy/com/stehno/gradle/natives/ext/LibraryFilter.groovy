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
package com.stehno.gradle.natives.ext

import groovy.transform.TypeChecked

/**
 * Filter object used to limit the scope of the resolved libraries.
 */
@TypeChecked
class LibraryFilter {

    /**
     * Limits the resolved libraries to only those provided in the list.
     */
    Collection<String> include = []

    /**
     * Limits the resolved libraries to only those NOT provided in the list.
     */
    Collection<String> exclude = []
}
