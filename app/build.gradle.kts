plugins {
    id("com.android.application")
}

android {
    namespace = "com.ridvan.planim"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ridvanozdemir.planim"
        minSdk = 26
        targetSdk = 36
        versionCode = 4
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
