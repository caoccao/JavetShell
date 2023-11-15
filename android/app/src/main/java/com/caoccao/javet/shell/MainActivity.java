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

package com.caoccao.javet.shell;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.loader.JavetLibLoader;
import com.caoccao.javet.utils.JavetOSUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.textView);
        StringBuilder sb = new StringBuilder();
        sb.append("Javet is Java + V8 (JAVa + V + EighT). It is an awesome way of embedding Node.js and V8 in Java.\n\n");
        sb.append("Javet v").append(JavetLibLoader.LIB_VERSION).append(" supports Android (arm, arm64, x86 and x86_64) ABI >= 24.\n\n");
        try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
            sb.append(v8Runtime.getExecutor("'Hello Javet'").executeString()).append("\n");
            sb.append("OS Name = ").append(JavetOSUtils.OS_NAME).append("\n");
            sb.append("OS Arch = ").append(JavetOSUtils.OS_ARCH).append("\n");
            sb.append("V8 = ").append(v8Runtime.getVersion()).append("\n");
            sb.append("Now = ").append(v8Runtime.getExecutor("new Date()").executeZonedDateTime()).append("\n");
        } catch (JavetException e) {
            sb.append(e.getMessage()).append("\n");
            sb.append("Error Code = ").append(e.getError().getCode()).append("\n");
            if (e.getCause() != null) {
                sb.append(e.getCause().getMessage()).append("\n");
            }
        }
        textView.setText(sb.toString());
    }
}