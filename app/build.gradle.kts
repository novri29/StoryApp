plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.nov.storyapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.nov.storyapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_URL", "\"https://story-api.dicoding.dev/v1/\"")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = (freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn").toMutableList()
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofitGson)
    implementation(libs.okhttpLoggingInterceptor)
    implementation(libs.lifecycleRuntimeKtx)

    // KTX
    implementation(libs.datastorePreferences)
    implementation(libs.lifecycleViewModelKtx)
    implementation(libs.lifecycleLiveDataKtx)
    implementation(libs.activityKtx)

    // Material Design
    implementation(libs.material)

    // Glide
    implementation(libs.glide)

    // Gson
    implementation(libs.gson)

    // Camera
    implementation(libs.cameraCamera2)
    implementation(libs.cameraLifecycle)
    implementation(libs.cameraView)

    // UCrop
    implementation(libs.ucrop)

    // Paging
    implementation("androidx.paging:paging-runtime-ktx:3.2.1")
    implementation("androidx.room:room-paging:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)

    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
}