plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.provandoperdavvero"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.provandoperdavvero"
        minSdk = 21
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    //implementation(libs.androidx.compose.ui) // Aggiornato
    implementation(libs.androidx.ui)
    //implementation(libs.androidx.compose.ui.graphics) // Aggiornato
    implementation(libs.androidx.ui.graphics)
    //implementation(libs.androidx.compose.ui.tooling.preview) // Aggiornato
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)


    implementation("androidx.navigation:navigation-compose:2.6.0") // Aggiornato
    //implementation("androidx.navigation:navigation-compose:2.5.3")


    //implementation "androidx.navigation:navigation-compose:2.5.3"  // o versione più recente
    implementation ("androidx.compose.material3:material3:1.0.0") // Material3
    implementation ("androidx.compose.ui:ui:1.5.0")  // Compose UI
    implementation ("androidx.compose.runtime:runtime:1.5.0")



    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    //androidTestImplementation(libs.androidx.compose.ui.test.junit4) // Aggiornato
    androidTestImplementation(libs.androidx.ui.test.junit4)
    //debugImplementation(libs.androidx.compose.ui.tooling) // Aggiornato
    debugImplementation(libs.androidx.ui.tooling)
    //debugImplementation(libs.androidx.compose.ui.test.manifest) // Aggiornato
    debugImplementation(libs.androidx.ui.test.manifest)



}