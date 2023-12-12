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

class JavetPackage(private val v8Runtime: V8Runtime, val name: String) : BaseJNCallable(), IJavetDirectCallable {
    override fun getCallbackContexts(): Array<JavetCallbackContext> {
        if (javetCallbackContexts == null) {
            javetCallbackContexts = arrayOf(
                JavetCallbackContext(
                    "name",
                    this, JavetCallbackType.DirectCallGetterAndNoThis,
                    IJavetDirectCallable.GetterAndNoThis<Exception>(this::getName),
                )
            )
        }
        return javetCallbackContexts
    }

    fun getName() = v8Runtime.createV8ValueString(name)

    fun toV8Value(): V8Value {
        val v8ValueObject = v8Runtime.createV8ValueObject()
        v8ValueObject.bind(this)
        return v8ValueObject
    }
}