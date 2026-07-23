package com.landmarkgroup.sahlawarehouse.feature.login.data.repository

import com.landmarkgroup.sahlawarehouse.core.network.ApiResult
import com.landmarkgroup.sahlawarehouse.core.network.BaseUrlConfig
import com.landmarkgroup.sahlawarehouse.core.network.safeApiCall
import com.landmarkgroup.sahlawarehouse.feature.login.data.api.LoginApi
import com.landmarkgroup.sahlawarehouse.feature.login.data.model.AuthorizationModel
import com.landmarkgroup.sahlawarehouse.feature.login.data.model.FeatureFlagModel
import com.landmarkgroup.sahlawarehouse.feature.login.data.model.RegisterDeviceModel
import com.landmarkgroup.sahlawarehouse.feature.login.data.model.UserConceptResponseModel
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LoginRepository @Inject constructor(
    private val api: LoginApi,
    private val baseUrlConfig: BaseUrlConfig
) {
    /** GET users/me (or /authorizations) - exchanges the ADFS id_token for app authorization data. */
    suspend fun authorize(idToken: String): ApiResult<AuthorizationModel> = safeApiCall {
        val url = "${baseUrlConfig.legacyBaseUrl()}users/me"
        api.authorize(url, idToken)
    }

    /** Lightweight health probe used to drive the login screen's network status indicator. */
    suspend fun ping(): ApiResult<Unit> = safeApiCall {
        val url = "${baseUrlConfig.legacyAppsUrl()}ping"
        api.ping(url)
        Unit
    }

    suspend fun registerDevice(body: RegisterDeviceModel): ApiResult<RegisterDeviceModel> = safeApiCall {
        val url = "${baseUrlConfig.kongBaseUrl()}device/registration"
        api.registerDevice(url, body)
    }

    suspend fun deRegisterDevice(user: String, delinkDevice: Boolean): ApiResult<Unit> = safeApiCall {
        val url = "${baseUrlConfig.kongBaseUrl()}device/registration"
        api.deRegisterDevice(url, user, delinkDevice)
    }

    suspend fun getFeatureFlags(facility: String): ApiResult<List<FeatureFlagModel>> = safeApiCall {
        val url = "${baseUrlConfig.resolveAppsBaseUrl()}feature-flags?facility=$facility"
        api.getFeatureFlags(url)
    }

    suspend fun getUserConcept(userName: String): ApiResult<UserConceptResponseModel> = safeApiCall {
        val url = "${baseUrlConfig.resolveAppsBaseUrl()}users/$userName/concept"
        api.getUserConcept(url)
    }
}
