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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.caoccao.javet.shell

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.caoccao.javet.interop.V8Host
import com.caoccao.javet.interop.V8Runtime
import com.caoccao.javet.interop.loader.JavetLibLoader
import com.caoccao.javet.shell.ui.theme.JavetShellTheme
import com.caoccao.javet.utils.JavetOSUtils
import com.caoccao.javet.values.V8Value

class MainActivity : ComponentActivity() {
    private var v8Runtime: V8Runtime? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        v8Runtime = V8Host.getV8Instance().createV8Runtime()
        val now = v8Runtime?.getExecutor("new Date()")?.executeZonedDateTime()
        val stringBuilder = StringBuilder().append(
            """Javet is Java + V8 (JAVa + V + EighT). It is an awesome way of embedding Node.js and V8 in Java.
                            | 
                            | Javet v${JavetLibLoader.LIB_VERSION} supports Android (arm, arm64, x86 and x86_64) ABI >= 24.
                            | 
                            | OS Name = ${JavetOSUtils.OS_NAME}
                            | OS Arch = ${JavetOSUtils.OS_ARCH}
                            | V8 = ${v8Runtime?.version}
                            | Now = $now
                            | 
                        """.trimMargin("| ")
        )
        setContent {
            JavetShellTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(v8Runtime = v8Runtime, stringBuilder = stringBuilder)
                }
            }
        }
    }

    override fun onDestroy() {
        v8Runtime?.close()
        v8Runtime = null
        super.onDestroy()
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    v8Runtime: V8Runtime? = null,
    stringBuilder: StringBuilder = StringBuilder()
) {
    var resultString by remember { mutableStateOf(stringBuilder.toString()) }
    var codeString by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                actions = {
                    IconButton(
                        onClick = {
                            v8Runtime?.resetContext()
                            stringBuilder.clear().append("V8 context is refreshed.")
                            codeString = ""
                            resultString = ""
                        },
                        modifier = modifier.testTag("iconButtonRefresh")
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = stringResource(id = R.string.icon_refresh)
                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        val padding = 5.dp
        val scrollState = rememberScrollState(0)
        val executeCode = {
            val trimmedCodeString = codeString.trim()
            if (trimmedCodeString.isNotBlank()) {
                try {
                    v8Runtime!!
                        .getExecutor(codeString)
                        .execute<V8Value>()
                        .use { v8Value ->
                            stringBuilder.append("\n> $trimmedCodeString\n$v8Value")
                        }
                    codeString = ""
                } catch (t: Throwable) {
                    stringBuilder.append("\n> $trimmedCodeString\n${t.message}")
                } finally {
                    resultString = stringBuilder.toString()
                }
            }
        }
        LaunchedEffect(resultString) {
            scrollState.scrollTo(scrollState.maxValue)
        }
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SelectionContainer(
                modifier = modifier
                    .fillMaxWidth()
                    .weight(0.7F)
            ) {
                Text(
                    text = resultString,
                    modifier = modifier
                        .verticalScroll(scrollState)
                        .padding(padding)
                        .fillMaxSize()
                        .testTag("textResult")
                )
            }
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .weight(0.3F)
            ) {
                ElevatedButton(
                    onClick = executeCode,
                    shape = RoundedCornerShape(10),
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(start = padding, end = padding)
                        .testTag("elevatedButtonExecute")
                ) {
                    Text(text = stringResource(id = R.string.button_execute))
                }
                BasicTextField(
                    value = codeString,
                    onValueChange = { newCodeString ->
                        val executionRequired = codeString.isNotEmpty()
                                && newCodeString.startsWith(codeString)
                                && newCodeString.substring(codeString.length - 1) == "\n\n"
                        codeString = newCodeString
                        if (executionRequired) {
                            executeCode()
                        }
                    },
                    modifier = modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(color = Color.LightGray)
                        .testTag("basicTextFieldCodeString")
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun JavetShellPreview() {
    JavetShellTheme {
        HomeScreen()
    }
}