package com.landmarkgroup.sahlawarehouse.core.auth

import android.content.Context
import android.net.Uri
import android.webkit.CookieManager
import com.landmarkgroup.sahlawarehouse.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenRequest
import net.openid.appauth.TokenResponse
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


@Singleton
class AuthManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val authorizationService: AuthorizationService by lazy {
        AuthorizationService(context)
    }

    /** The in-flight request - retained so [exchangeToken] can reuse its PKCE code verifier. */
    private var pendingRequest: AuthorizationRequest? = null

    val redirectUri: String get() = BuildConfig.ADFS_REDIRECT_URI


    private fun serviceConfiguration(): AuthorizationServiceConfiguration {
        val authority = BuildConfig.ADFS_AUTHORITY.trimEnd('/')
        return AuthorizationServiceConfiguration(
            Uri.parse("$authority/oauth2/authorize"),
            Uri.parse("$authority/oauth2/token"),
            null,
            Uri.parse("$authority/oauth2/logout")
        )
    }

    fun buildAuthorizationUrl(): Uri {
        val request = AuthorizationRequest.Builder(
            serviceConfiguration(),
            BuildConfig.ADFS_CLIENT_ID,
            ResponseTypeValues.CODE,
            Uri.parse(BuildConfig.ADFS_REDIRECT_URI)
        )
            .setAdditionalParameters(mapOf("resource" to BuildConfig.ADFS_RESOURCE))
            .setState(UUID.randomUUID().toString())
            .build()
        pendingRequest = request
        return request.toUri()
    }


    fun buildLogoutUrl(): Uri =
        serviceConfiguration().endSessionEndpoint
            ?: Uri.parse("${BuildConfig.ADFS_AUTHORITY.trimEnd('/')}/oauth2/logout")


    fun clearWebViewSession() {
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies(null)
        cookieManager.flush()
    }

    fun parseRedirectUrl(url: String): Result<String> {
        val uri = Uri.parse(url)
        val error = uri.getQueryParameter("error")
        if (error != null) {
            val description = uri.getQueryParameter("error_description") ?: error
            return Result.failure(IllegalStateException(description))
        }
        val code = uri.getQueryParameter("code")
            ?: return Result.failure(IllegalStateException("Authorization redirect missing 'code' parameter"))
        return Result.success(code)
    }

    suspend fun exchangeToken(code: String): TokenResponse {
        val authRequest = pendingRequest
            ?: throw IllegalStateException("exchangeToken() called with no pending authorization request")

        val tokenRequest = TokenRequest.Builder(serviceConfiguration(), BuildConfig.ADFS_CLIENT_ID)
            .setAuthorizationCode(code)
            .setRedirectUri(Uri.parse(BuildConfig.ADFS_REDIRECT_URI))
            .setCodeVerifier(authRequest.codeVerifier)
            .setNonce(authRequest.nonce)
            .setAdditionalParameters(mapOf("resource" to BuildConfig.ADFS_RESOURCE))
            .build()


        return suspendCancellableCoroutine { continuation ->
            authorizationService.performTokenRequest(tokenRequest) { tokenResponse, exception ->
                when {
                    tokenResponse != null -> continuation.resume(tokenResponse)
                    exception != null -> continuation.resumeWithException(exception)
                    else -> continuation.resumeWithException(
                        IllegalStateException("Token exchange returned neither response nor exception")
                    )
                }
            }
        }
    }

    fun buildAuthState(tokenResponse: TokenResponse): AuthState =
        AuthState().apply { update(tokenResponse, null) }

    fun dispose() {
        pendingRequest = null
        authorizationService.dispose()
    }
}
