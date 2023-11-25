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
import java.util.*

class JavetShell(
    private val options: Options,
) {
    fun run(): ExitCode {
        println("${Constants.Application.NAME} v${Constants.Application.VERSION} (${options.jsRuntimeType.name} ${options.jsRuntimeType.version})")
        println("Please input the script or press Ctrl+C to exit.")
        println()
        V8Host.getInstance(options.jsRuntimeType).createV8Runtime<V8Runtime>().use { v8Runtime ->
            Scanner(System.`in`).use { scanner ->
                val sb = StringBuilder()
                var isMultiline = false
                while (true) {
                    print(if (isMultiline) ">>> " else "> ")
                    try {
                        sb.appendLine(scanner.nextLine())
                        v8Runtime.getExecutor(sb.toString()).setResourceName(options.scriptName).execute<V8Value>()
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
                        break
                    } catch (t: Throwable) {
                        sb.clear()
                        isMultiline = false
                        println()
                        println(t.message)
                        println()
                    }
                }
            }
        }
        return ExitCode.NoError
    }
}