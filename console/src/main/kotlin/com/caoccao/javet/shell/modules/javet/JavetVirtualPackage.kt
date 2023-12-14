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

import com.caoccao.javet.interop.V8Runtime
import com.caoccao.javet.interop.callback.IJavetDirectCallable
import com.caoccao.javet.interop.callback.JavetCallbackContext
import com.caoccao.javet.interop.callback.JavetCallbackType
import com.caoccao.javet.javenode.modules.BaseJNCallable
import com.caoccao.javet.values.V8Value
import com.caoccao.javet.values.primitive.V8ValueString

class JavetVirtualPackage(
    private val v8Runtime: V8Runtime,
    private val packageName: String,
) : BaseJNCallable(), IJavetDirectCallable {
    override fun getCallbackContexts(): Array<JavetCallbackContext> {
        if (javetCallbackContexts == null) {
            javetCallbackContexts = arrayOf(
                JavetCallbackContext(
                    "getPackage",
                    this, JavetCallbackType.DirectCallNoThisAndResult,
                    IJavetDirectCallable.NoThisAndResult<Exception>(this::getPackage),
                ),
                JavetCallbackContext(
                    "name",
                    this, JavetCallbackType.DirectCallGetterAndNoThis,
                    IJavetDirectCallable.GetterAndNoThis<Exception>(this::getName)
                ),
                JavetCallbackContext(
                    "valid",
                    this, JavetCallbackType.DirectCallGetterAndNoThis,
                    IJavetDirectCallable.GetterAndNoThis<Exception>(this::isValid),
                ),
            )
        }
        return javetCallbackContexts
    }

    fun getName() = v8Runtime.createV8ValueString(packageName)

    fun getPackage(v8Values: Array<V8Value>): V8Value {
        if (v8Values.isNotEmpty()) {
            val v8Value = v8Values[0]
            if (v8Value is V8ValueString) {
                val childPackageName = v8Value.value
                if (childPackageName.isNotBlank()) {
                    return createJavetPackage(v8Runtime, "$packageName.$childPackageName")
                }
            }
        }
        return v8Runtime.createV8ValueUndefined()
    }

    fun isValid() = v8Runtime.createV8ValueBoolean(false)

    fun toV8Value(): V8Value {
        val v8ValueObject = v8Runtime.createV8ValueObject()
        v8ValueObject.bind(this)
        return v8ValueObject
    }
}