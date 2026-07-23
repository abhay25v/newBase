package com.landmarkgroup.sahlawarehouse.core.common

/**
 * UI model for facility/language pickers. Direct port of `SahlaWH.Models.CustomViewsModel.FacilityModel`.
 */
data class FacilityModel(
    val facilityId: String,
    val facilityName: String,
    val isSelected: Boolean = false
)
