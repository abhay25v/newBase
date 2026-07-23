import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.landmarkgroup.sahlawarehouse"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.landmarkgroup.sahlawarehouse"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    // Mirrors the old app's environment configs (Dev / SIT / FT / PT / Prod)
    // Each flavor supplies its own base URLs / client ids via BuildConfig fields,
    // matching SahlaWH.APIManager/APIConfiguration/BaseUrlConfig.cs
    flavorDimensions += "environment"
    productFlavors {
        // Environment endpoints below are ported 1:1 from
        // SahlaWH.APIManager/APIConfiguration/BaseUrlConfig.cs + APIConstants.cs of the old app
        // (DevBaseUrlDNS/SITBaseUrlDNS/FTBaseUrlDNS/PTBaseUrlDNS/ProdBaseUrlDNS and their
        // Nginx/Kong counterparts), so networking behaves identically against the same backend.
        //
        // ADFS_CLIENT_ID is `FormsApp.adfsClientId` ("Sahla Store" ADFS app registration) - it is
        // NOT environment-specific in the old app (hardcoded once, used for every flavor).
        // IBM_CLIENT_ID / LANDMARK_CLIENT_ID are both `APIConstants.ClientId` (the old app sends
        // the *same* value for both the x-ibm-client-id and x-landmark-client-id headers).
        //
        // ADFS_REDIRECT_URI MUST be exactly "http://SahlaApp" - this is the literal redirect URI
        // registered on the ADFS relying-party trust for adfsClientId below (see the old app's
        // `FormsApp.adfsReturnUri`). ADFS validates redirect_uri against its registered value(s)
        // and returns a generic "An error occurred, contact your administrator" page on any
        // mismatch - it is NOT environment-specific and NOT a real navigable URL: sign-in happens
        // entirely inside an in-app WebView (see AuthWebViewDialog/AuthManager) which intercepts
        // navigation to this URI itself via WebViewClient before it ever reaches the OS, so unlike
        // a Custom-Tabs based AppAuth flow, no OS-level deep-link intent-filter is needed for it.
        val adfsAuthority = "https://sts.landmarkgroup.com/adfs"
        val adfsClientId = "5a71b107-a456-4a68-b2d2-c1c8d09d7757"
        val adfsRedirectUri = "http://SahlaApp"
        val adfsResource = "https://SahlaWebApi"
        val nonProdClientId = "612f0b51-8594-4a44-93f0-265113649943"


        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            buildConfigField("String", "ENVIRONMENT_NAME", "\"Development\"")
            buildConfigField("String", "LEGACY_BASE_URL", "\"https://apidev.landmarkgroup.com/landmarkgroup/retail-dev/v2/\"")
            buildConfigField("String", "NGINX_BASE_URL", "\"\"")
            buildConfigField("String", "KONG_BASE_URL", "\"https://devapi3.landmarkgroup.com/\"")
            buildConfigField("String", "IBM_CLIENT_ID", "\"$nonProdClientId\"")
            buildConfigField("String", "LANDMARK_CLIENT_ID", "\"$nonProdClientId\"")
            buildConfigField("String", "ADFS_AUTHORITY", "\"$adfsAuthority\"")
            buildConfigField("String", "ADFS_CLIENT_ID", "\"$adfsClientId\"")
            buildConfigField("String", "ADFS_REDIRECT_URI", "\"$adfsRedirectUri\"")
            buildConfigField("String", "ADFS_RESOURCE", "\"$adfsResource\"")
            manifestPlaceholders["appAuthRedirectScheme"] = "com.landmarkgroup.sahlawarehouse.dev"

        }
        create("sit") {
            dimension = "environment"
            applicationIdSuffix = ".sit"
            versionNameSuffix = "-sit"
            buildConfigField("String", "ENVIRONMENT_NAME", "\"SIT\"")
            buildConfigField("String", "LEGACY_BASE_URL", "\"https://apidev.landmarkgroup.com/landmarkgroup/retail-sit/v2/\"")
            buildConfigField("String", "NGINX_BASE_URL", "\"https://devapi1.landmarkgroup.com/warehouse-ops/\"")
            buildConfigField("String", "KONG_BASE_URL", "\"https://devapi3.landmarkgroup.com/\"")
            buildConfigField("String", "IBM_CLIENT_ID", "\"$nonProdClientId\"")
            buildConfigField("String", "LANDMARK_CLIENT_ID", "\"$nonProdClientId\"")
            buildConfigField("String", "ADFS_AUTHORITY", "\"$adfsAuthority\"")
            buildConfigField("String", "ADFS_CLIENT_ID", "\"$adfsClientId\"")
            buildConfigField("String", "ADFS_REDIRECT_URI", "\"$adfsRedirectUri\"")
            buildConfigField("String", "ADFS_RESOURCE", "\"$adfsResource\"")
            manifestPlaceholders["appAuthRedirectScheme"] = "com.landmarkgroup.sahlawarehouse.sit"

        }
        create("ft") {
            dimension = "environment"
            applicationIdSuffix = ".ft"
            versionNameSuffix = "-ft"
            buildConfigField("String", "ENVIRONMENT_NAME", "\"FT\"")
            buildConfigField("String", "LEGACY_BASE_URL", "\"https://apidev.landmarkgroup.com/landmarkgroup/retail-ft/v2/\"")
            buildConfigField("String", "NGINX_BASE_URL", "\"https://ftapi1.landmarkgroup.com/warehouse-ops/\"")
            // Old app comment: "changing ft to pt" - FT's Kong gateway intentionally points at the PT Kong host.
            buildConfigField("String", "KONG_BASE_URL", "\"https://ptapi3.landmarkgroup.com/\"")
            buildConfigField("String", "IBM_CLIENT_ID", "\"$nonProdClientId\"")
            buildConfigField("String", "LANDMARK_CLIENT_ID", "\"$nonProdClientId\"")
            buildConfigField("String", "ADFS_AUTHORITY", "\"$adfsAuthority\"")
            buildConfigField("String", "ADFS_CLIENT_ID", "\"$adfsClientId\"")
            buildConfigField("String", "ADFS_REDIRECT_URI", "\"$adfsRedirectUri\"")
            buildConfigField("String", "ADFS_RESOURCE", "\"$adfsResource\"")
            manifestPlaceholders["appAuthRedirectScheme"] = "com.landmarkgroup.sahlawarehouse.ft"

        }
        create("pt") {
            dimension = "environment"
            applicationIdSuffix = ".pt"
            versionNameSuffix = "-pt"
            buildConfigField("String", "ENVIRONMENT_NAME", "\"PT\"")
            buildConfigField("String", "LEGACY_BASE_URL", "\"https://apidev.landmarkgroup.com/landmarkgroup/retail-pt/v2/\"")
            buildConfigField("String", "NGINX_BASE_URL", "\"\"")
            buildConfigField("String", "KONG_BASE_URL", "\"https://ptapi3.landmarkgroup.com/\"")
            buildConfigField("String", "IBM_CLIENT_ID", "\"$nonProdClientId\"")
            buildConfigField("String", "LANDMARK_CLIENT_ID", "\"$nonProdClientId\"")
            buildConfigField("String", "ADFS_AUTHORITY", "\"$adfsAuthority\"")
            buildConfigField("String", "ADFS_CLIENT_ID", "\"$adfsClientId\"")
            buildConfigField("String", "ADFS_REDIRECT_URI", "\"$adfsRedirectUri\"")
            buildConfigField("String", "ADFS_RESOURCE", "\"$adfsResource\"")
            manifestPlaceholders["appAuthRedirectScheme"] = "com.landmarkgroup.sahlawarehouse.pt"

        }
        create("prod") {
            dimension = "environment"
            buildConfigField("String", "ENVIRONMENT_NAME", "\"Production\"")
            buildConfigField("String", "LEGACY_BASE_URL", "\"https://api.landmarkgroup.com/v2/\"")
            buildConfigField("String", "NGINX_BASE_URL", "\"https://Api1.landmarkgroup.com/warehouse-ops/\"")
            buildConfigField("String", "KONG_BASE_URL", "\"https://api3.landmarkgroup.com/\"")
            buildConfigField("String", "IBM_CLIENT_ID", "\"baf8eee6-c121-49cf-b1db-887074105c1b\"")
            buildConfigField("String", "LANDMARK_CLIENT_ID", "\"baf8eee6-c121-49cf-b1db-887074105c1b\"")
            buildConfigField("String", "ADFS_AUTHORITY", "\"$adfsAuthority\"")
            buildConfigField("String", "ADFS_CLIENT_ID", "\"$adfsClientId\"")
            buildConfigField("String", "ADFS_REDIRECT_URI", "\"$adfsRedirectUri\"")
            buildConfigField("String", "ADFS_RESOURCE", "\"$adfsResource\"")
            manifestPlaceholders["appAuthRedirectScheme"] = "com.landmarkgroup.sahlawarehouse"

        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.splashscreen)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.security.crypto)

    implementation(libs.timber)

    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.mlkit.barcode.scanning)

    implementation(libs.accompanist.permissions)

    // ADFS / OAuth2-OIDC login (Authorization Code + PKCE), replacing the old ADAL
    implementation(libs.appauth)


    // Scandit SDK - add once license/artifact repo credentials are configured.
    // implementation("com.scandit.datacapture:core:7.x.x")
    // implementation("com.scandit.datacapture:barcode:7.x.x")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
}
