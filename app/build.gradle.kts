import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.hilt)
    alias(libs.plugins.kotlin.serialization)
    kotlin("kapt")
}

android {
    compileSdk = 34
    buildToolsVersion =  "33.0.2"

    defaultConfig {
        applicationId = "pawel.hn.coinmarketapp"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile(
                    "proguard-android-optimize.txt"),
                    "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    namespace = "pawel.hn.coinmarketapp"

}

dependencies {
    implementation(libs.androidx.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    implementation(libs.androidx.constraintlayout)

    //Navigation Component
    implementation(libs.androidx.navigation)
    implementation(libs.androidx.navigation.ui)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter)

    // Room
    implementation(libs.androidx.room)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Dagger Hilt
    implementation(libs.google.hilt.android)
    kapt(libs.google.hilt.android.compiler)

    // Unit Testing
    testImplementation(libs.bundles.testing)
    testImplementation(libs.androidx.lifecycle.runtime.testing)
    androidTestImplementation(libs.bundles.testing)
    androidTestImplementation(libs.androidx.lifecycle.runtime.testing)

    // Glide
    implementation(libs.glie)
    kapt (libs.glie.compiler)

    //Settings
    implementation (libs.androidx.preference)

    //Rss Parser
    implementation(libs.rss)

    // Http logging
    implementation(libs.httpLog)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation (libs.androidx.compose.material3)
    implementation (libs.androidx.compose.material)
    implementation (libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.google.hilt.navigation.compose)

    implementation(libs.bundles.ui.core)

    // Landscapist
    implementation (libs.landscapist)

    implementation (libs.retrofit.result.adapter)


}