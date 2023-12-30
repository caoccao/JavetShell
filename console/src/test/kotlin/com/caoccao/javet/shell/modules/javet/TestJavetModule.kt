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

package com.caoccao.javet.shell.modules.javet

import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interfaces.IJavetAnonymous
import com.caoccao.javet.shell.BaseTestSuite
import com.caoccao.javet.shell.mock.MockDynamicClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class TestJavetModule : BaseTestSuite() {
    @Test
    fun testDynamicObject() {
        v8Runtimes.forEach { v8Runtime ->
            val anonymous = object : IJavetAnonymous {
                @V8Function
                @Throws(Exception::class)
                fun test(dynamicClass: MockDynamicClass) {
                    assertEquals(3, dynamicClass.add(1, 2), "Add should work.")
                    (dynamicClass as AutoCloseable).close()
                }
            }
            v8Runtime.globalObject.set("a", anonymous)
            v8Runtime.getExecutor("a.test({ add: (a, b) => a + b });").executeVoid()
            v8Runtime.globalObject.delete("a")
        }
    }

    @Test
    fun testGC() {
        v8Runtimes.forEach { v8Runtime ->
            val initialCallbackContextCount = v8Runtime.callbackContextCount
            v8Runtime.globalObject.set("test", String::class.java)
            assertEquals(initialCallbackContextCount + 5, v8Runtime.callbackContextCount)
            v8Runtime.globalObject.delete("test")
            assertEquals(initialCallbackContextCount + 5, v8Runtime.callbackContextCount)
            v8Runtime.getExecutor("javet.gc()").executeVoid()
            assertEquals(initialCallbackContextCount, v8Runtime.callbackContextCount)
        }
    }

    @Test
    fun testPackage() {
        v8Runtimes.forEach { v8Runtime ->
            // Test java
            v8Runtime.getExecutor("let java = javet.package.java").executeVoid()
            assertFalse(v8Runtime.getExecutor("java['.valid']").executeBoolean())
            assertEquals("java", v8Runtime.getExecutor("java['.name']").executeString())
            // Test java.util
            v8Runtime.getExecutor("let javaUtil = java.util").executeVoid()
            assertTrue(v8Runtime.getExecutor("javaUtil['.valid']").executeBoolean())
            assertTrue(v8Runtime.getExecutor("javaUtil['.sealed']").executeBoolean())
            assertEquals("java.util", v8Runtime.getExecutor("javaUtil['.name']").executeString())
            // Test java.lang.Object
            assertEquals(Object::class.java, v8Runtime.getExecutor("java.lang.Object").executeObject())
            // Test invalid cases
            assertTrue(
                v8Runtime.getExecutor("javet.package.abc.def").executeObject<Any>() is JavetVirtualPackage
            )
            assertTrue(v8Runtime.getExecutor("java.lang.abcdefg").executeObject<Any>() is JavetVirtualPackage)
            assertEquals(
                "java.io,java.lang,java.math,java.net,java.nio,java.security,java.text,java.time,java.util",
                v8Runtime.getExecutor("java['.getPackages']().map(p => p['.name']).sort().join(',')").executeString()
            )
            // Clean up
            v8Runtime.getExecutor("java = undefined; javaUtil = undefined;").executeVoid()
        }
    }

    @Test
    fun testStringBuilder() {
        v8Runtimes.forEach { v8Runtime ->
            v8Runtime.getExecutor("let java = javet.package.java").executeVoid()
            assertEquals(
                "a1",
                v8Runtime.getExecutor(
                    "let sb = new java.lang.StringBuilder(); sb.append('a').append(1); sb.toString();"
                ).executeString()
            )
            v8Runtime.getExecutor("java = undefined; sb = undefined;").executeVoid()
        }
    }

    @Test
    fun testThread() {
        v8Runtimes.forEach { v8Runtime ->
            v8Runtime.getExecutor(
                """let java = javet.package.java;
                    | let count = 0;
                    | let thread = new java.lang.Thread(() => { count++; });
                    | thread.start();
                    | thread;
                """.trimMargin()
            ).executeObject<Thread>().join()
            assertEquals(
                1,
                v8Runtime.getExecutor("count").executeInteger()
            )
            v8Runtime.getExecutor("java = undefined; thread = undefined;").executeVoid()
        }
    }
}