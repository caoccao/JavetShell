/*
 * Copyright (c) 2024-2025. caoccao.com Sam Cao
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

import com.caoccao.javet.interfaces.IJavetLogger

class ConsoleLogger(
    private val stringBuilder: StringBuilder = StringBuilder(),
) : IJavetLogger {
    var refresh: () -> Unit = {}

    private fun internalLog(message: String?) {
        if (stringBuilder.isNotEmpty() && stringBuilder.last() != '\n') {
            if (message.isNullOrEmpty() || message.first() != '\n') {
                stringBuilder.append('\n')
            }
        }
        stringBuilder.append(message)
        refresh()
    }

    override fun debug(message: String?) {
        internalLog(message)
    }

    override fun error(message: String?) {
        internalLog(message)
    }

    override fun error(message: String?, cause: Throwable?) {
        internalLog(message)
    }

    override fun info(message: String?) {
        internalLog(message)
    }

    override fun warn(message: String?) {
        internalLog(message)
    }
}