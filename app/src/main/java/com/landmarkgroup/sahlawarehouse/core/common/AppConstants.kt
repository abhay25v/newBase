package com.landmarkgroup.sahlawarehouse.core.common

object AppConstants {

    /** list of roles allowed to use the app (`Constants.Roles`). */
    val ALLOWED_ROLES = listOf(
        "store_associate", "store_manager", "store_associates", "store_managers",
        "store_supervisors", "store_officials", "area_managers", "retail_ops",
        "warehouse_managers", "warehouse_supervisors", "warehouse_associates",
        "warehouse_cbw_supervisors"
    )

    const val ROLE_WAREHOUSE_MANAGERS = "warehouse_managers"
    const val ROLE_WAREHOUSE_SUPERVISORS = "warehouse_supervisors"
    const val ROLE_WAREHOUSE_ASSOCIATES = "warehouse_associates"
    const val ROLE_WAREHOUSE_CBW_SUPERVISORS = "warehouse_cbw_supervisors"

    // ----- Feature flag keys (returned by GET /feature-flags), from Constants.cs -----
    const val FEATURE_LOGGING = "UI_FEATURE_ANALYTICS"
    const val HTTP_FEATURE_LOGGING = "UI_FEATURE_HTTP_ANALYTICS"
    const val EVENT_FEATURE_LOGGING = "UI_FEATURE_EVENT_ANALYTICS"
    const val GENERIC_FEATURE_LOGGING = "UI_FEATURE_GENERIC_ANALYTICS"
    const val ERROR_FEATURE_LOGGING = "UI_FEATURE_ERROR_ANALYTICS"
    const val FEATURE_SINGLE_WALL_CLOSE = "UI_FEATURE_SINGLE_WALL_CLOSE"
    const val FEATURE_NGINX_FLAG = "UI_FEATURE_NGINX_FLAG"
    const val UI_FEATURE_KONG_FLAG = "UI_FEATURE_KONG_FLAG"
    const val UI_FEATURE_PTW_OPTIMIZATION_FLAG = "UI_FEATURE_PTW_OPTIMIZATION_FLAG"
    const val UI_FEATURE_BULKSORT_FLAG = "UI_FEATURE_BULKSORT_FLAG"
    const val FEATURE_DOLLY_BUILDING_ALLOWED = "UI_FEATURE_DOLLY_BUILDING_ALLOWED"
    const val FEATURE_ECOM_FACILITY_FLAG = "UI_FEATURE_ECOM_FACILITY_FLAG"
    const val FEATURE_WAVE_CREATION_FLAG = "UI_FEATURE_WAVE_CREATION"
    const val FEATURE_AUTHORIZE_API_FLAG = "UI_FEATURE_AUTHORIZE_API_FLAG"
    const val FEATURE_ENABLE_CRBW_VSI_FILTER = "UI_FEATURE_CRBW_VSI_FILTER"
    const val FEATURE_CASE_PICKING_ENABLE = "UI_FEATURE_CASE_PICKING_ENABLE"
    const val FEATURE_MULTI_STORE_ENABLE = "UI_FEATURE_MULTI_STORE_ENABLE"
    const val FEATURE_ENABLE_PRESORT_LOCATION = "UI_FEATURE_PRESORT_ENABLED"
    const val FEATURE_CRBW_CROSS_CONCEPTS = "UI_FEATURE_CRBW_CROSS_CONCEPTS"
    const val FEATURE_MODON_ENABLE = "UI_FEATURE_CRBW_LOCATION_TYPE_FILTER"
    const val FEATURE_CRBW_ENABLE_MCMP_MLDC_IN_HC = "UI_FEATURE_CRBW_ENABLE_MCMP_MLDC_IN_HC"
    const val FEATURE_IMAGE_URL = "UI_FEATURE_IMAGE_URL"

    // ----- Language ids (Constants.cs #region language) -----
    const val LANGUAGE_ENGLISH_ID = "1"
    const val LANGUAGE_ARABIC_ID = "2"
    const val LANGUAGE_HINDI_ID = "3"
    const val LANGUAGE_BANGLA_ID = "4"

    // ----- HTTP status codes used for ping/connectivity indication -----
    const val HTTP_STATUS_SUCCESS = 200
    const val HTTP_STATUS_TEAPOT = 418
    const val HTTP_STATUS_NOT_FOUND = 404

    /** Dammam CDC facility id - excluded from dolly-building/feature-flag calls in the old app. */
    const val FACILITY_DC = "2100000"
}
