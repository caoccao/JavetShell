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

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

object Config {
    object Projects {
        // https://mvnrepository.com/artifact/net.bytebuddy/byte-buddy
        const val BYTE_BUDDY = "net.bytebuddy:byte-buddy:${Versions.BYTE_BUDDY}"

        const val JAVENODE = "com.caoccao.javet:javenode:${Versions.JAVENODE}"
        const val JAVET__GROUP = "com.caoccao.javet"
        const val JAVET__MODULE = "javet"
        const val JAVET_ANDROID = "com.caoccao.javet:javet-android-v8:${Versions.JAVET}"

        // https://mvnrepository.com/artifact/io.vertx/vertx-core
        const val VERTX = "io.vertx:vertx-core:${Versions.VERTX}"
    }

    object Versions {
        const val BYTE_BUDDY = "1.14.10"
        const val JAVENODE = "0.6.0"
        const val JAVET = "3.1.0"
        const val JETTY_WEBSOCKET = "9.4.53.v20231009"
        const val VERTX = "4.5.0"
    }
}

android {
    namespace = "com.caoccao.javet.shell"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.caoccao.javet.shell"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0"
        setProperty("archivesBaseName", "javet-shell-v8-${versionName}")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/io.netty.versions.properties"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.02.02"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation(Config.Projects.BYTE_BUDDY)
    implementation(Config.Projects.JAVENODE) {
        exclude(group = Config.Projects.JAVET__GROUP, module = Config.Projects.JAVET__MODULE)
    }
    implementation(Config.Projects.JAVET_ANDROID)
    implementation(Config.Projects.VERTX)
    // implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.02"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}