package com.landmarkgroup.sahlawarehouse.feature.login.data.repository

import com.landmarkgroup.sahlawarehouse.core.common.AppConstants
import com.landmarkgroup.sahlawarehouse.core.data.settings.FeatureFlagSnapshot
import com.landmarkgroup.sahlawarehouse.feature.login.data.model.FeatureFlagModel

fun List<FeatureFlagModel>?.toFeatureFlagSnapshot(): FeatureFlagSnapshot {
    fun flag(name: String): Boolean =
        this?.firstOrNull { it.featureName == name }?.featureEnabledValue ?: false

    return FeatureFlagSnapshot(
        imageServerBaseUrlFlag = flag(AppConstants.FEATURE_IMAGE_URL),
        isLoggerEnabled = flag(AppConstants.FEATURE_LOGGING),
        isApiTrackingEnabled = flag(AppConstants.HTTP_FEATURE_LOGGING),
        isEventTrackingEnabled = flag(AppConstants.EVENT_FEATURE_LOGGING),
        isLogsTrackingEnabled = flag(AppConstants.GENERIC_FEATURE_LOGGING),
        isErrorTrackingEnabled = flag(AppConstants.ERROR_FEATURE_LOGGING),
        isSingleWallCloseEnabled = flag(AppConstants.FEATURE_SINGLE_WALL_CLOSE),
        isNginxUrlEnabled = flag(AppConstants.FEATURE_NGINX_FLAG),
        presortLocationEnabled = flag(AppConstants.FEATURE_ENABLE_PRESORT_LOCATION),
        isDollyBuildingAllowed = flag(AppConstants.FEATURE_DOLLY_BUILDING_ALLOWED),
        isEcomFacility = flag(AppConstants.FEATURE_ECOM_FACILITY_FLAG),
        isWaveCreationEnabled = flag(AppConstants.FEATURE_WAVE_CREATION_FLAG),
        featureAuthorizeApiUrlBase = flag(AppConstants.FEATURE_AUTHORIZE_API_FLAG),
        isCrbwVsiEnabled = flag(AppConstants.FEATURE_ENABLE_CRBW_VSI_FILTER),
        isCasePickingEnabled = flag(AppConstants.FEATURE_CASE_PICKING_ENABLE),
        isMultiPickingEnabled = flag(AppConstants.FEATURE_MULTI_STORE_ENABLE),
        isKongUrlEnabled = flag(AppConstants.UI_FEATURE_KONG_FLAG),
        isPtwOptimisationEnabled = flag(AppConstants.UI_FEATURE_PTW_OPTIMIZATION_FLAG),
        isCrbwCrossConcepts = flag(AppConstants.FEATURE_CRBW_CROSS_CONCEPTS),
        isBulkSortEnabled = flag(AppConstants.UI_FEATURE_BULKSORT_FLAG),
        isCrbwEnabledInHc = flag(AppConstants.FEATURE_CRBW_ENABLE_MCMP_MLDC_IN_HC),
        isModonEnabled = flag(AppConstants.FEATURE_MODON_ENABLE)
    )
}
