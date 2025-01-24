plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.mperic.diabetesnotificaions"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mperic.diabetesnotificaions"
        minSdk = 26
        targetSdk = 34
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    
    // Room components
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    
    // Billing
    implementation("com.android.billingclient:billing:6.1.0")
    
    // Gson
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Splash screen
    implementation("androidx.core:core-splashscreen:1.0.1")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}