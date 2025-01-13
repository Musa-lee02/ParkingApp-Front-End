plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.com.google.devtools.ksp)
}

android {
    namespace = "com.example.parkingappfront_end"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.parkingappfront_end"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    }
    kotlinOptions {
        jvmTarget = "21" //21
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.accompanist.permissions)
    implementation(libs.glide)
    implementation(libs.jackson.core)
    implementation(libs.jackson.annotations)
    implementation(libs.logging.interceptor)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.client.logging)
    implementation(libs.gson.v2101)
    implementation(libs.coil.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.browser)

    implementation(libs.androidx.compose.material)
    implementation(libs.places)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    implementation(libs.volley)
    implementation(libs.billing.ktx)
    implementation(libs.androidx.core.i18n)
    //implementation(libs.androidx.ui.desktop)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime.ktx)

    implementation(libs.androidx.activity)
    ksp(libs.androidx.room.compiler)
    implementation(kotlin("stdlib-jdk8"))
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.androidx.security.crypto)

    // MPAndroidChart
    //implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
    implementation(libs.mpandroidchart) // Aggiungi
    implementation (libs.philjay.mpandroidchart)


    implementation(libs.jwtdecode)
    implementation(libs.material.v1120)

    // Material 2 per BottomNavigation
    implementation(libs.androidx.material)


    implementation ("androidx.work:work-runtime-ktx:2.8.1")
    implementation ("androidx.core:core-ktx:1.12.0")
    implementation ("androidx.activity:activity-compose:1.7.2")
    implementation ("androidx.appcompat:appcompat:1.7.0-alpha03")
}

kotlin {
    jvmToolchain(21)
}
