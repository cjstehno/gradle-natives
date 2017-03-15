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
package com.stehno.gradle.natives

import com.stehno.gradle.natives.ext.NativesExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Gradle plugin providing assistance with managing the native components of Java-based libraries.
 */
class NativesPlugin implements Plugin<Project> {

    void apply(final Project project) {
        project.extensions.create('natives', NativesExtension)

        project.task 'listNatives', type: ListNativesTask
        project.task 'includeNatives', type: IncludeNativesTask
    }
}
