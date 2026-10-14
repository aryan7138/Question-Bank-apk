signingConfigs {
    // Only configure a release keystore if the CI environment provides it
    create("release") {
        val keystorePath = System.getenv("KEYSTORE_PATH")
        if (!keystorePath.isNullOrBlank()) {
            storeFile = file(keystorePath)
            storePassword = System.getenv("STORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS") ?: "upload"
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
}

buildTypes {
    debug {
        // Do NOT override debug signing with a non-existent repo-local keystore.
        // Android will use the default ~/.android/debug.keystore.
    }

    release {
        isMinifyEnabled = false
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )

        val releaseSigning = signingConfigs.findByName("release")
        if (releaseSigning != null &&
            !System.getenv("KEYSTORE_PATH").isNullOrBlank()
        ) {
            signingConfig = releaseSigning
        }
    }
}
