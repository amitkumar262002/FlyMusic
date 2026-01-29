plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.0.21"
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.flymusicai"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.flymusicai"
        minSdk = 26
        targetSdk = 36
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
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.foundation:foundation")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    
    // DataStore for SharedPreferences replacement
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // Material Icons Extended
    implementation("androidx.compose.material:material-icons-extended:1.6.1")
    
    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // System UI Controller
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")
    
    // ExoPlayer for audio playback
    implementation("androidx.media3:media3-exoplayer:1.2.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.2.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.2.1")

    implementation("androidx.media3:media3-ui:1.2.1")
    implementation("androidx.media3:media3-session:1.2.1")
    
    // Media library for notification controls
    implementation("androidx.media:media:1.7.0")
    
    // Ktor HTTP Client for music streaming APIs
    implementation("io.ktor:ktor-client-android:2.3.7")
    implementation("io.ktor:ktor-client-okhttp:2.3.7")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
    implementation("io.ktor:ktor-client-logging:2.3.7")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    
    implementation(libs.newpipe.extractor)
    implementation(libs.nanojson)
    implementation(libs.jsoup)
    implementation(libs.okhttp)
    
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.8.0"))

    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-analytics")
    
    // Coroutines Play Services for Firebase .await()
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
}