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

package com.caoccao.javet.shell.modules.javet

import com.caoccao.javet.shell.BaseTestSuite
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestJavetModule : BaseTestSuite() {
    @Test
    fun testPackage() {
        v8Runtimes.forEach { v8Runtime ->
            // Test java
            v8Runtime.getExecutor("let java = javet.getPackage('java')").executeVoid()
            assertFalse(v8Runtime.getExecutor("java['.valid']").executeBoolean())
            assertEquals("java", v8Runtime.getExecutor("java['.name']").executeString())
            // Test java.util
            v8Runtime.getExecutor("let javaUtil = java.util").executeVoid()
            assertTrue(v8Runtime.getExecutor("javaUtil['.valid']").executeBoolean())
            assertTrue(v8Runtime.getExecutor("javaUtil['.sealed']").executeBoolean())
            assertEquals("java.util", v8Runtime.getExecutor("javaUtil['.name']").executeString())
            // Test java.lang.Object
            assertEquals(Object::class.java, v8Runtime.getExecutor("java.lang.Object").executeObject());
            // Test java.lang.StringBuilder
            assertEquals(
                "a1",
                v8Runtime.getExecutor(
                    "let sb = new java.lang.StringBuilder(); sb.append('a').append(1); sb.toString();"
                ).executeString()
            )
            // Test invalid cases
            assertTrue(
                v8Runtime.getExecutor("javet.getPackage('abc.def')").executeObject<Any>() is JavetVirtualPackage
            );
            assertTrue(v8Runtime.getExecutor("java.lang.abcdefg").executeObject<Any>() is JavetVirtualPackage);
            assertEquals(
                "java.io,java.lang,java.math,java.net,java.nio,java.security,java.text,java.time,java.util",
                v8Runtime.getExecutor("java['.getPackages']().map(p => p['.name']).sort().join(',')").executeString()
            )
            // Clean up
            v8Runtime.getExecutor("java = undefined").executeVoid()
            v8Runtime.getExecutor("javaUtil = undefined").executeVoid()
            v8Runtime.getExecutor("sb = undefined").executeVoid()
        }
    }
}