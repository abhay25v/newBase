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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.landmarkgroup.sahlawarehouse.R
import com.landmarkgroup.sahlawarehouse.core.common.FacilityModel

/*
  Tablet-optimised login screen layout.
 */
@Composable
fun LoginScreenTablet(
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
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Card wraps the main form content
                Card(
                    modifier = Modifier
                        .widthIn(max = 480.dp)
                        .fillMaxWidth()
                        .padding(48.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // App name with larger typography for tablets
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(64.dp))

                        // Network status indicator
                        TabletNetworkStatusIndicator(
                            status = uiState.networkStatus,
                            onRetry = onRetryPing
                        )

                        Spacer(Modifier.height(48.dp))

                        // Login button - larger for tablet touch targets
                        Button(
                            onClick = onLoginClicked,
                            enabled = uiState.loginButtonEnabled && !uiState.isBusy,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            if (uiState.isBusy) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = stringResource(R.string.login_button),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        // Language picker button
                        TextButton(onClick = onChangeLanguageClicked) {
                            Icon(
                                Icons.Filled.Language,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.height(0.dp))
                            Text(
                                text = " " + stringResource(R.string.login_choose_language),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }


            if (uiState.showLanguageList) {
                TabletPickerDialog(
                    title = stringResource(R.string.login_choose_language),
                    items = uiState.languageList,
                    onItemSelected = onLanguageSelected,
                    onDismiss = { }
                )
            }

            if (uiState.showFacilityList) {
                TabletPickerDialog(
                    title = stringResource(R.string.login_choose_facility),
                    items = uiState.facilityList,
                    onItemSelected = onFacilitySelected,
                    onDismiss = { }
                )
            }

            if (uiState.showDeviceCapturePopup) {
                TabletDeviceCaptureDialog(
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
private fun TabletNetworkStatusIndicator(status: NetworkStatus, onRetry: () -> Unit) {
    val (icon, color, label) = when (status) {
        NetworkStatus.ONLINE -> Triple(
            Icons.Filled.CheckCircle,
            Color(0xFF2E7D32),
            "Online"
        )
        NetworkStatus.CONSTRAINED -> Triple(
            Icons.Filled.Warning,
            Color(0xFFF9A825),
            "Limited connectivity"
        )
        NetworkStatus.OFFLINE -> Triple(
            Icons.Filled.WifiOff,
            MaterialTheme.colorScheme.error,
            "Offline"
        )
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(0.dp))
        Text(
            text = " $label",
            color = color,
            style = MaterialTheme.typography.bodyLarge
        )
        if (status == NetworkStatus.OFFLINE) {
            TextButton(onClick = onRetry) {
                Text(
                    stringResource(R.string.common_retry),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}


@Composable
private fun TabletPickerDialog(
    title: String,
    items: List<FacilityModel>,
    onItemSelected: (FacilityModel) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier.widthIn(max = 400.dp)
            ) {
                items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = { onItemSelected(item) }) {
                            Text(
                                text = item.facilityName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        },
        confirmButton = { }
    )
}

@Composable
private fun TabletDeviceCaptureDialog(
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
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 400.dp)
            )
        },
        confirmButton = {
            TextButton(onClick = onSubmit) {
                Text(
                    stringResource(R.string.login_capture_device_submit),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    )
}
