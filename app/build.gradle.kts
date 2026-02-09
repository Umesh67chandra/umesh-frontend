plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// DEBUG: log SDK env and local.properties to help diagnose SDK path issues
println("[DEBUG] ANDROID_SDK_ROOT=" + System.getenv("ANDROID_SDK_ROOT"))
println("[DEBUG] ANDROID_HOME=" + System.getenv("ANDROID_HOME"))
try {
    val lp = rootProject.file("local.properties")
    if (lp.exists()) println("[DEBUG] local.properties: " + lp.readText())
} catch (e: Exception) {
    println("[DEBUG] failed reading local.properties: " + e.message)
}

android {
    namespace = "com.example.focusguardian"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.focusguardian"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "SUPABASE_URL", "\"https://jdatkdmztxzpkhqcmbnt.supabase.co/\"")
        buildConfigField("String", "SUPABASE_KEY", "\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImpkYXRrZG16dHh6cGtocWNtYm50Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzAyNjU1MzIsImV4cCI6MjA4NTg0MTUzMn0.VtarcCXnWl7F7aYrBX0qW08RNPQNueef13CPWLcp1Q0\"")

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
        buildConfig = true
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
    implementation("androidx.compose.material:material-icons-extended")

    // ✅ ADDED THIS LINE
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

}
