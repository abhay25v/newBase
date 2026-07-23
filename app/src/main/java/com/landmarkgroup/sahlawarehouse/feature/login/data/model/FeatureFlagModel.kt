package com.landmarkgroup.sahlawarehouse.feature.login.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeatureFlagModel(
    @SerialName("feature_name") val featureName: String? = null,
    @SerialName("feature_enabled_value") val featureEnabledValue: Boolean = false
)

@Serializable
data class UserConceptResponseModel(
    @SerialName("concept") val concept: ConceptModel? = null
)

@Serializable
data class ConceptModel(
    @SerialName("code") val code: String? = null,
    @SerialName("name") val name: String? = null
)
