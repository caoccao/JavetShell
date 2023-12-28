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

package com.caoccao.javet.shell.constants

import com.caoccao.javet.interop.converters.JavetProxyConverter
import com.caoccao.javet.interop.proxy.JavetReflectionObjectFactory
import com.caoccao.javet.shell.enums.RuntimeType

object Constants {
    object Application {
        const val AWAIT_INTERVAL_IN_MILLIS = 100L
        const val NAME = "Javet Shell"
        const val PROMPT_STRING = "Please input the script or press Ctrl+C to exit."
        const val VERSION = "0.1.0"
    }

    object Options {
        val JS_RUNTIME_TYPE_DEFAULT_TYPE = RuntimeType.V8
        const val DEBUG_PORT_DEFAULT_VALUE = 9229U
        const val DEBUG_PORT_DESCRIPTION = "Debug port"
        const val DEBUG_PORT_SHORT_NAME = "p"
        const val JS_RUNTIME_TYPE_DESCRIPTION = "JS runtime type"
        const val JS_RUNTIME_TYPE_SHORT_NAME = "r"
        const val SCRIPT_NAME_DEFAULT_VALUE = "main.js"
        const val SCRIPT_NAME_DESCRIPTION = "Script name"
        const val SCRIPT_NAME_SHORT_NAME = "s"
    }

    object Inspector {
        const val APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=UTF-8"
        const val PATH_ROOT = "/"
        const val PATH_JSON = "/json"
        const val PATH_JSON_VERSION = "/json/version"
        const val PATH_JAVET = "/javet"

        fun getWebSocketUrl(port: UInt) = "ws://127.0.0.1:${port}${PATH_JAVET}"
    }

    object Javet {
        val JAVET_PROXY_CONVERTER = JavetProxyConverter()

        init {
            JAVET_PROXY_CONVERTER.config.setReflectionObjectFactory(JavetReflectionObjectFactory.getInstance())
        }
    }
}