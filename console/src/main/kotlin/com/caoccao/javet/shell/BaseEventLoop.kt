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

import com.caoccao.javet.enums.V8AwaitMode
import com.caoccao.javet.interfaces.IJavetClosable
import com.caoccao.javet.interop.V8Runtime
import com.caoccao.javet.shell.constants.Constants
import com.caoccao.javet.shell.entities.Options
import java.util.concurrent.TimeUnit

abstract class BaseEventLoop(
    protected val v8Runtime: V8Runtime,
    protected val options: Options,
) : IJavetClosable, Runnable {
    private var daemonThread: Thread? = null

    @Volatile
    var running = false

    override fun close() {
        // Do nothing
    }

    override fun isClosed(): Boolean = !running

    override fun run() {
        while (running) {
            v8Runtime.await(V8AwaitMode.RunOnce)
            try {
                TimeUnit.MILLISECONDS.sleep(Constants.Application.AWAIT_INTERVAL_IN_MILLIS)
            } catch (t: Throwable) {
                // Ignore
            }
        }
    }

    open fun start() {
        running = true
        daemonThread = Thread(this)
        daemonThread?.start()
    }

    open fun stop() {
        running = false
        daemonThread?.join()
        daemonThread = null
    }
}