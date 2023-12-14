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
import com.caoccao.javet.sanitizer.antlr.JavaScriptParser
import com.caoccao.javet.sanitizer.exceptions.JavetSanitizerException
import com.caoccao.javet.sanitizer.parsers.JavaScriptStatementListParser
import com.caoccao.javet.shell.constants.Constants
import com.caoccao.javet.shell.entities.Options
import com.caoccao.javet.shell.enums.ExitCode
import com.caoccao.javet.shell.utils.JavetShellLogger
import com.caoccao.javet.values.V8Value
import sun.misc.Signal
import java.io.File
import java.util.*

abstract class BaseJavetShell(
    protected val options: Options,
) {
    companion object {
        const val PROMPT_STRING = "Please input the script or press Ctrl+C to exit."
    }

    protected abstract val prompt: String

    protected abstract fun createEventLoop(v8Runtime: V8Runtime, options: Options): BaseEventLoop

    fun execute(): ExitCode {
        println("${Constants.Application.NAME} v${Constants.Application.VERSION} (${options.jsRuntimeType.name} ${options.jsRuntimeType.version})")
        println(PROMPT_STRING)
        println()
        V8Host.getInstance(options.jsRuntimeType).createV8Runtime<V8Runtime>().use { v8Runtime ->
            v8Runtime.logger = JavetShellLogger()
            v8Runtime.converter = Constants.Javet.JAVET_PROXY_CONVERTER
            createEventLoop(v8Runtime, options).use { eventLoop ->
                Signal.handle(Signal("INT")) {
                    // Stop the event loop after Ctrl+C is pressed.
                    eventLoop.running = false
                }
                registerPromiseRejectCallback(v8Runtime)
                Scanner(System.`in`).use { scanner ->
                    val sb = StringBuilder()
                    var isESM = false
                    var isMultiline = false
                    var isBlockCompleted = false
                    while (eventLoop.running) {
                        print(if (isMultiline) ">>> " else prompt)
                        try {
                            val line = scanner.nextLine()
                            if (line == null) {
                                println()
                                eventLoop.running = false
                                break
                            } else if (line.isBlank()) {
                                isBlockCompleted = true
                            } else {
                                isBlockCompleted = false
                                sb.appendLine(line)
                            }
                            if (eventLoop.running) {
                                val codeString = sb.toString()
                                if (codeString.isNotEmpty()) {
                                    if (!isESM) {
                                        val parser = if (isBlockCompleted) {
                                            try {
                                                JavaScriptStatementListParser(codeString).parse()
                                            } catch (e: JavetSanitizerException) {
                                                null
                                            }
                                        } else {
                                            JavaScriptStatementListParser(codeString).parse()
                                        }
                                        val context = parser?.javaScriptStatementParsers?.first()?.context
                                        if (context != null && context.childCount > 0) {
                                            isESM = context.getChild(0) is JavaScriptParser.ImportStatementContext
                                        }
                                    }
                                    v8Runtime
                                        .getExecutor(codeString)
                                        .setResourceName(File(options.scriptName).absolutePath)
                                        .setModule(isESM && isBlockCompleted)
                                        .execute<V8Value>()
                                        .use { v8Value ->
                                            println(v8Value.toString())
                                        }
                                }
                                isMultiline = false
                            } else {
                                println()
                                break
                            }
                        } catch (e: JavetSanitizerException) {
                            if (isBlockCompleted) {
                                println()
                                println(e.message)
                                println()
                            }
                            isMultiline = !isBlockCompleted
                        } catch (e: JavetCompilationException) {
                            if (isBlockCompleted) {
                                println()
                                println(e.scriptingError.toString())
                                println()
                            }
                            isMultiline = !isBlockCompleted
                        } catch (e: JavetExecutionException) {
                            isMultiline = false
                            println()
                            println(e.scriptingError.toString())
                            println()
                        } catch (e: NoSuchElementException) {
                            println()
                            eventLoop.running = false
                            break
                        } catch (t: Throwable) {
                            isMultiline = false
                            println()
                            println(t.message)
                            println()
                        } finally {
                            if (!isMultiline) {
                                sb.clear()
                            }
                            if (isBlockCompleted) {
                                isESM = false
                            }
                            isBlockCompleted = false
                        }
                    }
                }
            }
        }
        return ExitCode.NoError
    }

    protected abstract fun registerPromiseRejectCallback(v8Runtime: V8Runtime)
}