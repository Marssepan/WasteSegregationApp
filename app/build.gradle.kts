plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.wastesegregationapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.wastesegregationapp"
        minSdk = 29
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation (libs.room.runtime)
    annotationProcessor (libs.room.compiler)

    implementation (libs.tensorflow.tensorflow.lite)
    implementation (libs.tensorflow.lite.support)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}