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

import com.caoccao.javet.enums.JSRuntimeType
import com.caoccao.javet.interop.V8Host
import com.caoccao.javet.interop.V8Runtime
import com.caoccao.javet.shell.constants.Constants
import com.caoccao.javet.shell.enums.ExitCode
import com.caoccao.javet.values.V8Value
import java.util.*

class JavetShell(
    private val jsRuntimeType: JSRuntimeType,
) {
    fun run(): ExitCode {
        println("${Constants.Application.NAME} v${Constants.Application.VERSION} (${jsRuntimeType.name} ${jsRuntimeType.version})")
        println("Please input the script or '.exit' to exit.")
        println()
        V8Host.getInstance(jsRuntimeType).createV8Runtime<V8Runtime>().use { v8Runtime ->
            Scanner(System.`in`).use { scanner ->
                while (true) {
                    print("> ")
                    val inputLine = scanner.nextLine()
                    if (inputLine == ".exit") {
                        break
                    }
                    try {
                        v8Runtime.getExecutor(inputLine).execute<V8Value>().use { v8Value ->
                            println(v8Value.toString())
                        }
                    } catch (t: Throwable) {
                        println(t.message)
                    }
                }
            }
        }
        return ExitCode.NoError
    }
}