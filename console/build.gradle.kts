/*
 * Copyright (c) 2021-2023. caoccao.com Sam Cao
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
        // https://mvnrepository.com/artifact/org.antlr/antlr4
        const val ANTLR4 = "org.antlr:antlr4:${Versions.ANTLR4}"

        const val JAVENODE = "com.caoccao.javet:javenode:${Versions.JAVENODE}"
        const val JAVET = "com.caoccao.javet:javet:${Versions.JAVET}"
        const val JAVET_LINUX_ARM64 = "com.caoccao.javet:javet-linux-arm64:${Versions.JAVET}"
        const val JAVET_MACOS = "com.caoccao.javet:javet-macos:${Versions.JAVET}"

        // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
        const val JUNIT_JUPITER_API = "org.junit.jupiter:junit-jupiter-api:${Versions.JUNIT}"

        // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-engine
        const val JUNIT_JUPITER_ENGINE = "org.junit.jupiter:junit-jupiter-engine:${Versions.JUNIT}"

        const val KOTLIN_STDLIB_JDK8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.KOTLIN_STDLIB_JDK8}"

        // https://github.com/Kotlin/kotlinx-cli
        // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-cli-jvm
        const val KOTLINX_CLI = "org.jetbrains.kotlinx:kotlinx-cli:${Versions.KOTLINX_CLI}"

        // https://mvnrepository.com/artifact/io.vertx/vertx-core
        const val VERTX = "io.vertx:vertx-core:${Versions.VERTX}"
    }

    object Versions {
        const val ANTLR4 = "4.13.1"
        const val JAVENODE = "0.3.0"
        const val JAVET = "3.0.2"
        const val JAVET_SANITIZER = "0.3.0"
        const val JAVET_SHELL = "0.1.0"
        const val JUNIT = "5.10.1"
        const val KOTLIN_STDLIB_JDK8 = "1.8.10"
        const val KOTLINX_CLI = "0.3.6"
        const val VERTX = "4.4.6"
    }
}

plugins {
    application
    kotlin("jvm") version "1.9.20"
}

repositories {
    mavenCentral()
}

group = Config.GROUP_ID
version = Config.VERSION

dependencies {
    implementation(Config.Projects.JAVENODE)
    val os = OperatingSystem.current()
    val cpuArch = System.getProperty("os.arch")
    if (os.isMacOsX) {
        implementation(Config.Projects.JAVET_MACOS)
    } else if (os.isLinux && (cpuArch == "aarch64" || cpuArch == "arm64")) {
        implementation(Config.Projects.JAVET_LINUX_ARM64)
    } else {
        implementation(Config.Projects.JAVET)
    }
    implementation(Config.Projects.KOTLIN_STDLIB_JDK8)
    implementation(Config.Projects.KOTLINX_CLI)
    implementation(Config.Projects.VERTX)

    testImplementation(Config.Projects.JUNIT_JUPITER_API)
    testRuntimeOnly(Config.Projects.JUNIT_JUPITER_ENGINE)
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

kotlin {
    jvmToolchain(17)
}