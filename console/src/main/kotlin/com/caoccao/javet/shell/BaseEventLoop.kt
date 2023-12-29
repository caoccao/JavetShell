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

import com.caoccao.javet.enums.V8AwaitMode
import com.caoccao.javet.interfaces.IJavetClosable
import com.caoccao.javet.interfaces.IJavetLogger
import com.caoccao.javet.interop.V8Runtime
import com.caoccao.javet.javenode.JNEventLoop
import com.caoccao.javet.shell.constants.Constants
import com.caoccao.javet.shell.entities.Options
import com.caoccao.javet.shell.inspector.InspectorHttpServlet
import com.caoccao.javet.shell.inspector.InspectorWebSocketCreator
import com.caoccao.javet.shell.utils.JavetShellSilentLogger
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.websocket.server.NativeWebSocketConfiguration
import org.eclipse.jetty.websocket.server.NativeWebSocketServletContainerInitializer
import org.eclipse.jetty.websocket.server.WebSocketUpgradeFilter
import java.util.concurrent.TimeUnit
import javax.servlet.ServletContext

abstract class BaseEventLoop(
    protected val logger: IJavetLogger,
    protected val v8Runtime: V8Runtime,
    protected val options: Options,
) : IJavetClosable, Runnable {
    private var daemonThread: Thread? = null
    private var inspectorServer: Server? = null
    protected var jnEventLoop: JNEventLoop? = null

    @Volatile
    var gcScheduled = false

    @Volatile
    var running = false

    init {
        start()
    }

    override fun close() {
        stop()
    }

    override fun isClosed(): Boolean = !running

    override fun run() {
        while (running) {
            v8Runtime.await(V8AwaitMode.RunOnce)
            if (gcScheduled) {
                System.gc()
                System.runFinalization()
                v8Runtime.lowMemoryNotification()
                gcScheduled = false
            } else {
                try {
                    TimeUnit.MILLISECONDS.sleep(Constants.Application.AWAIT_INTERVAL_IN_MILLIS)
                } catch (_: Throwable) {
                }
            }
        }
        System.gc()
        System.runFinalization()
        v8Runtime.lowMemoryNotification()
    }

    protected open fun start() {
        jnEventLoop = JNEventLoop(v8Runtime)
        running = true
        gcScheduled = false
        daemonThread = Thread(this)
        daemonThread?.start()
        try {
            inspectorServer = Server(options.debugPort.toInt())
            val inspectorServletContextHandler = ServletContextHandler(
                ServletContextHandler.SESSIONS or ServletContextHandler.NO_SECURITY
            )
            inspectorServletContextHandler.contextPath = Constants.Inspector.PATH_ROOT
            inspectorServer!!.setHandler(inspectorServletContextHandler)
            val servletHolder = ServletHolder(InspectorHttpServlet(options))
            inspectorServletContextHandler.addServlet(servletHolder, Constants.Inspector.PATH_ROOT)
            NativeWebSocketServletContainerInitializer.configure(inspectorServletContextHandler)
            { _: ServletContext, nativeWebSocketConfiguration: NativeWebSocketConfiguration ->
                nativeWebSocketConfiguration.policy.maxTextMessageBufferSize = 0xFFFFFF
                nativeWebSocketConfiguration.addMapping(
                    Constants.Inspector.PATH_JAVET,
                    InspectorWebSocketCreator(logger, v8Runtime, options),
                )
            }
            WebSocketUpgradeFilter.configure(inspectorServletContextHandler)
            inspectorServer!!.start()
        } catch (t: Throwable) {
            logger.error("\nError: ${t.message}\n")
        }
    }

    protected open fun stop() {
        running = false
        gcScheduled = false
        if (inspectorServer != null && (inspectorServer!!.isStarted || inspectorServer!!.isStarting)) {
            try {
                inspectorServer!!.stop()
            } catch (t: Throwable) {
                logger.error("\nError: ${t.message}\n")
            }
        }
        daemonThread?.join()
        daemonThread = null
        inspectorServer = null
        jnEventLoop = null
    }
}