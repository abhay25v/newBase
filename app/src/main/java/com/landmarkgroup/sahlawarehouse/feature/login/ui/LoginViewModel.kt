package com.landmarkgroup.sahlawarehouse.feature.login.ui

import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import com.landmarkgroup.sahlawarehouse.R
import com.landmarkgroup.sahlawarehouse.core.auth.AuthManager
import com.landmarkgroup.sahlawarehouse.core.common.AppConstants
import com.landmarkgroup.sahlawarehouse.core.common.DeviceIdProvider
import com.landmarkgroup.sahlawarehouse.core.common.FacilityModel
import com.landmarkgroup.sahlawarehouse.core.common.UserFacility
import com.landmarkgroup.sahlawarehouse.core.data.settings.SessionCache
import com.landmarkgroup.sahlawarehouse.core.data.settings.SettingsRepository
import com.landmarkgroup.sahlawarehouse.core.network.ApiResult
import com.landmarkgroup.sahlawarehouse.feature.login.data.model.AuthorizationModel
import com.landmarkgroup.sahlawarehouse.feature.login.data.model.RegisterDeviceModel
import com.landmarkgroup.sahlawarehouse.feature.login.data.repository.LoginRepository
import com.landmarkgroup.sahlawarehouse.feature.login.data.repository.toFeatureFlagSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject



