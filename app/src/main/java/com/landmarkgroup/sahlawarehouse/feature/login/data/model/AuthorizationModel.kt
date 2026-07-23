package com.landmarkgroup.sahlawarehouse.feature.login.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthorizationModel(
    @SerialName("id") val userId: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("authorizations") val authorizations: Authorizations? = null,
    @SerialName("capture_device_id") val captureDeviceId: Boolean = false,
    @SerialName("validity") val validity: ValidityInfo? = null
)

@Serializable
data class Authorizations(
    @SerialName("concepts") val concepts: List<String> = emptyList(),
    @SerialName("territories") val territories: List<String> = emptyList(),
    @SerialName("roles") val roles: List<String> = emptyList(),
    @SerialName("locations") val locations: List<String> = emptyList()
)

@Serializable
data class ValidityInfo(
    @SerialName("issued_at") val issuedAt: Long = 0,
    @SerialName("expires_at") val expiresAt: Long = 0,
    @SerialName("total_time") val totalTime: Long = 0
)
