package com.landmarkgroup.sahlawarehouse.core.data.settings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * DataStore Preference key definitions. 1:1 mapping to the constants declared in the old
 * `SahlaWH.UtilityManager.Settings` static class, so behaviour/persisted-key-names stay familiar
 * during the migration (helps side-by-side QA comparisons).
 */
object SettingsKeys {
    val USER_NAME = stringPreferencesKey("UserName")
    val ID_TOKEN = stringPreferencesKey("id_token")
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    val FIRST_NAME = stringPreferencesKey("FirstName")
    val LAST_NAME = stringPreferencesKey("LastName")
    val EMAIL = stringPreferencesKey("Email")
    val ROLES = stringPreferencesKey("RoleName")
    val USER_CONCEPT_CODE = stringPreferencesKey("UserConceptCode")
    val VALIDITY = stringPreferencesKey("Validity")
    val LAST_LOGGED_IN = stringPreferencesKey("LastLoggedInDateTime")

    val FACILITY = stringPreferencesKey("Facility")
    val FACILITY_NAME = stringPreferencesKey("FacilityName")
    val STATION_ID = stringPreferencesKey("StationId")
    val DEVICE_ID = stringPreferencesKey("DeviceId")

    val IS_ARABIC_ON = booleanPreferencesKey("isArabicOn")
    val SELECTED_LANGUAGE_ID = stringPreferencesKey("SelectedLanguageID")

    val IS_PROD_ENABLED = booleanPreferencesKey("IsProductionEnabled")
    val IS_KONG_URL_ENABLED = booleanPreferencesKey("IsKongURLEnabled")
    val IS_NGINX_URL_ENABLED = booleanPreferencesKey("NewServerNginxURLEnabled")
    val PING_STATUS_CODE = intPreferencesKey("PingErrorKey")

    val PRINTER_IP = stringPreferencesKey("PrinterIP")
    val PRINTER_BT_ADDRESS = stringPreferencesKey("PrinterBtAddress")

    val IS_ECOM_FACILITY = booleanPreferencesKey("IsEcomFacility")
    val IS_WAVE_CREATION_ENABLED = booleanPreferencesKey("IsWaveCreationEnabled")
    val FEATURE_AUTHORIZE_API_URL_BASE = booleanPreferencesKey("FeatureAuthorizeApiUrlBase")
    val IS_CASE_PICKING_ENABLED = booleanPreferencesKey("IsCasePickingEnabled")
    val IS_MULTI_PICKING_ENABLED = booleanPreferencesKey("IsMultiPickingEnabled")
    val IS_DOLLY_BUILDING_ALLOWED = booleanPreferencesKey("IsMovableWall")
    val IS_SINGLE_WALL_CLOSE_ENABLED = booleanPreferencesKey("IsSingleWallCloseEnabled")
    val PRESORT_LOCATION_ENABLED = booleanPreferencesKey("PresortLocationEnabled")
    val IS_PTW_OPTIMISATION_ENABLED = booleanPreferencesKey("IsPTWOptimisationEnabled")
    val IS_CRBW_CROSS_CONCEPTS = booleanPreferencesKey("IsCRBWCrossConcepts")
    val IS_CRBW_ENABLED_IN_HC = booleanPreferencesKey("IsCRBWENABLEInHC")
    val IS_MODON_ENABLED = booleanPreferencesKey("IsModonEnabled")
    val IS_BULK_SORT_ENABLED = booleanPreferencesKey("IsBulkSortEnabled")
    val IS_CRBW_VSI_ENABLED = booleanPreferencesKey("IsCRBWVSIEnabled")
    val ODOU_FEATURE_ENABLED = booleanPreferencesKey("ODOUFeatureEnabled")
    val IMAGE_SERVER_BASE_URL_FLAG = booleanPreferencesKey("ImageServerBaseUrlFlag")

    val IS_SCAN_SOUND_ENABLED = booleanPreferencesKey("IsScanSoundEnabled")
    val IS_LOGGER_ENABLED = booleanPreferencesKey("IsLoggerEnabled")
    val IS_API_TRACKING_ENABLED = booleanPreferencesKey("IsAPITrackingEnabled")
    val IS_EVENT_TRACKING_ENABLED = booleanPreferencesKey("IsEventTrackingEnabled")
    val IS_ERROR_TRACKING_ENABLED = booleanPreferencesKey("IsErrorTrackingEnabledKey")
    val IS_LOGS_TRACKING_ENABLED = booleanPreferencesKey("IsLogsTrackingEnabled")
}
