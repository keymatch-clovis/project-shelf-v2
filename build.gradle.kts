// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    /// Serialization
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.parcelize) apply false

    // KSP Plugin
    alias(libs.plugins.ksp) apply false

    /// Compose
    alias(libs.plugins.compose.compiler) apply false

    /// Hilt
    alias(libs.plugins.hilt) apply false

    /// Object Box
    alias(libs.plugins.objectbox) apply false
}