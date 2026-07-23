package com.landmarkgroup.sahlawarehouse.core.network

import com.landmarkgroup.sahlawarehouse.BuildConfig
import com.landmarkgroup.sahlawarehouse.core.data.settings.SessionCache
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaseUrlConfig @Inject constructor(
    private val sessionCache: SessionCache
) {

    /** Legacy `v2` REST base url (used for Authorize / Ping). */
    fun legacyBaseUrl(): String =
        if (sessionCache.isProdEnabled) PROD_BASE_URL_DNS else BuildConfig.LEGACY_BASE_URL

    /** Legacy base url without the trailing `v2/` segment - used when Kong/Nginx are disabled. */
    fun legacyAppsUrl(): String = legacyBaseUrl().replace("v2/", "")

    fun nginxBaseUrl(): String =
        if (sessionCache.isProdEnabled) PROD_BASE_NGINX_URL_DNS else BuildConfig.NGINX_BASE_URL

    fun kongBaseUrl(): String =
        if (sessionCache.isProdEnabled) PROD_BASE_KONG_URL_DNS else BuildConfig.KONG_BASE_URL

    fun resolveAppsBaseUrl(): String = when {
        sessionCache.isKongUrlEnabled -> kongBaseUrl()
        sessionCache.isNginxUrlEnabled -> nginxBaseUrl()
        else -> legacyAppsUrl()
    }

    companion object {
        // Hardcoded production endpoints (production never
        // changes per-build so these remain constants rather than BuildConfig fields).
        const val PROD_BASE_URL_DNS = "https://api.landmarkgroup.com/v2/"
        const val PROD_BASE_NGINX_URL_DNS = "https://Api1.landmarkgroup.com/warehouse-ops/"
        const val PROD_BASE_KONG_URL_DNS = "https://api3.landmarkgroup.com/"
    }
}
