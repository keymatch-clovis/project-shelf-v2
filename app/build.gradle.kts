plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    /// KSP Plugin
    alias(libs.plugins.ksp)

    /// Compose Plugin
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.project_shelf"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.project_shelf"
        minSdk = 25
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    /// KSP
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    /// Compose
    implementation(platform(libs.androidx.compose))
    androidTestImplementation(platform(libs.androidx.compose))
    // Activity
    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.compose)
    // Material Design 3
    implementation(libs.androidx.compose.material3)
    // Android Studio Preview support
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.unit)
    implementation(libs.androidx.compose.material.icons)

    /// Splashscreen
    implementation(libs.androidx.core.splashscreen)

    /// Test Coroutines
    testImplementation(libs.kotlinx.coroutines.test)
}