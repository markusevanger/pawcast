plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "no.uio.ifi.in2000.team19.prosjekt"
    compileSdk = 34

    defaultConfig {
        applicationId = "no.uio.ifi.in2000.team19.prosjekt"
        minSdk = 26 // 26 to support more tools like LocalDate
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        android.buildFeatures.buildConfig = true // added by us
        compose = true
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

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.room:room-common:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.05.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")


    // EGNE DEPENDENCIES

    // ktor dependencies
    val ktorVersion = "2.3.8"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-gson:$ktorVersion")


    implementation("ch.qos.logback:logback-classic:1.2.3") // Logging
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0") // collectAsStateWithLifecycle


    // MapBox search
    implementation("com.mapbox.search:mapbox-search-android-ui:1.2.0")

    //Room database
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    //graphs
    implementation("com.patrykandpatrick.vico:compose-m3:2.0.0-alpha.14")

    //Dagger Hilt (dependency injection
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-compiler:2.51.1")
    ksp("androidx.hilt:hilt-compiler:1.2.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")


    //Pull to refresh
    implementation("eu.bambooapps:compose-material3-pullrefresh:1.1.1")

    // Extended icons pack for more choice. Needed for cloud icon for navbar
    implementation("androidx.compose.material:material-icons-extended:1.6.7")


    // DataStore. Storing preferences to local storage (used to remember if user has completed setup)
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    //Material Design 3
    //implementation("androidx.compose.material3:material3:1.2.1")
    ///implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.0.0-alpha07")
    // implementation("androidx.compose.material3:material3-window-size-class:1.2.1")  // would be used to scale the app for different screens https://m3.material.io/foundations/layout/applying-layout/window-size-classes
    //
    // ^We never ended up using these libraries



    // MARKDOWN LIBRARY for advice screens
    implementation("com.github.jeziellago:compose-markdown:0.5.0")

}
