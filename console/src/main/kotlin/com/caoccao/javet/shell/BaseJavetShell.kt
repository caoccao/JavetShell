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

import com.caoccao.javet.exceptions.JavetCompilationException
import com.caoccao.javet.exceptions.JavetExecutionException
import com.caoccao.javet.interop.V8Host
import com.caoccao.javet.interop.V8Runtime
import com.caoccao.javet.shell.constants.Constants
import com.caoccao.javet.shell.entities.Options
import com.caoccao.javet.shell.enums.ExitCode
import com.caoccao.javet.values.V8Value
import java.io.File
import java.util.*

abstract class BaseJavetShell(
    protected val options: Options,
) {
    protected var v8Runtime: V8Runtime? = null

    protected abstract val prompt: String

    protected abstract fun createEventLoop(v8Runtime: V8Runtime, options: Options): BaseEventLoop

    fun execute(): ExitCode {
        println("${Constants.Application.NAME} v${Constants.Application.VERSION} (${options.jsRuntimeType.name} ${options.jsRuntimeType.version})")
        println("Please input the script or press Ctrl+C to exit.")
        println()
        V8Host.getInstance(options.jsRuntimeType).createV8Runtime<V8Runtime>().use { v8Runtime ->
            createEventLoop(v8Runtime, options).use { eventLoop ->
                this.v8Runtime = v8Runtime
                registerPromiseRejectCallback()
                eventLoop.start()
                Scanner(System.`in`).use { scanner ->
                    val sb = StringBuilder()
                    var isMultiline = false
                    while (eventLoop.running) {
                        print(if (isMultiline) ">>> " else prompt)
                        try {
                            sb.appendLine(scanner.nextLine())
                            v8Runtime
                                .getExecutor(sb.toString())
                                .setResourceName(File(options.scriptName).absolutePath)
                                .setModule(options.module)
                                .execute<V8Value>()
                                .use { v8Value ->
                                    println(v8Value.toString())
                                }
                            sb.clear()
                            isMultiline = false
                        } catch (e: JavetCompilationException) {
                            isMultiline = true
                        } catch (e: JavetExecutionException) {
                            sb.clear()
                            isMultiline = false
                            println()
                            println(e.scriptingError.toString())
                            println()
                        } catch (e: NoSuchElementException) {
                            println()
                            eventLoop.running = false
                        } catch (t: Throwable) {
                            sb.clear()
                            isMultiline = false
                            println()
                            println(t.message)
                            println()
                        }
                    }

                }
                eventLoop.stop()
                this.v8Runtime = null
            }
        }
        return ExitCode.NoError
    }

    protected abstract fun registerPromiseRejectCallback()
}