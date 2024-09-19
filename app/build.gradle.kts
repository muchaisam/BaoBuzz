plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.msdc.baobuzz"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.msdc.baobuzz"
        minSdk = 26
        targetSdk = 34
        versionCode = 3
        versionName = "0.1.0"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.9.1")
    implementation("androidx.paging:paging-common-android:3.3.2")
    implementation("androidx.paging:paging-runtime-ktx:3.3.2")
    implementation("androidx.hilt:hilt-work:1.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    val nav_version = "2.6.0-rc01"

    // Kotlin
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

    //splashscreen
    implementation("androidx.core:core-splashscreen:1.0.0-rc01")

    //circleimageview
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //Retrofit and Gson converter dependencies
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    //coil
    implementation("io.coil-kt:coil:1.4.0")

    implementation ("io.github.oshai:kotlin-logging-jvm:5.1.4")
    implementation("com.michael-bull.kotlin-retry:kotlin-retry:2.0.1")

    implementation("org.slf4j:slf4j-api:1.7.36")

    val room_version = "2.5.1"
    val work_version = "2.9.1"

    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    // Kotlin + coroutines
    implementation("androidx.work:work-runtime-ktx:$work_version")

    implementation("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

    //datetime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    //calendarview
    implementation("com.kizitonwose.calendar:view:2.5.4")

    //logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    //swipe refresh layout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    //SHIMMER
    implementation("com.facebook.shimmer:shimmer:0.5.0")


    // Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")

    // Compose
    implementation("androidx.compose.ui:ui:1.5.2")
    implementation("androidx.compose.material:material:1.5.2")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.2")
    implementation("androidx.activity:activity-compose:1.7.2")

    //coil compose
    implementation("io.coil-kt:coil-compose:2.2.2")

    // Accompanist for gesture handling
    implementation("com.google.accompanist:accompanist-pager:0.32.0")


    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")
    // Add Hilt Navigation Compose dependency
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
}
