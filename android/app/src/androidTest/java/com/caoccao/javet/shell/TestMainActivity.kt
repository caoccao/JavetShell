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
    fun testButtonExecute() {
        V8Host.getV8Instance().createV8Runtime<V8Runtime>().use { v8Runtime ->
            composeRule.setContent {
                JavetShellTheme {
                    HomeScreen(v8Runtime = v8Runtime, stringBuilder = StringBuilder())
                }
            }
            composeRule.onNodeWithTag("basicTextFieldCodeString").performTextInput("1 + 1")
            composeRule.onNodeWithTag("elevatedButtonExecute").performClick()
            composeRule.onNodeWithTag("textResult").assertTextEquals("\n> 1 + 1\n2")
        }
    }

    @Test
    fun testBasicTextFieldExecute() {
        V8Host.getV8Instance().createV8Runtime<V8Runtime>().use { v8Runtime ->
            composeRule.setContent {
                JavetShellTheme {
                    HomeScreen(v8Runtime = v8Runtime, stringBuilder = StringBuilder())
                }
            }
            val nodeBasicTextField = composeRule.onNodeWithTag("basicTextFieldCodeString")
            nodeBasicTextField.performTextInput("1 + 1\n")
            nodeBasicTextField.performTextInput("\n")
            composeRule.onNodeWithTag("textResult").assertTextEquals("\n> 1 + 1\n2")
        }
    }

    @Test
    fun testAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.caoccao.javet.shell", appContext.packageName)
    }
}