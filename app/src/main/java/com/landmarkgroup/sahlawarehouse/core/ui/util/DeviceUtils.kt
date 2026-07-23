package com.landmarkgroup.sahlawarehouse.core.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration


enum class DeviceType {
    PHONE,
    TABLET
}

@Composable
fun currentDeviceType(): DeviceType {
    val configuration = LocalConfiguration.current
    return remember(configuration.screenWidthDp) {
        if (configuration.screenWidthDp >= 600) {
            DeviceType.TABLET
        } else {
            DeviceType.PHONE
        }
    }
}

/**
 * Convenience function to check if the current device is a tablet.
 * @return true if screen width >= 600dp
 */
@Composable
fun isTablet(): Boolean = currentDeviceType() == DeviceType.TABLET

/**
 * Convenience function to check if the current device is a phone.
 * @return true if screen width < 600dp
 */
@Composable
fun isPhone(): Boolean = currentDeviceType() == DeviceType.PHONE