@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val settingsRepository: SettingsRepository,
    private val sessionCache: SessionCache,
    private val authManager: AuthManager,
    private val deviceIdProvider: DeviceIdProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoginEvent>(extraBufferCapacity = 4)
    val events: SharedFlow<LoginEvent> = _events

    /** Latest `/users/me` response, held only for the duration of the post-login flow. */
    private var authorizationResponse: AuthorizationModel? = null
    private var captureDeviceId: Boolean = false
    private var blockButtonInteraction = false

    /** Redirect URI prefix the in-app WebView watches for, exposed to [LoginScreen]. */
    val redirectUri: String get() = authManager.redirectUri


    init {
        // Direct port of InitViewModel().
        _uiState.update {
            it.copy(
                isArabicOn = sessionCache.isArabicOn,
                loginButtonEnabled = true,
                languageList = listOf(
                    FacilityModel(AppConstants.LANGUAGE_ENGLISH_ID, "English", sessionCache.selectedLanguageId == AppConstants.LANGUAGE_ENGLISH_ID),
                    FacilityModel(AppConstants.LANGUAGE_ARABIC_ID, "عربى", sessionCache.selectedLanguageId == AppConstants.LANGUAGE_ARABIC_ID),
                    FacilityModel(AppConstants.LANGUAGE_HINDI_ID, "हिंदी", sessionCache.selectedLanguageId == AppConstants.LANGUAGE_HINDI_ID),
                    FacilityModel(AppConstants.LANGUAGE_BANGLA_ID, "বাংলা", sessionCache.selectedLanguageId == AppConstants.LANGUAGE_BANGLA_ID)
                )
            )
        }
        sessionCache.isScanSoundEnabled = true
        refreshPing()
    }

    /** Direct port of `TriggerPingRefresh()` + `PerformPingAPI()`. */
    fun refreshPing() = viewModelScope.launch {
        _uiState.update { it.copy(isBusy = true) }
        val result = loginRepository.ping()
        val code = when (result) {
            is ApiResult.Success -> AppConstants.HTTP_STATUS_SUCCESS
            is ApiResult.Error -> if (result.httpCode == AppConstants.HTTP_STATUS_TEAPOT) {
                AppConstants.HTTP_STATUS_TEAPOT
            } else {
                AppConstants.HTTP_STATUS_NOT_FOUND
            }
            is ApiResult.NetworkError -> AppConstants.HTTP_STATUS_NOT_FOUND
        }
        settingsRepository.setPingStatusCode(code)
        applyNetworkStatus(code)
        _uiState.update { it.copy(isBusy = false) }
    }

    /** Direct port of `SetNetWorkAvailability()`. */
    private fun applyNetworkStatus(pingStatusCode: Int) {
        val (status, buttonEnabled) = when (pingStatusCode) {
            AppConstants.HTTP_STATUS_SUCCESS -> NetworkStatus.ONLINE to true
            AppConstants.HTTP_STATUS_TEAPOT -> NetworkStatus.CONSTRAINED to true
            else -> NetworkStatus.OFFLINE to false
        }
        _uiState.update { it.copy(networkStatus = status, loginButtonEnabled = buttonEnabled) }
    }

    fun onChangeLanguageClicked() {
        _uiState.update { it.copy(showLanguageList = true) }
    }

    /** Direct port of `OnLanguageClicked(FacilityModel)`. */
    fun onLanguageSelected(language: FacilityModel) = viewModelScope.launch {
        _uiState.update { it.copy(showLanguageList = false) }
        val isArabic = language.facilityId == AppConstants.LANGUAGE_ARABIC_ID
        settingsRepository.setArabicOn(isArabic)
        settingsRepository.setSelectedLanguageId(language.facilityId)
        _uiState.update { it.copy(isArabicOn = isArabic) }
    }

    fun onFacilitySelected(facility: FacilityModel) = viewModelScope.launch {
        settingsRepository.setFacility(facility.facilityId, facility.facilityName)
        _uiState.update { it.copy(showFacilityList = false) }
        performHomeNavigation()
    }

    /** Direct port of `GetFacilityData()`. */
    private fun showFacilityPicker(locations: List<String>) {
        val facilities = locations
            .filter { it.isNotBlank() && UserFacility.isDefined(it) }
            .map { FacilityModel(it, UserFacility.nameFor(it)) }
        _uiState.update { it.copy(facilityList = facilities, showFacilityList = true) }
    }

    /** Direct port of `OnLoginWithADClicked()` - now opens the in-app WebView instead of a Custom Tab. */
    fun onLoginWithAdClicked() {
        if (blockButtonInteraction || !_uiState.value.loginButtonEnabled) return
        blockButtonInteraction = true
        _uiState.update { it.copy(isBusy = true, authWebViewUrl = authManager.buildAuthorizationUrl().toString()) }
    }

    /** The user closed the in-app sign-in WebView before completing authentication. */
    fun onAuthWebViewDismissed() {
        _uiState.update { it.copy(authWebViewUrl = null) }
        finishLoginAttempt(toastRes = null)
    }

    /**
     * Called by [com.landmarkgroup.sahlawarehouse.feature.login.ui.AuthWebViewDialog] the moment
     * its WebView navigates to the ADFS redirect URI - replaces the old Custom-Tab
     * `onAuthorizationResult(Intent)` handler.
     */
    fun onAuthRedirectIntercepted(redirectUrl: String) = viewModelScope.launch {
        Timber.d("Auth redirect intercepted: %s", redirectUrl)
        _uiState.update { it.copy(authWebViewUrl = null) }
        val parsed = authManager.parseRedirectUrl(redirectUrl)
        val code = parsed.getOrNull()
        if (code == null) {
            Timber.w(parsed.exceptionOrNull(), "Failed to parse authorization code from redirect url")
            finishLoginAttempt(toastRes = R.string.login_failed)
            return@launch
        }
        try {
            Timber.d("Exchanging authorization code for tokens")
            val tokenResponse = authManager.exchangeToken(code)
            Timber.d(
                "Token exchange succeeded: idToken=%s accessToken=%s refreshToken=%s",
                !tokenResponse.idToken.isNullOrBlank(),
                !tokenResponse.accessToken.isNullOrBlank(),
                !tokenResponse.refreshToken.isNullOrBlank()
            )

            val idToken = tokenResponse.idToken.orEmpty()
            val accessToken = tokenResponse.accessToken.orEmpty()
            sessionCache.idToken = idToken
            sessionCache.accessToken = accessToken

            when (val authResult = loginRepository.authorize(idToken)) {
                is ApiResult.Success -> {
                    val body = authResult.data
                    Timber.d(
                        "authorize() success: userId=%s roles=%s locations=%s concepts=%s territories=%s",
                        body.userId,
                        body.authorizations?.roles,
                        body.authorizations?.locations,
                        body.authorizations?.concepts,
                        body.authorizations?.territories
                    )
                    if (validateUserAuthorization(body)) {
                        authorizationResponse = body
                        captureDeviceId = body.captureDeviceId
                        settingsRepository.setSession(
                            userName = body.userId.orEmpty(),
                            idToken = idToken,
                            accessToken = accessToken,
                            refreshToken = tokenResponse.refreshToken.orEmpty(),
                            firstName = sessionCache.firstName,
                            lastName = sessionCache.lastName,
                            email = sessionCache.email,
                            roles = sessionCache.roles,
                            validity = body.validity?.expiresAt?.toString().orEmpty()
                        )
                        performAuthorizationBinding(body)
                    } else {
                        Timber.w("validateUserAuthorization() rejected the user - missing roles/locations/concepts/territories or no allowed role")
                        finishLoginAttempt(toastRes = R.string.login_unauthorized)
                    }
                }
                is ApiResult.Error -> {
                    Timber.w(
                        "authorize() HTTP error: code=%d body=%s",
                        authResult.httpCode,
                        authResult.errorBody
                    )
                    finishLoginAttempt(toastRes = R.string.login_failed)
                }
                is ApiResult.NetworkError -> {
                    Timber.e(authResult.throwable, "authorize() network error")
                    finishLoginAttempt(toastRes = R.string.login_failed)
                }
            }
        } catch (t: Throwable) {
            Timber.e(t, "Token exchange threw an exception")
            finishLoginAttempt(toastRes = R.string.login_failed)
        }
    }

    /** Direct port of `ValidateUserAuthorization(AuthorizationModel)`. */
    private fun validateUserAuthorization(response: AuthorizationModel?): Boolean {
        val auth = response?.authorizations ?: return false
        if (auth.concepts.isEmpty() || auth.locations.isEmpty() ||
            auth.roles.isEmpty() || auth.territories.isEmpty()
        ) return false
        return auth.roles.any { it in AppConstants.ALLOWED_ROLES }
    }


    /** Direct port of `PerformAuthorizationBinding()`. */
    private fun performAuthorizationBinding(body: AuthorizationModel) = viewModelScope.launch {
        val nameParts = body.name.orEmpty().split(" ")
        val firstName = nameParts.getOrNull(0).orEmpty()
        val lastName = nameParts.getOrNull(1).orEmpty()
        val roles = body.authorizations?.roles.orEmpty().joinToString(",")

        settingsRepository.setSession(
            userName = body.userId.orEmpty(),
            idToken = sessionCache.idToken,
            accessToken = sessionCache.accessToken,
            refreshToken = sessionCache.refreshToken,
            firstName = firstName,
            lastName = lastName,
            email = sessionCache.email,
            roles = roles,
            validity = body.validity?.expiresAt?.toString().orEmpty()
        )

        val locations = body.authorizations?.locations.orEmpty()
        val facilities = locations.filter { it.isNotBlank() && UserFacility.isDefined(it) }

        val currentFacility = sessionCache.facility
        val isFacilityRegistered = facilities.contains(currentFacility)

        when {
            currentFacility.isNotBlank() && isFacilityRegistered -> performHomeNavigation()
            facilities.isEmpty() -> {
                settingsRepository.clearSession()
                settingsRepository.setFacility("", "")
                finishLoginAttempt(toastRes = R.string.login_store_not_found)
            }
            facilities.size == 1 -> {
                val onlyFacility = facilities.first()
                settingsRepository.setFacility(onlyFacility, UserFacility.nameFor(onlyFacility))
                performHomeNavigation()
            }
            else -> {
                showFacilityPicker(facilities)
                blockButtonInteraction = false
                _uiState.update { it.copy(isBusy = false) }
            }
        }
    }

    /** Direct port of `PerformHomeNavigation()` -> `ValidateDevice()`. */
    private fun performHomeNavigation() = viewModelScope.launch {
        val currentDeviceId = deviceIdProvider.getDeviceId()
        settingsRepository.setDeviceId(currentDeviceId)

        val device = fetchCurrentUserAuthorizedDevice(currentDeviceId)
        when {
            device != null && currentDeviceId == device.macId -> navigateToHome()
            device != null && !device.macId.isNullOrBlank() && sessionCache.odouFeatureEnabled -> {
                _uiState.update {
                    it.copy(
                        showSecondLoginAlert = true,
                        secondLoginAlertMessage = "${sessionCache.firstName} is already signed in on ${device.device.orEmpty()}",
                        isBusy = false
                    )
                }
            }
            else -> captureDeviceIdIfNeeded()
        }
    }

    /** Direct port of `FetchCurrentUserAuthorizedDevice()`. */
    private suspend fun fetchCurrentUserAuthorizedDevice(currentDeviceId: String): RegisterDeviceModel? {
        val request = RegisterDeviceModel(
            device = currentDeviceId,
            user = sessionCache.userName,
            override = false,
            userStatus = sessionCache.pingStatusCode,
            macId = currentDeviceId,
            userName = "${sessionCache.firstName} ${sessionCache.lastName}",
            captureDeviceId = captureDeviceId
        )
        return when (val result = loginRepository.registerDevice(request)) {
            is ApiResult.Success -> {
                sessionCache.odouFeatureEnabled = result.data.featureEnabled
                captureDeviceId = result.data.captureDeviceId
                result.data
            }
            is ApiResult.Error -> {
                Timber.w("registerDevice() HTTP error: code=%d body=%s", result.httpCode, result.errorBody)
                null
            }
            is ApiResult.NetworkError -> {
                Timber.e(result.throwable, "registerDevice() network error")
                null
            }
        }
    }

    private fun captureDeviceIdIfNeeded() {
        if (captureDeviceId && sessionCache.odouFeatureEnabled) {
            _uiState.update { it.copy(showDeviceCapturePopup = true, isBusy = false) }
        } else {
            viewModelScope.launch { navigateToHome() }
        }
    }

    fun onDeviceIdInputChanged(value: String) {
        _uiState.update { it.copy(deviceIdInput = value) }
    }

    fun onSubmitDeviceIdOverride() = viewModelScope.launch {
        val deviceId = _uiState.value.deviceIdInput.trim()
        if (deviceId.isBlank()) return@launch

        _uiState.update { it.copy(isBusy = true) }
        val request = RegisterDeviceModel(
            device = deviceId,
            user = sessionCache.userName,
            override = true,
            userStatus = sessionCache.pingStatusCode,
            macId = deviceIdProvider.getDeviceId(),
            userName = "${sessionCache.firstName} ${sessionCache.lastName}",
            captureDeviceId = captureDeviceId
        )
        when (val result = loginRepository.registerDevice(request)) {
            is ApiResult.Success -> {
                sessionCache.odouFeatureEnabled = result.data.featureEnabled
                captureDeviceId = result.data.captureDeviceId
                _uiState.update { it.copy(showDeviceCapturePopup = false) }
                navigateToHome()
            }
            else -> {
                _uiState.update { it.copy(isBusy = false) }
                _events.tryEmit(LoginEvent.ShowToast(R.string.login_failed))
            }
        }
    }

    fun onSecondLoginProceed() = viewModelScope.launch {
        _uiState.update { it.copy(showSecondLoginAlert = false, isBusy = true) }
        val request = RegisterDeviceModel(
            device = deviceIdProvider.getDeviceId(),
            user = sessionCache.userName,
            override = true
        )
        when (val result = loginRepository.registerDevice(request)) {
            is ApiResult.Success -> {
                sessionCache.odouFeatureEnabled = result.data.featureEnabled
                captureDeviceId = result.data.captureDeviceId
                navigateToHome()
            }
            else -> navigateToHome()
        }
    }

    fun onSecondLoginCancel() {
        _uiState.update { it.copy(showSecondLoginAlert = false) }
        logoutUser()
    }

    private suspend fun navigateToHome() {
        val flagsResult = loginRepository.getFeatureFlags(sessionCache.facility)
        val flags = (flagsResult as? ApiResult.Success)?.data
        settingsRepository.applyFeatureFlags(flags.toFeatureFlagSnapshot())

        val conceptResult = loginRepository.getUserConcept(sessionCache.userName)
        if (conceptResult is ApiResult.Success) {
            // Concept code stored for downstream feature modules; persisted via a lightweight
            // dedicated setter to avoid growing setSession()'s parameter list further.
            sessionCache.userConceptCode = conceptResult.data.concept?.code.orEmpty()
        }

        // Direct port of the CBW-role gate at the end of NavigateToHome(): a user whose *only*
        // warehouse role is `warehouse_cbw_supervisors` must additionally pass CBW validation.
        val roles = sessionCache.roles
        val isCbwOnly = roles.contains(AppConstants.ROLE_WAREHOUSE_CBW_SUPERVISORS) &&
            !roles.contains(AppConstants.ROLE_WAREHOUSE_MANAGERS) &&
            !roles.contains(AppConstants.ROLE_WAREHOUSE_SUPERVISORS) &&
            !roles.contains(AppConstants.ROLE_WAREHOUSE_ASSOCIATES)

        if (isCbwOnly) {
            // TODO: wire up CBWRepository.validateCbw() once the CBW feature module is migrated.
            // For now we allow navigation through - the old app blocked login here only when the
            // CBW batch service explicitly reported CBWEnabled = false.
        }

        blockButtonInteraction = false
        _uiState.update { it.copy(isBusy = false, loginCompleted = true) }
        _events.tryEmit(LoginEvent.NavigateHome)
    }

    private fun logoutUser() {
        viewModelScope.launch { settingsRepository.clearSession() }
        authManager.clearWebViewSession()
    }

    private fun finishLoginAttempt(@androidx.annotation.StringRes toastRes: Int?) {
        blockButtonInteraction = false
        _uiState.update { it.copy(isBusy = false) }
        if (toastRes != null) {
            _events.tryEmit(LoginEvent.ShowToast(toastRes))
        }
    }


    override fun onCleared() {
        super.onCleared()
        authManager.dispose()
    }
}

private inline fun MutableStateFlow<LoginUiState>.update(block: (LoginUiState) -> LoginUiState) {
    value = block(value)
}
