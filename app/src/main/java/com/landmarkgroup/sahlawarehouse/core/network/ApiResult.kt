package com.landmarkgroup.sahlawarehouse.core.network

import kotlinx.serialization.json.Json
import okhttp3.ResponseBody
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(
        val httpCode: Int,
        val errorBody: ServiceExceptionResponse?,
        val rawMessage: String? = null
    ) : ApiResult<Nothing>()
    data class NetworkError(val throwable: Throwable) : ApiResult<Nothing>()
}

@kotlinx.serialization.Serializable
data class ServiceExceptionResponse(
    val httpCode: Int? = null,
    val httpMessage: String? = null,
    val moreInformation: MoreInformation? = null
)

@kotlinx.serialization.Serializable
data class MoreInformation(
    val errCode: String? = null,
    val errType: String? = null,
    val message: String? = null
)

private val errorJson = Json { ignoreUnknownKeys = true; isLenient = true }

suspend fun <T> safeApiCall(call: suspend () -> T): ApiResult<T> {
    return try {
        ApiResult.Success(call())
    } catch (httpException: HttpException) {
        val errorBody = parseErrorBody(httpException.response()?.errorBody())
        Timber.w(httpException, "API HTTP error: %d", httpException.code())
        ApiResult.Error(
            httpCode = httpException.code(),
            errorBody = errorBody,
            rawMessage = httpException.message()
        )
    } catch (io: IOException) {
        Timber.w(io, "API network error")
        ApiResult.NetworkError(io)
    } catch (t: Throwable) {
        Timber.e(t, "Unexpected API error")
        ApiResult.NetworkError(t)
    }
}

private fun parseErrorBody(body: ResponseBody?): ServiceExceptionResponse? {
    val raw = body?.string() ?: return null
    return try {
        errorJson.decodeFromString(ServiceExceptionResponse.serializer(), raw)
    } catch (e: Exception) {
        try {
            errorJson.decodeFromString(MoreInformation.serializer(), raw).let {
                ServiceExceptionResponse(moreInformation = it)
            }
        } catch (e2: Exception) {
            null
        }
    }
}
