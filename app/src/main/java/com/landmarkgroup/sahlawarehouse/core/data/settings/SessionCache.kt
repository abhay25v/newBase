package com.landmarkgroup.sahlawarehouse.core.data.settings

import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory, thread-safe mirror of the persisted DataStore settings.
 */
@Singleton
class SessionCache @Inject constructor() {

    // ----- Auth / session -----
    @Volatile var userName: String = ""
    @Volatile var idToken: String = ""
    @Volatile var accessToken: String = ""
    @Volatile var refreshToken: String = ""
    @Volatile var firstName: String = ""
    @Volatile var lastName: String = ""
    @Volatile var email: String = ""
    @Volatile var roles: String = ""
    @Volatile var userConceptCode: String = ""
    @Volatile var validity: String = ""
    @Volatile var lastLoggedIn: String = ""

    // ----- Facility / device -----
    @Volatile var facility: String = ""
    @Volatile var facilityName: String = ""
    @Volatile var stationId: String = ""
    @Volatile var deviceId: String = ""

    // ----- Locale -----
    @Volatile var isArabicOn: Boolean = false
    @Volatile var selectedLanguageId: String = "1"

    // ----- Environment / gateway routing (mirrors BaseUrlConfig switches) -----
    @Volatile var isProdEnabled: Boolean = false
    @Volatile var isKongUrlEnabled: Boolean = false
    @Volatile var isNginxUrlEnabled: Boolean = false
    @Volatile var pingStatusCode: Int = 404

    // ----- Printer -----
    @Volatile var printerIp: String = ""
    @Volatile var printerBtAddress: String = ""

    // ----- Feature flags (fetched from /feature-flags after login, see LoginViewModel) -----
    @Volatile var isEcomFacility: Boolean = false
    @Volatile var isWaveCreationEnabled: Boolean = false
    @Volatile var featureAuthorizeApiUrlBase: Boolean = false
    @Volatile var isCasePickingEnabled: Boolean = false
    @Volatile var isMultiPickingEnabled: Boolean = false
    @Volatile var isDollyBuildingAllowed: Boolean = false
    @Volatile var isSingleWallCloseEnabled: Boolean = false
    @Volatile var presortLocationEnabled: Boolean = false
    @Volatile var isPtwOptimisationEnabled: Boolean = false
    @Volatile var isCrbwCrossConcepts: Boolean = false
    @Volatile var isCrbwEnabledInHc: Boolean = false
    @Volatile var isModonEnabled: Boolean = false
    @Volatile var isBulkSortEnabled: Boolean = false
    @Volatile var isCrbwVsiEnabled: Boolean = false
    @Volatile var odouFeatureEnabled: Boolean = false
    @Volatile var imageServerBaseUrlFlag: Boolean = false

    // ----- Diagnostics / logging flags -----
    @Volatile var isScanSoundEnabled: Boolean = true
    @Volatile var isLoggerEnabled: Boolean = false
    @Volatile var isApiTrackingEnabled: Boolean = false
    @Volatile var isEventTrackingEnabled: Boolean = false
    @Volatile var isErrorTrackingEnabled: Boolean = false
    @Volatile var isLogsTrackingEnabled: Boolean = false


    val isAuthenticated: Boolean
        get() = idToken.isNotBlank() && userName.isNotBlank()

    /** Clears all in-memory session state on logout. Persisted values are cleared separately. */
    fun clearSession() {
        userName = ""
        idToken = ""
        accessToken = ""
        refreshToken = ""
        firstName = ""
        lastName = ""
        email = ""
        roles = ""
        userConceptCode = ""
        validity = ""
        deviceId = deviceId
    }
}
