package com.landmarkgroup.sahlawarehouse.core.data.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for persisted app settings.
 *
 * Replaces `SahlaWH.UtilityManager.Settings`. Every property is exposed both as:
 *  - a suspend getter/setter pair that reads/writes DataStore (durable across process death), and
 *  - kept in sync with [SessionCache] so synchronous call-sites (interceptors, header builders)
 *    behave like the old static `Settings` class.
 */
@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessionCache: SessionCache
) {
    private val dataStore get() = context.settingsDataStore

    private val data: Flow<Preferences> = dataStore.data.catch { e ->
        if (e is IOException) emit(emptyPreferences()) else throw e
    }

    /** Called once from [com.landmarkgroup.sahlawarehouse.SahlaApplication.onCreate]. */
    suspend fun hydrateSessionCache() {
        val prefs = data.first()
        with(sessionCache) {
            userName = prefs[SettingsKeys.USER_NAME] ?: ""
            idToken = prefs[SettingsKeys.ID_TOKEN] ?: ""
            accessToken = prefs[SettingsKeys.ACCESS_TOKEN] ?: ""
            refreshToken = prefs[SettingsKeys.REFRESH_TOKEN] ?: ""
            firstName = prefs[SettingsKeys.FIRST_NAME] ?: ""
            lastName = prefs[SettingsKeys.LAST_NAME] ?: ""
            email = prefs[SettingsKeys.EMAIL] ?: ""
            roles = prefs[SettingsKeys.ROLES] ?: ""
            userConceptCode = prefs[SettingsKeys.USER_CONCEPT_CODE] ?: ""
            validity = prefs[SettingsKeys.VALIDITY] ?: ""
            lastLoggedIn = prefs[SettingsKeys.LAST_LOGGED_IN] ?: ""

            facility = prefs[SettingsKeys.FACILITY] ?: ""
            facilityName = prefs[SettingsKeys.FACILITY_NAME] ?: ""
            stationId = prefs[SettingsKeys.STATION_ID] ?: ""
            deviceId = prefs[SettingsKeys.DEVICE_ID] ?: ""

            isArabicOn = prefs[SettingsKeys.IS_ARABIC_ON] ?: false
            selectedLanguageId = prefs[SettingsKeys.SELECTED_LANGUAGE_ID] ?: "1"

            isProdEnabled = prefs[SettingsKeys.IS_PROD_ENABLED] ?: false
            isKongUrlEnabled = prefs[SettingsKeys.IS_KONG_URL_ENABLED] ?: false
            isNginxUrlEnabled = prefs[SettingsKeys.IS_NGINX_URL_ENABLED] ?: false
            pingStatusCode = prefs[SettingsKeys.PING_STATUS_CODE] ?: 404

            printerIp = prefs[SettingsKeys.PRINTER_IP] ?: ""
            printerBtAddress = prefs[SettingsKeys.PRINTER_BT_ADDRESS] ?: ""

            isEcomFacility = prefs[SettingsKeys.IS_ECOM_FACILITY] ?: false
            isWaveCreationEnabled = prefs[SettingsKeys.IS_WAVE_CREATION_ENABLED] ?: false
            featureAuthorizeApiUrlBase = prefs[SettingsKeys.FEATURE_AUTHORIZE_API_URL_BASE] ?: false
            isCasePickingEnabled = prefs[SettingsKeys.IS_CASE_PICKING_ENABLED] ?: false
            isMultiPickingEnabled = prefs[SettingsKeys.IS_MULTI_PICKING_ENABLED] ?: false
            isDollyBuildingAllowed = prefs[SettingsKeys.IS_DOLLY_BUILDING_ALLOWED] ?: false
            isSingleWallCloseEnabled = prefs[SettingsKeys.IS_SINGLE_WALL_CLOSE_ENABLED] ?: false
            presortLocationEnabled = prefs[SettingsKeys.PRESORT_LOCATION_ENABLED] ?: false
            isPtwOptimisationEnabled = prefs[SettingsKeys.IS_PTW_OPTIMISATION_ENABLED] ?: false
            isCrbwCrossConcepts = prefs[SettingsKeys.IS_CRBW_CROSS_CONCEPTS] ?: false
            isCrbwEnabledInHc = prefs[SettingsKeys.IS_CRBW_ENABLED_IN_HC] ?: false
            isModonEnabled = prefs[SettingsKeys.IS_MODON_ENABLED] ?: false
            isBulkSortEnabled = prefs[SettingsKeys.IS_BULK_SORT_ENABLED] ?: false
            isCrbwVsiEnabled = prefs[SettingsKeys.IS_CRBW_VSI_ENABLED] ?: false
            odouFeatureEnabled = prefs[SettingsKeys.ODOU_FEATURE_ENABLED] ?: false
            imageServerBaseUrlFlag = prefs[SettingsKeys.IMAGE_SERVER_BASE_URL_FLAG] ?: false

            isScanSoundEnabled = prefs[SettingsKeys.IS_SCAN_SOUND_ENABLED] ?: true
            isLoggerEnabled = prefs[SettingsKeys.IS_LOGGER_ENABLED] ?: false
            isApiTrackingEnabled = prefs[SettingsKeys.IS_API_TRACKING_ENABLED] ?: false
            isEventTrackingEnabled = prefs[SettingsKeys.IS_EVENT_TRACKING_ENABLED] ?: false
            isErrorTrackingEnabled = prefs[SettingsKeys.IS_ERROR_TRACKING_ENABLED] ?: false
            isLogsTrackingEnabled = prefs[SettingsKeys.IS_LOGS_TRACKING_ENABLED] ?: false
        }
    }

    // ---- Observable flows for UI (Compose collectAsStateWithLifecycle) ----
    val isArabicOnFlow: Flow<Boolean> = data.map { it[SettingsKeys.IS_ARABIC_ON] ?: false }
    val selectedLanguageIdFlow: Flow<String> = data.map { it[SettingsKeys.SELECTED_LANGUAGE_ID] ?: "1" }
    val isProdEnabledFlow: Flow<Boolean> = data.map { it[SettingsKeys.IS_PROD_ENABLED] ?: false }

    // ---- Auth session ----
    suspend fun setSession(
        userName: String,
        idToken: String,
        accessToken: String,
        refreshToken: String,
        firstName: String,
        lastName: String,
        email: String,
        roles: String,
        validity: String
    ) {
        dataStore.edit { prefs ->
            prefs[SettingsKeys.USER_NAME] = userName
            prefs[SettingsKeys.ID_TOKEN] = idToken
            prefs[SettingsKeys.ACCESS_TOKEN] = accessToken
            prefs[SettingsKeys.REFRESH_TOKEN] = refreshToken
            prefs[SettingsKeys.FIRST_NAME] = firstName
            prefs[SettingsKeys.LAST_NAME] = lastName
            prefs[SettingsKeys.EMAIL] = email
            prefs[SettingsKeys.ROLES] = roles
            prefs[SettingsKeys.VALIDITY] = validity
        }
        with(sessionCache) {
            this.userName = userName
            this.idToken = idToken
            this.accessToken = accessToken
            this.refreshToken = refreshToken
            this.firstName = firstName
            this.lastName = lastName
            this.email = email
            this.roles = roles
            this.validity = validity
        }
    }

    suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.remove(SettingsKeys.USER_NAME)
            prefs.remove(SettingsKeys.ID_TOKEN)
            prefs.remove(SettingsKeys.ACCESS_TOKEN)
            prefs.remove(SettingsKeys.REFRESH_TOKEN)
            prefs.remove(SettingsKeys.FIRST_NAME)
            prefs.remove(SettingsKeys.LAST_NAME)
            prefs.remove(SettingsKeys.EMAIL)
            prefs.remove(SettingsKeys.ROLES)
            prefs.remove(SettingsKeys.VALIDITY)
        }
        sessionCache.clearSession()
    }

    suspend fun setFacility(facilityId: String, facilityName: String) {
        dataStore.edit { prefs ->
            prefs[SettingsKeys.FACILITY] = facilityId
            prefs[SettingsKeys.FACILITY_NAME] = facilityName
        }
        sessionCache.facility = facilityId
        sessionCache.facilityName = facilityName
    }

    suspend fun setStationId(stationId: String) {
        dataStore.edit { it[SettingsKeys.STATION_ID] = stationId }
        sessionCache.stationId = stationId
    }

    suspend fun setDeviceId(deviceId: String) {
        dataStore.edit { it[SettingsKeys.DEVICE_ID] = deviceId }
        sessionCache.deviceId = deviceId
    }

    suspend fun setArabicOn(enabled: Boolean) {
        dataStore.edit { it[SettingsKeys.IS_ARABIC_ON] = enabled }
        sessionCache.isArabicOn = enabled
    }

    suspend fun setSelectedLanguageId(languageId: String) {
        dataStore.edit { it[SettingsKeys.SELECTED_LANGUAGE_ID] = languageId }
        sessionCache.selectedLanguageId = languageId
    }

    suspend fun setProdEnabled(enabled: Boolean) {
        dataStore.edit { it[SettingsKeys.IS_PROD_ENABLED] = enabled }
        sessionCache.isProdEnabled = enabled
    }

    suspend fun setPingStatusCode(code: Int) {
        dataStore.edit { it[SettingsKeys.PING_STATUS_CODE] = code }
        sessionCache.pingStatusCode = code
    }

    suspend fun setPrinterDetails(ip: String, btAddress: String) {
        dataStore.edit { prefs ->
            prefs[SettingsKeys.PRINTER_IP] = ip
            prefs[SettingsKeys.PRINTER_BT_ADDRESS] = btAddress
        }
        sessionCache.printerIp = ip
        sessionCache.printerBtAddress = btAddress
    }

    /**
     * Persists the full feature-flag set fetched right after login (mirrors
     * `LoginViewModel.NavigateToHome()` in the old app, where every flag from the
     * `/feature-flags` response is mapped onto a `Settings` property).
     */
    suspend fun applyFeatureFlags(flags: FeatureFlagSnapshot) {
        dataStore.edit { prefs ->
            prefs[SettingsKeys.IMAGE_SERVER_BASE_URL_FLAG] = flags.imageServerBaseUrlFlag
            prefs[SettingsKeys.IS_LOGGER_ENABLED] = flags.isLoggerEnabled
            prefs[SettingsKeys.IS_API_TRACKING_ENABLED] = flags.isApiTrackingEnabled
            prefs[SettingsKeys.IS_EVENT_TRACKING_ENABLED] = flags.isEventTrackingEnabled
            prefs[SettingsKeys.IS_LOGS_TRACKING_ENABLED] = flags.isLogsTrackingEnabled
            prefs[SettingsKeys.IS_ERROR_TRACKING_ENABLED] = flags.isErrorTrackingEnabled
            prefs[SettingsKeys.IS_SINGLE_WALL_CLOSE_ENABLED] = flags.isSingleWallCloseEnabled
            prefs[SettingsKeys.IS_NGINX_URL_ENABLED] = flags.isNginxUrlEnabled
            prefs[SettingsKeys.PRESORT_LOCATION_ENABLED] = flags.presortLocationEnabled
            prefs[SettingsKeys.IS_DOLLY_BUILDING_ALLOWED] = flags.isDollyBuildingAllowed
            prefs[SettingsKeys.IS_ECOM_FACILITY] = flags.isEcomFacility
            prefs[SettingsKeys.IS_WAVE_CREATION_ENABLED] = flags.isWaveCreationEnabled
            prefs[SettingsKeys.FEATURE_AUTHORIZE_API_URL_BASE] = flags.featureAuthorizeApiUrlBase
            prefs[SettingsKeys.IS_CRBW_VSI_ENABLED] = flags.isCrbwVsiEnabled
            prefs[SettingsKeys.IS_CASE_PICKING_ENABLED] = flags.isCasePickingEnabled
            prefs[SettingsKeys.IS_MULTI_PICKING_ENABLED] = flags.isMultiPickingEnabled
            prefs[SettingsKeys.IS_KONG_URL_ENABLED] = flags.isKongUrlEnabled
            prefs[SettingsKeys.IS_PTW_OPTIMISATION_ENABLED] = flags.isPtwOptimisationEnabled
            prefs[SettingsKeys.IS_CRBW_CROSS_CONCEPTS] = flags.isCrbwCrossConcepts
            prefs[SettingsKeys.IS_BULK_SORT_ENABLED] = flags.isBulkSortEnabled
            prefs[SettingsKeys.IS_CRBW_ENABLED_IN_HC] = flags.isCrbwEnabledInHc
            prefs[SettingsKeys.IS_MODON_ENABLED] = flags.isModonEnabled
        }
        with(sessionCache) {
            imageServerBaseUrlFlag = flags.imageServerBaseUrlFlag
            isLoggerEnabled = flags.isLoggerEnabled
            isApiTrackingEnabled = flags.isApiTrackingEnabled
            isEventTrackingEnabled = flags.isEventTrackingEnabled
            isLogsTrackingEnabled = flags.isLogsTrackingEnabled
            isErrorTrackingEnabled = flags.isErrorTrackingEnabled
            isSingleWallCloseEnabled = flags.isSingleWallCloseEnabled
            isNginxUrlEnabled = flags.isNginxUrlEnabled
            presortLocationEnabled = flags.presortLocationEnabled
            isDollyBuildingAllowed = flags.isDollyBuildingAllowed
            isEcomFacility = flags.isEcomFacility
            isWaveCreationEnabled = flags.isWaveCreationEnabled
            featureAuthorizeApiUrlBase = flags.featureAuthorizeApiUrlBase
            isCrbwVsiEnabled = flags.isCrbwVsiEnabled
            isCasePickingEnabled = flags.isCasePickingEnabled
            isMultiPickingEnabled = flags.isMultiPickingEnabled
            isKongUrlEnabled = flags.isKongUrlEnabled
            isPtwOptimisationEnabled = flags.isPtwOptimisationEnabled
            isCrbwCrossConcepts = flags.isCrbwCrossConcepts
            isBulkSortEnabled = flags.isBulkSortEnabled
            isCrbwEnabledInHc = flags.isCrbwEnabledInHc
            isModonEnabled = flags.isModonEnabled
        }
    }
}

