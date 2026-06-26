plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    // Conecta el google-services.json con la app para Firebase
    id("com.google.gms.google-services")
}

android {
    namespace = "com.jort.stockcontrolpm"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.jort.stockcontrolpm"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
    buildToolsVersion = "34.0.0"
}

dependencies {

    // ── Firebase BOM ──────────────────────────────────────────────────────────
    // BOM versión 33.7.0 — estable y compatible con AGP 8.x / Kotlin 2.x
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))

    // Firebase Authentication: registro e inicio de sesión con email/contraseña
    implementation("com.google.firebase:firebase-auth-ktx")

    // Cloud Firestore: base de datos NoSQL en la nube
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Firebase Cloud Messaging (FCM): notificaciones push
    implementation("com.google.firebase:firebase-messaging-ktx")

    // Soporte de corrutinas para Firebase Tasks (.await())
    // Provee la extensión kotlinx.coroutines.tasks.await que usamos en los repositorios
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")
    // ──────────────────────────────────────────────────────────────────────────

    implementation("androidx.compose.material:material-icons-extended")
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation("androidx.navigation:navigation-compose:2.9.8")
    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("io.coil-kt:coil-compose:2.7.0")
    annotationProcessor("androidx.room:room-compiler:2.8.4")

    testImplementation(libs.junit)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
