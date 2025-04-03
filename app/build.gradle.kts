plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.safeArgs)
    alias(libs.plugins.ksp)
}

android {
    namespace = "ru.kaycom.usagetime"
    compileSdk = 35

    defaultConfig {
        applicationId = "ru.kaycom.usagetime"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.animation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Lifecycle
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.livedata)

    //Koin
    implementation(libs.koin)

    //Navigation Component
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    //Glide
    implementation(libs.glide)

    //Timber
    implementation(libs.timber)

    //RecyclerView
    implementation(libs.androidx.recyclerView)
}