/*
 * Copyright (c) 2023. caoccao.com Sam Cao
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
import com.caoccao.javet.interop.callback.IJavetDirectCallable
import com.caoccao.javet.interop.callback.JavetCallbackContext
import com.caoccao.javet.interop.callback.JavetCallbackType
import com.caoccao.javet.shell.constants.Constants
import com.caoccao.javet.shell.modules.BaseDirectProxyHandler
import com.caoccao.javet.values.V8Value
import com.caoccao.javet.values.primitive.V8ValueString

abstract class BaseJavetPackage(private val v8Runtime: V8Runtime) : BaseDirectProxyHandler(v8Runtime) {
    companion object {
        fun createPackageOrClass(v8Runtime: V8Runtime, name: String): V8Value {
            if (name.isNotBlank()) {
                try {
                    val clazz = Class.forName(name)
                    return Constants.Javet.JAVET_PROXY_CONVERTER.toV8Value(v8Runtime, clazz)
                } catch (_: Throwable) {
                }
                @Suppress("DEPRECATION")
                val namedPackage = Package.getPackage(name)
                if (namedPackage != null) {
                    return JavetPackage(v8Runtime, namedPackage).toV8Value()
                } else {
                    return JavetVirtualPackage(v8Runtime, name).toV8Value()
                }
            }
            return v8Runtime.createV8ValueUndefined()
        }
    }

    abstract fun getName(): String

    abstract fun isValid(): Boolean

    override fun proxyGet(target: V8Value?, property: V8Value, receiver: V8Value?): V8Value {
        var v8Value = super.proxyGet(target, property, receiver)
        if (v8Value.isUndefined) {
            if (property is V8ValueString) {
                val name = property.value
                if (name.isNotBlank()) {
                    v8Value = createPackageOrClass(v8Runtime, "${getName()}.$name")
                }
            }
        }
        return v8Value
    }

    override fun proxyGetStringGetterMap(): Map<String, IJavetUniFunction<String, out V8Value, Exception>> {
        if (stringGetterMap == null) {
            stringGetterMap = mapOf(
                ".getPackages" to IJavetUniFunction<String, V8Value, Exception> { propertyName: String ->
                    v8Runtime.createV8ValueFunction(
                        JavetCallbackContext(
                            propertyName,
                            this, JavetCallbackType.DirectCallNoThisAndResult,
                            IJavetDirectCallable.NoThisAndResult<Exception> { _: Array<V8Value>? ->
                                val prefix = "${getName()}."
                                val v8ValueArray = v8Runtime.createV8ValueArray()
                                Package.getPackages()
                                    .filter { it.name.startsWith(prefix) }
                                    .filter { !it.name.substring(prefix.length).contains(".") }
                                    .map { JavetPackage(v8Runtime, it).toV8Value() }
                                    .forEach {
                                        v8ValueArray.push(it)
                                    }
                                v8ValueArray
                            },
                        )
                    )
                },
                ".name" to IJavetUniFunction<String, V8Value, Exception> { _: String ->
                    v8Runtime.createV8ValueString(getName())
                },
                ".valid" to IJavetUniFunction<String, V8Value, Exception> { _: String ->
                    v8Runtime.createV8ValueBoolean(isValid())
                },
            )
        }
        return stringGetterMap!!
    }
}