/** Plain snapshot of every feature flag consumed at login time. See [SettingsRepository.applyFeatureFlags]. */
data class FeatureFlagSnapshot(
    val imageServerBaseUrlFlag: Boolean = false,
    val isLoggerEnabled: Boolean = false,
    val isApiTrackingEnabled: Boolean = false,
    val isEventTrackingEnabled: Boolean = false,
    val isLogsTrackingEnabled: Boolean = false,
    val isErrorTrackingEnabled: Boolean = false,
    val isSingleWallCloseEnabled: Boolean = false,
    val isNginxUrlEnabled: Boolean = false,
    val presortLocationEnabled: Boolean = false,
    val isDollyBuildingAllowed: Boolean = false,
    val isEcomFacility: Boolean = false,
    val isWaveCreationEnabled: Boolean = false,
    val featureAuthorizeApiUrlBase: Boolean = false,
    val isCrbwVsiEnabled: Boolean = false,
    val isCasePickingEnabled: Boolean = false,
    val isMultiPickingEnabled: Boolean = false,
    val isKongUrlEnabled: Boolean = false,
    val isPtwOptimisationEnabled: Boolean = false,
    val isCrbwCrossConcepts: Boolean = false,
    val isBulkSortEnabled: Boolean = false,
    val isCrbwEnabledInHc: Boolean = false,
    val isModonEnabled: Boolean = false
)
