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

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.caoccao.javet.interop.V8Host
import com.caoccao.javet.interop.V8Runtime
import com.caoccao.javet.shell.ui.theme.JavetShellTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class TestMainActivity {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun testAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.caoccao.javet.shell", appContext.packageName)
    }

    @Test
    fun testButtonExecute() {
        V8Host.getV8Instance().createV8Runtime<V8Runtime>().use { v8Runtime ->
            v8Runtime.logger = ConsoleLogger()
            composeRule.setContent {
                JavetShellTheme {
                    HomeScreen(v8Runtime = v8Runtime, stringBuilder = StringBuilder())
                }
            }
            val nodeBasicTextField = composeRule.onNodeWithTag("basicTextFieldCodeString")
            val nodeElevatedButtonExecute = composeRule.onNodeWithTag("elevatedButtonExecute")
            val nodeTextResult = composeRule.onNodeWithTag("textResult")
            nodeBasicTextField.performTextInput("1 + 1")
            nodeElevatedButtonExecute.performClick()
            nodeTextResult.assertTextEquals("\n> 1 + 1\n2")
        }
    }

    @Test
    fun testBasicTextFieldExecute() {
        V8Host.getV8Instance().createV8Runtime<V8Runtime>().use { v8Runtime ->
            v8Runtime.logger = ConsoleLogger()
            composeRule.setContent {
                JavetShellTheme {
                    HomeScreen(v8Runtime = v8Runtime, stringBuilder = StringBuilder())
                }
            }
            val nodeBasicTextField = composeRule.onNodeWithTag("basicTextFieldCodeString")
            val nodeTextResult = composeRule.onNodeWithTag("textResult")
            nodeBasicTextField.performTextInput("1 + 1\n")
            nodeBasicTextField.performTextInput("\n")
            nodeTextResult.assertTextEquals("\n> 1 + 1\n2")
        }
    }

    @Test
    fun testIconButtonRefresh() {
        V8Host.getV8Instance().createV8Runtime<V8Runtime>().use { v8Runtime ->
            v8Runtime.logger = ConsoleLogger()
            composeRule.setContent {
                JavetShellTheme {
                    HomeScreen(v8Runtime = v8Runtime, stringBuilder = StringBuilder())
                }
            }
            val nodeIconButtonRefresh = composeRule.onNodeWithTag("iconButtonRefresh")
            val nodeElevatedButtonExecute = composeRule.onNodeWithTag("elevatedButtonExecute")
            val nodeBasicTextField = composeRule.onNodeWithTag("basicTextFieldCodeString")
            val nodeTextResult = composeRule.onNodeWithTag("textResult")
            nodeBasicTextField.performTextInput("const a = 1\n")
            nodeElevatedButtonExecute.performClick()
            nodeTextResult.assertTextEquals("\n> const a = 1\nundefined")
            nodeBasicTextField.performTextInput("const a = 1\n")
            nodeElevatedButtonExecute.performClick()
            nodeTextResult.assertTextEquals(
                "\n> const a = 1\nundefined" +
                        "\n> const a = 1\nSyntaxError: Identifier 'a' has already been declared"
            )
            nodeIconButtonRefresh.performClick()
            nodeBasicTextField.performTextInput("const a = 1\n")
            nodeElevatedButtonExecute.performClick()
            nodeTextResult.assertTextEquals("V8 context is refreshed.\n> const a = 1\nundefined")
        }
    }
}