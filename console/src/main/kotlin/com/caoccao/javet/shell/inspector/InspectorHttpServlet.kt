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

import com.caoccao.javet.interop.loader.JavetLibLoader
import com.caoccao.javet.shell.constants.Constants
import com.caoccao.javet.shell.entities.Options
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class InspectorHttpServlet(val options: Options) : HttpServlet() {
    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val requestURI = request.requestURI
        response.contentType = Constants.Inspector.APPLICATION_JSON_CHARSET_UTF_8
        response.status = HttpServletResponse.SC_OK
        when (requestURI) {
            Constants.Inspector.PATH_JSON, Constants.Inspector.PATH_JSON_LIST -> {
                val webSocketUrl = Constants.Inspector.getWebSocketUrl(options.debugPort)
                response.writer.println(
                    """[ {
                       |  "description": "javet",
                       |  "devtoolsFrontendUrl": "devtools://devtools/bundled/js_app.html?experiments=true&v8only=true&ws=$webSocketUrl",
                       |  "devtoolsFrontendUrlCompat": "devtools://devtools/bundled/inspector.html?experiments=true&v8only=true&ws=$webSocketUrl",
                       |  "id": "javet",
                       |  "title": "javet",
                       |  "type": "node",
                       |  "url": "file://",
                       |  "webSocketDebuggerUrl": "ws://$webSocketUrl"
                       |} ]
                       |""".trimMargin("|")
                )
            }

            Constants.Inspector.PATH_JSON_VERSION -> {
                response.writer.println(
                    """{
                      |  "Browser": "Javet/v${JavetLibLoader.LIB_VERSION}",
                      |  "Protocol-Version": "1.3"
                      |} """.trimMargin("|")
                )
            }

            else -> {
                response.writer.println("{}")
            }
        }
    }
}