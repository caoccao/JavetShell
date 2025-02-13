/*
 * Copyright (c) 2023-2025. caoccao.com Sam Cao
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

import com.caoccao.javet.shell.constants.Constants
import com.caoccao.javet.shell.entities.Options
import com.caoccao.javet.shell.enums.ExitCode
import com.caoccao.javet.shell.enums.RuntimeType
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val argParser = ArgParser(Constants.Application.NAME)
    val debugPort by argParser.option(
        ArgType.Int,
        shortName = Constants.Options.DEBUG_PORT_SHORT_NAME,
        description = Constants.Options.DEBUG_PORT_DESCRIPTION,
    ).default(Constants.Options.DEBUG_PORT_DEFAULT_VALUE.toInt())
    val runtimeType by argParser.option(
        ArgType.Choice<RuntimeType>(),
        shortName = Constants.Options.JS_RUNTIME_TYPE_SHORT_NAME,
        description = Constants.Options.JS_RUNTIME_TYPE_DESCRIPTION,
    ).default(Constants.Options.JS_RUNTIME_TYPE_DEFAULT_TYPE)
    val scriptName by argParser.option(
        ArgType.String,
        shortName = Constants.Options.SCRIPT_NAME_SHORT_NAME,
        description = Constants.Options.SCRIPT_NAME_DESCRIPTION,
    ).default(Constants.Options.SCRIPT_NAME_DEFAULT_VALUE)
    val verbose by argParser.option(
        ArgType.Boolean,
        shortName = Constants.Options.VERBOSE_SHORT_NAME,
        description = Constants.Options.VERBOSE_DESCRIPTION,
    ).default(Constants.Options.VERBOSE_DEFAULT_VALUE)
    argParser.parse(args)
    val options = Options(
        debugPort.toUInt(),
        runtimeType.value,
        scriptName,
        verbose,
    )
    ByteArrayOutputStream().use { byteArrayOutputStream ->
        PrintStream(byteArrayOutputStream).use { printStream ->
            System.setErr(printStream)
            val javetShell = if (options.jsRuntimeType.isNode) {
                JavetShellNode(options)
            } else {
                JavetShellV8(options)
            }
            val exitCode =
                try {
                    javetShell.execute()
                } catch (t: Throwable) {
                    t.printStackTrace()
                    ExitCode.UnknownError
                }
            exitProcess(exitCode.code)
        }
    }
}
