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

import com.caoccao.javet.interop.callback.IJavetDirectCallable
import com.caoccao.javet.interop.callback.JavetCallbackContext
import com.caoccao.javet.interop.callback.JavetCallbackType
import com.caoccao.javet.javenode.JNEventLoop
import com.caoccao.javet.javenode.modules.BaseJNModule
import com.caoccao.javet.shell.enums.JavetShellModuleType
import com.caoccao.javet.values.V8Value
import com.caoccao.javet.values.primitive.V8ValueString

class JavetModule(eventLoop: JNEventLoop) : BaseJNModule(eventLoop), IJavetDirectCallable {
    companion object {
        const val NAME = "javet"
    }

    override fun getType() = JavetShellModuleType.Javet

    override fun getCallbackContexts(): Array<JavetCallbackContext> {
        if (javetCallbackContexts == null) {
            javetCallbackContexts = arrayOf(
                JavetCallbackContext(
                    "getPackage",
                    this, JavetCallbackType.DirectCallNoThisAndResult,
                    IJavetDirectCallable.NoThisAndResult<Exception>(this::getPackage),
                )
            )
        }
        return javetCallbackContexts
    }

    override fun bind() {
        v8Runtime.createV8ValueObject().use { v8ValueObject ->
            bind(v8ValueObject)
            v8Runtime.globalObject.set(NAME, v8ValueObject)
        }
    }

    fun getPackage(v8Values: Array<V8Value>?): V8Value {
        if (!v8Values.isNullOrEmpty()) {
            val v8Value = v8Values[0]
            if (v8Value is V8ValueString) {
                return BaseJavetPackage.createPackageOrClass(v8Runtime, v8Value.value)
            }
        }
        return v8Runtime.createV8ValueUndefined()
    }

    override fun unbind() {
        v8Runtime.globalObject.delete(NAME)
    }
}
