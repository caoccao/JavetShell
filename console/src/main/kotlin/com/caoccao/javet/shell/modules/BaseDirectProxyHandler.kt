/*
 * Copyright (c) 2023-2023. caoccao.com Sam Cao
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

package com.caoccao.javet.shell.modules

import com.caoccao.javet.interfaces.IJavetBiFunction
import com.caoccao.javet.interfaces.IJavetUniFunction
import com.caoccao.javet.interop.V8Runtime
import com.caoccao.javet.interop.proxy.IJavetDirectProxyHandler
import com.caoccao.javet.javenode.modules.BaseJNCallable
import com.caoccao.javet.shell.constants.Constants
import com.caoccao.javet.values.V8Value
import com.caoccao.javet.values.reference.V8ValueSymbol

abstract class BaseDirectProxyHandler(private val v8Runtime: V8Runtime) :
    BaseJNCallable(), IJavetDirectProxyHandler<Exception> {
    protected var stringGetterMap: Map<String, IJavetUniFunction<String, out V8Value, Exception>>? = null
    protected var stringSetterMap: Map<String, IJavetBiFunction<String, V8Value, Boolean, Exception>>? = null
    protected var symbolGetterMap: Map<String, IJavetUniFunction<V8ValueSymbol, out V8Value, Exception>>? = null
    protected var symbolSetterMap: Map<String, IJavetBiFunction<V8ValueSymbol, V8Value, Boolean, Exception>>? = null

    override fun getV8Runtime(): V8Runtime {
        return v8Runtime
    }

    open fun toV8Value(): V8Value {
        return Constants.Javet.JAVET_PROXY_CONVERTER.toV8Value(v8Runtime, this)
    }
}