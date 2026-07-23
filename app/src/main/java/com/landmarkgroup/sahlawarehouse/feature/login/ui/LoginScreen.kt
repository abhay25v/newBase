package com.landmarkgroup.sahlawarehouse.feature.login.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.landmarkgroup.sahlawarehouse.R
import com.landmarkgroup.sahlawarehouse.core.common.FacilityModel
import com.landmarkgroup.sahlawarehouse.core.ui.util.currentDeviceType
import com.landmarkgroup.sahlawarehouse.core.ui.util.DeviceType
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Detect device type for responsive layout selection
    val deviceType = currentDeviceType()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is LoginEvent.NavigateHome -> onLoginSuccess()
                is LoginEvent.ShowToast -> {
                    val message = context.getString(event.messageRes)
                    Timber.w("Login failed, showing message to user: %s", message)
                    coroutineScope.launch { snackbarHostState.showSnackbar(message) }
                }
            }
        }
    }

    // Select layout based on device type - routes to phone or tablet layout
    if (deviceType == DeviceType.TABLET) {
        LoginScreenTablet(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onLoginClicked = viewModel::onLoginWithAdClicked,
            onChangeLanguageClicked = viewModel::onChangeLanguageClicked,
            onLanguageSelected = viewModel::onLanguageSelected,
            onFacilitySelected = viewModel::onFacilitySelected,
            onDeviceIdChanged = viewModel::onDeviceIdInputChanged,
            onDeviceIdSubmit = viewModel::onSubmitDeviceIdOverride,
            onSecondLoginProceed = viewModel::onSecondLoginProceed,
            onSecondLoginCancel = viewModel::onSecondLoginCancel,
            onRetryPing = viewModel::refreshPing
        )
    } else {
        LoginContent(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onLoginClicked = viewModel::onLoginWithAdClicked,
            onChangeLanguageClicked = viewModel::onChangeLanguageClicked,
            onLanguageSelected = viewModel::onLanguageSelected,
            onFacilitySelected = viewModel::onFacilitySelected,
            onDeviceIdChanged = viewModel::onDeviceIdInputChanged,
            onDeviceIdSubmit = viewModel::onSubmitDeviceIdOverride,
            onSecondLoginProceed = viewModel::onSecondLoginProceed,
            onSecondLoginCancel = viewModel::onSecondLoginCancel,
            onRetryPing = viewModel::refreshPing
        )
    }

    // In-app ADFS sign-in WebView - replaces the previous Custom-Tab/system-browser launch so
    // the user authenticates without ever leaving the app.
    uiState.authWebViewUrl?.let { authUrl ->
        AuthWebViewDialog(
            url = authUrl,
            redirectUriPrefix = viewModel.redirectUri,
            onRedirect = viewModel::onAuthRedirectIntercepted,
            onDismiss = viewModel::onAuthWebViewDismissed
        )
    }
}



@Composable
private fun LoginContent(
    uiState: LoginUiState,
    snackbarHostState: SnackbarHostState,
    onLoginClicked: () -> Unit,
    onChangeLanguageClicked: () -> Unit,
    onLanguageSelected: (FacilityModel) -> Unit,
    onFacilitySelected: (FacilityModel) -> Unit,
    onDeviceIdChanged: (String) -> Unit,
    onDeviceIdSubmit: () -> Unit,
    onSecondLoginProceed: () -> Unit,
    onSecondLoginCancel: () -> Unit,
    onRetryPing: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(48.dp))

                NetworkStatusIndicator(status = uiState.networkStatus, onRetry = onRetryPing)

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = onLoginClicked,
                    enabled = uiState.loginButtonEnabled && !uiState.isBusy,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),


                    ) {
                    if (uiState.isBusy) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(R.string.login_button))
                    }
                }

                Spacer(Modifier.height(16.dp))

                TextButton(onClick = onChangeLanguageClicked) {
                    Icon(Icons.Filled.Language, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.height(0.dp))
                    Text(" " + stringResource(R.string.login_choose_language))
                }
            }

            if (uiState.showLanguageList) {
                PickerDialog(
                    title = stringResource(R.string.login_choose_language),
                    items = uiState.languageList,
                    onItemSelected = onLanguageSelected,
                    onDismiss = { }
                )
            }

            if (uiState.showFacilityList) {
                PickerDialog(
                    title = stringResource(R.string.login_choose_facility),
                    items = uiState.facilityList,
                    onItemSelected = onFacilitySelected,
                    onDismiss = { }
                )
            }

            if (uiState.showDeviceCapturePopup) {
                DeviceCaptureDialog(
                    value = uiState.deviceIdInput,
                    onValueChange = onDeviceIdChanged,
                    onSubmit = onDeviceIdSubmit
                )
            }

            if (uiState.showSecondLoginAlert) {
                AlertDialog(
                    onDismissRequest = { },
                    title = { Text(stringResource(R.string.login_second_login_title)) },
                    text = { Text(uiState.secondLoginAlertMessage) },
                    confirmButton = {
                        TextButton(onClick = onSecondLoginProceed) {
                            Text(stringResource(R.string.login_second_login_proceed))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = onSecondLoginCancel) {
                            Text(stringResource(R.string.login_second_login_cancel))
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun NetworkStatusIndicator(status: NetworkStatus, onRetry: () -> Unit) {
    val (icon, color, label) = when (status) {
        NetworkStatus.ONLINE -> Triple(Icons.Filled.CheckCircle, Color(0xFF2E7D32), "Online")
        NetworkStatus.CONSTRAINED -> Triple(Icons.Filled.Warning, Color(0xFFF9A825), "Limited connectivity")
        NetworkStatus.OFFLINE -> Triple(Icons.Filled.WifiOff, MaterialTheme.colorScheme.error, "Offline")
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(0.dp))
        Text(text = " $label", color = color, style = MaterialTheme.typography.bodyMedium)
        if (status == NetworkStatus.OFFLINE) {
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.common_retry))
            }
        }
    }
}

@Composable
private fun PickerDialog(
    title: String,
    items: List<FacilityModel>,
    onItemSelected: (FacilityModel) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            LazyColumn {
                items(items) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = { onItemSelected(item) }) {
                            Text(item.facilityName)
                        }
                    }
                }
            }
        },
        confirmButton = { }
    )
}

@Composable
private fun DeviceCaptureDialog(
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(stringResource(R.string.login_capture_device_title)) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(stringResource(R.string.login_capture_device_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = onSubmit) {
                Text(stringResource(R.string.login_capture_device_submit))
            }
        }
    )
}

