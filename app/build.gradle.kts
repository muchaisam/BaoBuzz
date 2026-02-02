import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20"
    id("dagger.hilt.android.plugin")
}

// Load local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.msdc.baobuzz"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.msdc.baobuzz"
        minSdk = 26
        targetSdk = 34
        versionCode = 5
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Add API keys from local.properties
        buildConfigField("String", "FOOTBALL_API_KEY", "\"${localProperties.getProperty("API_KEY") ?: ""}\"")
        buildConfigField("String", "FOOTBALL_DATA_API_KEY", "\"${localProperties.getProperty("FOOTBALL_DATA_API_KEY") ?: ""}\"")
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

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,DEPENDENCIES}"
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }

}

dependencies {
    val lifecycleVersion = "2.7.0"
    val roomVersion = "2.6.1"
    val hiltVersion = "2.48.1"
    val retrofitVersion = "2.9.0"
    val okhttpVersion = "4.12.0"
    val navVersion = "2.7.6"
    val coilVersion = "2.5.0"

    // AndroidX Core - Compatible with Kotlin 1.9.0
    implementation("androidx.core:core-ktx:1.12.0") {
        version {
            strictly("1.12.0")
        }
    }
    implementation("androidx.core:core:1.12.0") {
        version {
            strictly("1.12.0")
        }
    }
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Compose - Using BOM compatible with Kotlin 1.9.0
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material:material-icons-extended")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Material Design
    implementation("com.google.android.material:material:1.11.0")

    // Navigation - Pure Compose
    implementation("androidx.navigation:navigation-compose:$navVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")

    // Hilt - Compatible with Kotlin 1.9.0
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    ksp("com.google.dagger:hilt-android-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-work:1.1.0")
    ksp("androidx.hilt:hilt-compiler:1.1.0")

    // Room - Compatible with Kotlin 1.9.0
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Retrofit & OkHttp - Compatible with Kotlin 1.9.0
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")

    // CircleImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Timber
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    ksp("com.github.bumptech.glide:compiler:4.16.0")

    // Kotlin DateTime - Compatible with Kotlin 1.9.0
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")

    // Coil - Compatible with Kotlin 1.9.0
    implementation("io.coil-kt:coil-compose:$coilVersion")

    // Kotlin Serialization - Compatible with Kotlin 1.9.0
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Splashscreen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Calendar - Compatible with Kotlin 1.9.0
    implementation("com.kizitonwose.calendar:compose:2.4.1")
    implementation("com.kizitonwose.calendar:view:2.4.1")

    // Paging - Compatible with Kotlin 1.9.0
    implementation("androidx.paging:paging-runtime-ktx:3.2.1")
    implementation("androidx.paging:paging-compose:3.2.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}