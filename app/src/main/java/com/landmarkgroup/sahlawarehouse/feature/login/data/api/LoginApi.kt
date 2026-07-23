package com.landmarkgroup.sahlawarehouse.feature.login.data.api

import com.landmarkgroup.sahlawarehouse.feature.login.data.model.AuthorizationModel
import com.landmarkgroup.sahlawarehouse.feature.login.data.model.FeatureFlagModel
import com.landmarkgroup.sahlawarehouse.feature.login.data.model.RegisterDeviceModel
import com.landmarkgroup.sahlawarehouse.feature.login.data.model.UserConceptResponseModel
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url


interface LoginApi {

    /** GET {legacyBase}/users/me or /users/authorizations (feature-flag controlled). */
    @GET
    suspend fun authorize(
        @Url url: String,
        @Header("id_token") idToken: String
    ): AuthorizationModel

    /** GET {base}/ping - lightweight connectivity/health probe. */
    @GET
    suspend fun ping(@Url url: String): retrofit2.Response<Unit>

    /** POST {kongBase}/device/registration */
    @POST
    suspend fun registerDevice(
        @Url url: String,
        @Body body: RegisterDeviceModel
    ): RegisterDeviceModel

    /** DELETE {kongBase}/device/registration?user=..&delinkdevice=.. */
    @DELETE
    suspend fun deRegisterDevice(
        @Url url: String,
        @Query("user") user: String,
        @Query("delinkdevice") delinkDevice: Boolean
    )

    /** GET {base}/feature-flags?facility=.. */
    @GET
    suspend fun getFeatureFlags(@Url url: String): List<FeatureFlagModel>

    /** GET {base}/users/{userName}/concept */
    @GET
    suspend fun getUserConcept(@Url url: String): UserConceptResponseModel
}
