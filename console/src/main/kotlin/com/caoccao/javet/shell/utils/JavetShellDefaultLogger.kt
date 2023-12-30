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

package com.caoccao.javet.shell.utils

import com.caoccao.javet.interfaces.IJavetLogger

class JavetShellDefaultLogger : IJavetLogger {
    companion object {
        const val BLACK = "\u001b[30m"
        const val BLUE = "\u001b[34m"
        const val CYAN = "\u001b[36m"
        const val GREEN = "\u001b[32m"
        const val MAGENTA = "\u001b[35m"
        const val RED = "\u001b[31m"
        const val WHITE = "\u001b[37m"
        const val YELLOW = "\u001b[33m"

        const val BRIGHT_BLACK = "\u001b[30;1m"
        const val BRIGHT_BLUE = "\u001b[34;1m"
        const val BRIGHT_CYAN = "\u001b[36;1m"
        const val BRIGHT_GREEN = "\u001b[32;1m"
        const val BRIGHT_MAGENTA = "\u001b[35;1m"
        const val BRIGHT_RED = "\u001b[31;1m"
        const val BRIGHT_WHITE = "\u001b[37;1m"
        const val BRIGHT_YELLOW = "\u001b[33;1m"

        const val RESET = "\u001b[0m"
    }

    override fun debug(message: String) {
        println("$BRIGHT_YELLOW$message$RESET")
    }

    override fun error(message: String) {
        println("$BRIGHT_RED$message$RESET")
    }

    override fun error(message: String, cause: Throwable) {
        println("$BRIGHT_RED$message$RESET")
    }

    override fun info(message: String) {
        println(message)
    }

    override fun warn(message: String) {
        println("$BRIGHT_CYAN$message$RESET")
    }
}