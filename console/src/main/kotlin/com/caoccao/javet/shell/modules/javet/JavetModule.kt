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

import com.caoccao.javet.interop.callback.IJavetDirectCallable
import com.caoccao.javet.interop.callback.JavetCallbackContext
import com.caoccao.javet.interop.callback.JavetCallbackType
import com.caoccao.javet.javenode.JNEventLoop
import com.caoccao.javet.javenode.modules.BaseJNModule
import com.caoccao.javet.sanitizer.utils.StringUtils
import com.caoccao.javet.shell.enums.JavetShellModuleType

class JavetModule(eventLoop: JNEventLoop) : BaseJNModule(eventLoop), IJavetDirectCallable {
    companion object {
        const val NAME = "javet"
    }

    override fun getType() = JavetShellModuleType.Javet

    override fun getCallbackContexts(): Array<JavetCallbackContext> {
        if (javetCallbackContexts == null) {
            javetCallbackContexts = arrayOf(
                JavetCallbackContext(
                    "gc",
                    this, JavetCallbackType.DirectCallNoThisAndNoResult,
                    IJavetDirectCallable.NoThisAndNoResult<Exception> { v8Runtime.lowMemoryNotification() },
                ),
                JavetCallbackContext(
                    "package",
                    this, JavetCallbackType.DirectCallGetterAndNoThis,
                    IJavetDirectCallable.GetterAndNoThis<Exception> {
                        JavetVirtualPackage(v8Runtime, StringUtils.EMPTY).toV8Value()
                    },
                ),
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

    override fun unbind() {
        v8Runtime.globalObject.delete(NAME)
    }
}
