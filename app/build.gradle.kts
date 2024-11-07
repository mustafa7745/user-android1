plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization") version "1.8.10"
}

android {
    namespace = "com.yemen_restaurant.greenland"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.yemen_restaurant.greenland"
        minSdk = 23
        targetSdk = 34
        versionCode = 26
        versionName = "1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.4.3"
//    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildToolsVersion = "34.0.0"
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3")
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging-ktx:23.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation ("io.coil-kt:coil-svg:2.4.0")
    implementation ("com.google.android.libraries.places:places:4.0.0")
////    implementation ("com.google.maps.android:android-maps-utils:3.8.2")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:review:2.0.1")
    implementation("com.google.android.play:review-ktx:2.0.1")
//    implementation ("io.github.ahmad-hamwi:tabsync-compose:1.0.1")
//    implementation("com.quadible:smart-tabs:1.0.0")
    implementation("com.google.maps.android:maps-compose:6.2.0")
//    // Optionally, you can include the Compose utils library for Clustering,
//    // Street View metadata checks, etc.
//    implementation ("com.google.maps.android:maps-compose-utils:6.2.1")
//
//    // Optionally, you can include the widgets library for ScaleBar, etc.
//    implementation ("com.google.maps.android:maps-compose-widgets:6.2.1")
    // Maps SDK for Android
    implementation("com.google.android.gms:play-services-maps:19.0.0")

}