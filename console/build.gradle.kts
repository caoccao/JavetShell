/*
 * Copyright (c) 2021-2025. caoccao.com Sam Cao
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

import org.gradle.internal.os.OperatingSystem

object Config {
    const val GROUP_ID = "com.caoccao.javet"
    const val NAME = "Javet Shell"
    const val VERSION = Versions.JAVET_SHELL
    const val URL = "https://github.com/caoccao/JavetShell"

    object Projects {
        // https://mvnrepository.com/artifact/net.bytebuddy/byte-buddy
        const val BYTE_BUDDY = "net.bytebuddy:byte-buddy:${Versions.BYTE_BUDDY}"

        const val JAVET = "com.caoccao.javet:javet:${Versions.JAVET}"
        const val JAVET_NODE = "com.caoccao.javet:javet-node"
        const val JAVET_V8 = "com.caoccao.javet:javet-v8"

        // https://mvnrepository.com/artifact/com.caoccao.javet.buddy/javet-buddy
        const val JAVET_BUDDY = "com.caoccao.javet.buddy:javet-buddy:${Versions.JAVET_BUDDY}"

        const val JAVET_SWC4J = "com.caoccao.javet:swc4j:${Versions.JAVET_SWC4J}"
        const val JAVET_SWC4J_BIN = "com.caoccao.javet:swc4j"

        const val JAVENODE = "com.caoccao.javet:javenode:${Versions.JAVENODE}"

        // https://mvnrepository.com/artifact/org.eclipse.jetty.websocket/javax-websocket-server-impl
        const val JETTY_JAVAX_WEBSOCKET_SERVER_IMPL =
            "org.eclipse.jetty.websocket:javax-websocket-server-impl:${Versions.JETTY_WEBSOCKET}"

        // https://mvnrepository.com/artifact/org.eclipse.jetty.websocket/websocket-server
        const val JETTY_WEBSOCKET_SERVER = "org.eclipse.jetty.websocket:websocket-server:${Versions.JETTY_WEBSOCKET}"

        // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
        const val JUNIT_JUPITER_API = "org.junit.jupiter:junit-jupiter-api:${Versions.JUNIT}"

        // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-engine
        const val JUNIT_JUPITER_ENGINE = "org.junit.jupiter:junit-jupiter-engine:${Versions.JUNIT}"

        // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-stdlib-jdk8
        const val KOTLIN_STDLIB_JDK8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.KOTLIN_STDLIB_JDK8}"

        // https://github.com/Kotlin/kotlinx-cli
        // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-cli-jvm
        const val KOTLINX_CLI = "org.jetbrains.kotlinx:kotlinx-cli:${Versions.KOTLINX_CLI}"

        // https://mvnrepository.com/artifact/io.vertx/vertx-core
        const val VERTX = "io.vertx:vertx-core:${Versions.VERTX}"
    }

    object Versions {
        const val BYTE_BUDDY = "1.15.5"
        const val JAVET = "4.1.4"
        const val JAVET_BUDDY = "0.4.0"
        const val JAVET_SHELL = "0.1.0"
        const val JAVET_SWC4J = "1.6.0"
        const val JAVENODE = "0.8.0"
        const val JETTY_WEBSOCKET = "9.4.53.v20231009"
        const val JUNIT = "5.12.2"
        const val KOTLIN_STDLIB_JDK8 = "1.9.21"
        const val KOTLINX_CLI = "0.3.6"
        const val VERTX = "4.5.0"
    }
}

plugins {
    application
    kotlin("jvm") version "1.9.21"
    id("org.graalvm.buildtools.native") version "0.10.1"
}

repositories {
    mavenCentral()
}

group = Config.GROUP_ID
version = Config.VERSION


dependencies {
    val os = OperatingSystem.current()
    val arch = System.getProperty("os.arch")
    val osType = if (os.isWindows) "windows" else
        if (os.isMacOsX) "macos" else
            if (os.isLinux) "linux" else ""
    val archType = if (arch == "aarch64" || arch == "arm64") "arm64" else "x86_64"

    implementation(Config.Projects.BYTE_BUDDY)
    implementation(Config.Projects.JAVET)
    implementation("${Config.Projects.JAVET_NODE}-$osType-$archType:${Config.Versions.JAVET}")
    implementation("${Config.Projects.JAVET_V8}-$osType-$archType:${Config.Versions.JAVET}")
    implementation(Config.Projects.JAVET_BUDDY)
    implementation(Config.Projects.JAVET_SWC4J)
    implementation("${Config.Projects.JAVET_SWC4J_BIN}-$osType-$archType:${Config.Versions.JAVET_SWC4J}")
    implementation(Config.Projects.JAVENODE)
    implementation(Config.Projects.JETTY_JAVAX_WEBSOCKET_SERVER_IMPL)
    implementation(Config.Projects.JETTY_WEBSOCKET_SERVER)
    implementation(Config.Projects.KOTLIN_STDLIB_JDK8)
    implementation(Config.Projects.KOTLINX_CLI)
    implementation(Config.Projects.VERTX)

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("${project.group}.shell.MainKt")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "${project.group}.shell.MainKt"
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

graalvmNative {
    binaries {
        named("main") {
            fallback.set(true)
        }
    }
}
