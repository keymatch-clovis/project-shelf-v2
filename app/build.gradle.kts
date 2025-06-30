plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    /// Serialization
    alias(libs.plugins.kotlin.serialization)

    /// KSP Plugin
    alias(libs.plugins.ksp)

    /// Compose Plugin
    alias(libs.plugins.compose.compiler)

    /// Hilt
    alias(libs.plugins.hilt)
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
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    /// Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)

    /// Data Store
    implementation(libs.androidx.datastore)

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

    /// Hilt
    implementation(libs.androidx.hilt)
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)

    /// Faker
    testImplementation(libs.faker)

    /// Paging
    implementation(libs.androidx.paging)
    implementation(libs.androidx.paging.compose)
}