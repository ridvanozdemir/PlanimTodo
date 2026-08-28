plugins {
    id("com.android.application")
}

val releaseStorePath = System.getenv("PLANIM_KEYSTORE_PATH")
val releaseStorePassword = System.getenv("PLANIM_KEYSTORE_PASSWORD")
val releaseKeyAlias = System.getenv("PLANIM_KEY_ALIAS")
val releaseKeyPassword = System.getenv("PLANIM_KEY_PASSWORD")

android {
    namespace = "com.ridvan.planim"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ridvanozdemir.planim"
        minSdk = 26
        targetSdk = 36
        versionCode = 9
        versionName = "1.1.4"
    }

    signingConfigs {
        if (
            !releaseStorePath.isNullOrBlank() &&
            !releaseStorePassword.isNullOrBlank() &&
            !releaseKeyAlias.isNullOrBlank() &&
            !releaseKeyPassword.isNullOrBlank()
        ) {
            create("release") {
                storeFile = file(releaseStorePath)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfigs.findByName("release")?.let { signingConfig = it }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
