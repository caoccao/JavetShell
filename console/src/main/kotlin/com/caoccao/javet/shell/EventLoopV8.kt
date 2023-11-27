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

package com.caoccao.javet.shell

import com.caoccao.javet.interop.V8Runtime
import com.caoccao.javet.javenode.JNEventLoop
import com.caoccao.javet.javenode.enums.JNModuleType
import com.caoccao.javet.shell.entities.Options

class EventLoopV8(
    v8Runtime: V8Runtime,
    options: Options,
) : BaseEventLoop(v8Runtime, options) {
    private var jnEventLoop: JNEventLoop? = null

    override fun start() {
        jnEventLoop = JNEventLoop(v8Runtime)
        jnEventLoop?.loadStaticModules(
            JNModuleType.Console,
            JNModuleType.Timers,
        )
        super.start()
    }

    override fun stop() {
        jnEventLoop?.await()
        jnEventLoop = null
        super.stop()
    }
}