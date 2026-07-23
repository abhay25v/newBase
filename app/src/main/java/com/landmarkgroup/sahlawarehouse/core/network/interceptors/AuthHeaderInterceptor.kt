package com.landmarkgroup.sahlawarehouse.core.network.interceptors

import com.landmarkgroup.sahlawarehouse.BuildConfig
import com.landmarkgroup.sahlawarehouse.core.data.settings.SessionCache
import com.landmarkgroup.sahlawarehouse.core.network.ApiHeaders
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthHeaderInterceptor @Inject constructor(
    private val sessionCache: SessionCache
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val builder = original.newBuilder()
            .header(ApiHeaders.CLIENT_ID_HEADER, BuildConfig.IBM_CLIENT_ID)
            .header(ApiHeaders.LANDMARK_CLIENT_ID_HEADER, BuildConfig.LANDMARK_CLIENT_ID)

        if (sessionCache.userName.isNotBlank()) {
            builder.header(ApiHeaders.USER_ID_HEADER, sessionCache.userName)
            builder.header(ApiHeaders.USER_HEADER, sessionCache.userName)
        }
        if (sessionCache.stationId.isNotBlank()) {
            builder.header(ApiHeaders.STATION_ID_HEADER, sessionCache.stationId)
        }
        if (sessionCache.facility.isNotBlank()) {
            builder.header(ApiHeaders.USER_FACILITY_ID_HEADER, sessionCache.facility)
            builder.header(ApiHeaders.FACILITY_HEADER, sessionCache.facility)
            builder.header(ApiHeaders.LOCATION_HEADER, sessionCache.facility)
        }
        if (sessionCache.deviceId.isNotBlank()) {
            builder.header(ApiHeaders.DEVICE_ID_HEADER, sessionCache.deviceId)
        }
        if (sessionCache.idToken.isNotBlank()) {
            builder.header("Authorization", "Bearer ${sessionCache.idToken}")
        }

        return chain.proceed(builder.build())
    }
}
