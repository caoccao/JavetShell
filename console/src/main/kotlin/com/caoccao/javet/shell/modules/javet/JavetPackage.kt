/*
 * Copyright (c) 2023-2024. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caoccao.javet.shell.modules.javet

import com.caoccao.javet.interfaces.IJavetUniFunction
import com.caoccao.javet.interop.V8Runtime
import com.caoccao.javet.values.V8Value

class JavetPackage(
    private val v8Runtime: V8Runtime,
    private val namedPackage: Package,
) : BaseJavetPackage(v8Runtime) {
    override fun getName() = namedPackage.name

    override fun isValid() = true

    override fun proxyGetStringGetterMap(): Map<String, IJavetUniFunction<String, out V8Value, Exception>> {
        if (stringGetterMap == null) {
            stringGetterMap = super.proxyGetStringGetterMap()
            stringGetterMap = stringGetterMap!! + mapOf(
                ".implementationTitle" to IJavetUniFunction<String, V8Value, Exception> { _: String ->
                    v8Runtime.createV8ValueString(namedPackage.implementationTitle)
                },
                ".implementationVersion" to IJavetUniFunction<String, V8Value, Exception> { _: String ->
                    v8Runtime.createV8ValueString(namedPackage.implementationVersion)
                },
                ".implementationVendor" to IJavetUniFunction<String, V8Value, Exception> { _: String ->
                    v8Runtime.createV8ValueString(namedPackage.implementationVendor)
                },
                ".sealed" to IJavetUniFunction<String, V8Value, Exception> { _: String ->
                    v8Runtime.createV8ValueBoolean(namedPackage.isSealed)
                },
                ".specificationTitle" to IJavetUniFunction<String, V8Value, Exception> { _: String ->
                    v8Runtime.createV8ValueString(namedPackage.specificationTitle)
                },
                ".specificationVersion" to IJavetUniFunction<String, V8Value, Exception> { _: String ->
                    v8Runtime.createV8ValueString(namedPackage.specificationVersion)
                },
                ".specificationVendor" to IJavetUniFunction<String, V8Value, Exception> { _: String ->
                    v8Runtime.createV8ValueString(namedPackage.specificationVendor)
                },
            )
        }
        return stringGetterMap!!
    }
}