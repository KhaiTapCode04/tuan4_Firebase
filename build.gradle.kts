plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services") // Plugin cho Google Services
}

android {
    namespace = "com.example.tuan4"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tuan4"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Core Android dependencies
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth-ktx")

    // Firebase Storage
    implementation("com.google.firebase:firebase-storage-ktx")

    // Glide (Nếu chưa có)
    implementation("com.github.bumptech.glide:glide:4.11.0")
    // Firebase BOM để quản lý phiên bản Firebase đồng bộ
    implementation(platform("com.google.firebase:firebase-bom:33.11.0"))

    // Firebase Authentication và Analytics
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Google Sign-In SDK
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Glide để tải ảnh
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // CircleImageView để hiển thị avatar tròn
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}