import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")

    // secrets
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")

if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.example.cropcart"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.cropcart"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val apitubeApiKey = localProperties.getProperty("APITUBE_API") ?: ""
        val mediaStackApiKey = localProperties.getProperty("MEDIASTACK_API") ?: ""
        val geminiApiKey = localProperties.getProperty("MEDIASTACK_API") ?: ""
        buildConfigField("String", "APITUBE_API", "\"$apitubeApiKey\"")
        buildConfigField("String", "MEDIASTACK_API", "\"$mediaStackApiKey\"")
        buildConfigField("String", "GEMINI_API", "\"$geminiApiKey\"")
    }

    buildFeatures {
        buildConfig = true
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation(libs.circleimageview)
    implementation(libs.glide)
    implementation(libs.generativeai)

    implementation("io.noties.markwon:core:4.6.2")

    // networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
}