package com.landmarkgroup.sahlawarehouse.feature.login.ui

import com.landmarkgroup.sahlawarehouse.core.common.FacilityModel


enum class NetworkStatus { ONLINE, CONSTRAINED, OFFLINE }

/** Immutable UI state for [LoginViewModel] - single source of truth for [LoginScreen]. */
data class LoginUiState(
    val isBusy: Boolean = false,
    val isArabicOn: Boolean = false,
    val isProduction: Boolean = false,
    val networkStatus: NetworkStatus = NetworkStatus.OFFLINE,
    val loginButtonEnabled: Boolean = true,
    val languageList: List<FacilityModel> = emptyList(),
    val showLanguageList: Boolean = false,
    val facilityList: List<FacilityModel> = emptyList(),
    val showFacilityList: Boolean = false,
    val showDeviceCapturePopup: Boolean = false,
    val showSecondLoginAlert: Boolean = false,
    val secondLoginAlertMessage: String = "",
    val deviceIdInput: String = "",
    val errorMessage: String? = null,
    val toastMessage: String? = null,
    val authWebViewUrl: String? = null,
    val loginCompleted: Boolean = false
)

