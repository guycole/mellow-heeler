plugins {
    id("com.android.application")
}

android {
    namespace = "net.braingang.heeler"
    compileSdk = 34

    defaultConfig {
        applicationId = "net.braingang.heeler"
        minSdk = 34
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
    implementation("pub.devrel:easypermissions:3.0.0")

    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation("com.amazonaws:aws-android-sdk-s3:2.6.30")
    implementation("com.amazonaws:aws-android-sdk-core:2.6.30")
    implementation("com.amazonaws:aws-android-sdk-ddb:2.6.30")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("com.google.android.material:material:1.11.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}