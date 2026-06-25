plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.xkh.checkin"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.xkh.checkin"
        minSdk = 26
        targetSdk = 35
        versionCode = 3
        versionName = "1.2.0"
    }

    // Keep Java and Kotlin bytecode targets consistent.
    // This prevents: compileDebugJavaWithJavac (1.8) vs compileDebugKotlin (21).
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
