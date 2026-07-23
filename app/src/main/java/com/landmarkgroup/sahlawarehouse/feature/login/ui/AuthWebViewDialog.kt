package com.landmarkgroup.sahlawarehouse.feature.login.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.landmarkgroup.sahlawarehouse.R

/**
 * Full-screen in-app WebView used to render the ADFS sign-in page
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthWebViewDialog(
    url: String,

    redirectUriPrefix: String,
    onRedirect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        var isLoading by remember { mutableStateOf(true) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.login_button)) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.common_retry))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                AdfsWebView(
                    url = url,
                    redirectUriPrefix = redirectUriPrefix,
                    onRedirect = onRedirect,
                    onLoadingChanged = { isLoading = it },
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .then(Modifier)
                )
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun AdfsWebView(
    url: String,
    redirectUriPrefix: String,
    onRedirect: (String) -> Unit,
    onLoadingChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                // ADFS forms auth commonly redirects through several internal pages -
                // allow the WebView's own cookie jar so the ADFS session/anti-forgery
                // cookies set on the login page survive those redirects.
                android.webkit.CookieManager.getInstance().setAcceptCookie(true)

                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, loadingUrl: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, loadingUrl, favicon)
                        if (loadingUrl != null && loadingUrl.startsWith(redirectUriPrefix, ignoreCase = true)) {
                            onRedirect(loadingUrl)
                        } else {
                            onLoadingChanged(true)
                        }
                    }

                    override fun onPageFinished(view: WebView?, finishedUrl: String?) {
                        super.onPageFinished(view, finishedUrl)
                        onLoadingChanged(false)
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val requestUrl = request?.url?.toString().orEmpty()
                        return if (requestUrl.startsWith(redirectUriPrefix, ignoreCase = true)) {
                            onRedirect(requestUrl)
                            true
                        } else {
                            false
                        }
                    }
                }
                loadUrl(url)
            }
        },
        update = { /* no-op: the WebView owns its own navigation once created */ }
    )
}
