plugins {
    id("com.android.application")
}

android {
    compileSdk = 34

    defaultConfig {
        applicationId = "com.caoccao.javet.shell"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "0.1.0"
        setProperty("archivesBaseName", "javet-shell-${versionName}")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    namespace = "com.caoccao.javet.shell"
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.caoccao.javet:javet-android:3.0.2")
//    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))
    testImplementation("junit:junit:5.10.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.21")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}