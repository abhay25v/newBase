package com.landmarkgroup.sahlawarehouse.feature.login.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterDeviceModel(
    @SerialName("user") val user: String? = null,
    @SerialName("device") val device: String? = null,
    @SerialName("override") val override: Boolean = false,
    @SerialName("user_status") val userStatus: Int = 0,
    @SerialName("user_name") val userName: String? = null,
    @SerialName("mac_id") val macId: String? = null,
    @SerialName("capture_device_id") val captureDeviceId: Boolean = false,
    @SerialName("feature_enabled") val featureEnabled: Boolean = false
)
