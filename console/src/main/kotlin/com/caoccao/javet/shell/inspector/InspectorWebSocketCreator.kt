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

package com.caoccao.javet.shell.inspector

import com.caoccao.javet.interfaces.IJavetLogger
import com.caoccao.javet.interop.V8Runtime
import com.caoccao.javet.shell.entities.Options
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse
import org.eclipse.jetty.websocket.servlet.WebSocketCreator

class InspectorWebSocketCreator(
    val logger: IJavetLogger,
    val v8Runtime: V8Runtime,
    val options: Options,
) : WebSocketCreator {
    override fun createWebSocket(
        request: ServletUpgradeRequest,
        response: ServletUpgradeResponse,
    ) = InspectorWebSocketAdapter(logger, v8Runtime, options)
}