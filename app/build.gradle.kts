plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.swag.vyom"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.swag.vyom"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.navigation.compose)

    testImplementation(libs.kotlinx.coroutines.test)

    // Retrofit for network calls
    implementation(libs.retrofit)
    // Gson converter for JSON parsing
    implementation( libs.converter.gson)
    // Kotlin Coroutines for asynchronous programming
    implementation(libs.kotlinx.coroutines.android)
    // OkHttp for network operations
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)



    // Moshi (for JSON serialization/deserialization)
    implementation(libs.moshi.kotlin)
    implementation(libs.moshi)
    implementation(libs.moshi.adapters)

    // Retrofit Moshi Converter (to use Moshi with Retrofit)
    implementation(libs.converter.moshi)



}