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
import com.caoccao.javet.interop.callback.JavetBuiltInModuleResolver
import com.caoccao.javet.shell.entities.Options

class JavetShellNode(
    options: Options,
) : BaseJavetShell(options) {
    init {
        assert(options.jsRuntimeType.isNode) { "JS runtime type must be Node." }
    }

    override val prompt: String
        get() = "N > "

    override fun createEventLoop(v8Runtime: V8Runtime, options: Options): BaseEventLoop {
        return EventLoopNode(v8Runtime, options)
    }

    override fun registerPromiseRejectCallback(v8Runtime: V8Runtime) {
        v8Runtime.v8ModuleResolver = JavetBuiltInModuleResolver()
        v8Runtime.getExecutor(
            """const process = require('process');
                                    process.on('unhandledRejection', (reason, promise) => {
                                        console.error();
                                        console.error(reason.toString());
                                        console.error();
                                    });"""
        )?.executeVoid()
    }
}