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

import com.caoccao.javet.interop.V8Host
import com.caoccao.javet.interop.V8Runtime
import com.caoccao.javet.shell.constants.Constants
import com.caoccao.javet.shell.entities.Options
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

open class BaseTestSuite {
    protected lateinit var v8Runtimes: Array<V8Runtime>
    protected lateinit var eventLoops: Array<BaseEventLoop>

    @AfterEach
    protected fun afterEach() {
        eventLoops.forEach(BaseEventLoop::close)
        v8Runtimes.forEach { v8Runtime ->
            v8Runtime.lowMemoryNotification()
            System.gc()
            System.runFinalization()
            v8Runtime.close()
        }
    }

    @BeforeEach
    protected fun beforeEach() {
        v8Runtimes = arrayOf(
            V8Host.getNodeInstance().createV8Runtime(),
            V8Host.getV8Instance().createV8Runtime(),
        )
        v8Runtimes.forEach { v8Runtime ->
            v8Runtime.converter = Constants.Javet.JAVET_PROXY_CONVERTER
        }
        eventLoops = v8Runtimes.map { v8Runtime ->
            val option = Options(v8Runtime.jsRuntimeType, Constants.Options.SCRIPT_NAME_DEFAULT_VALUE)
            if (v8Runtime.jsRuntimeType.isNode) {
                EventLoopNode(v8Runtime, option)
            } else {
                EventLoopV8(v8Runtime, option)
            }
        }.toTypedArray()
    }
}