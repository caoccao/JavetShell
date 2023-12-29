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

package com.caoccao.javet.shell.inspector

import com.caoccao.javet.interfaces.IJavetLogger
import com.caoccao.javet.interop.IV8InspectorListener
import com.caoccao.javet.interop.V8Runtime
import com.caoccao.javet.shell.constants.Constants
import com.caoccao.javet.shell.entities.Options
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketAdapter

class InspectorWebSocketAdapter(
    val logger: IJavetLogger,
    val v8Runtime: V8Runtime,
    val options: Options,
) : WebSocketAdapter(),
    IV8InspectorListener {
    override fun flushProtocolNotifications() {
    }

    override fun onWebSocketClose(statusCode: Int, reason: String?) {
        v8Runtime.v8Inspector.removeListeners(this)
        logger.debug("\nDebug server is closed.\n")
        super.onWebSocketClose(statusCode, reason)
    }

    override fun onWebSocketConnect(session: Session?) {
        super.onWebSocketConnect(session)
        logger.debug("\nDebug server is open at ws://${Constants.Inspector.getWebSocketUrl(options.debugPort)}.\n")
        v8Runtime.v8Inspector.addListeners(this)
    }

    override fun onWebSocketError(cause: Throwable?) {
        cause?.let { t ->
            logger.error("\nError: ${t.message}\n")
        }
    }

    override fun onWebSocketText(message: String?) {
        if (!message.isNullOrBlank()) {
            try {
                v8Runtime.v8Inspector.sendRequest(message);
            } catch (t: Throwable) {
                logger.error("\nError: ${t.message}\n")
            }
        }
    }

    override fun receiveNotification(message: String?) {
        if (!message.isNullOrBlank()) {
            try {
                remote.sendString(message);
            } catch (t: Throwable) {
                logger.error("\nError: ${t.message}\n")
            }
        }
    }

    override fun receiveResponse(message: String?) {
        if (!message.isNullOrBlank()) {
            try {
                remote.sendString(message);
            } catch (t: Throwable) {
                logger.error("\nError: ${t.message}\n")
            }
        }
    }

    override fun runIfWaitingForDebugger(contextGroupId: Int) {
        try {
            v8Runtime.getExecutor("console.log('Welcome to Javet Debugging Environment!');").executeVoid();
        } catch (t: Throwable) {
            logger.error("\nError: ${t.message}\n")
        }
    }

    override fun sendRequest(message: String?) {
    }
}