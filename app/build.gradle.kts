plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.com.google.devtools.ksp)
}

android {
    namespace = "com.example.ecommercefront_end"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ecommercefront_end"
        minSdk = 30
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
        jvmTarget = "21"
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
    implementation(libs.jackson.core)
    implementation (libs.jackson.annotations)
    implementation ("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("io.ktor:ktor-client-core:2.3.4")
    implementation("io.ktor:ktor-client-cio:2.3.4")
    implementation("io.ktor:ktor-client-serialization:2.3.4")
    implementation("io.ktor:ktor-client-logging:2.3.4")
    implementation("com.google.code.gson:gson:2.8.2")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.browser)


    implementation(libs.androidx.compose.material)
    implementation(libs.places)
    implementation (libs.androidx.core.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    implementation(libs.volley)

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
    implementation(libs.androidx.compose.material)

    implementation(libs.androidx.activity)
    ksp(libs.androidx.room.compiler)
    implementation(kotlin("stdlib-jdk8"))
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation (libs.androidx.security.crypto)
    implementation (libs.jwtdecode)

    implementation("com.google.android.material:material:1.4.0")

    // Mantieni Material 2 per BottomNavigation
    implementation("androidx.compose.material:material:1.4.3")

    // Material 3
    implementation("androidx.compose.material3:material3:1.1.1")
}

kotlin {
    jvmToolchain(21)
}