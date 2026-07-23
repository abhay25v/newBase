package com.landmarkgroup.sahlawarehouse.core.common

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Replaces the old `IDeviceService.GetInfo()` (Android `Settings.Secure.ANDROID_ID` based MAC/id
 * surrogate used since real MAC addresses aren't accessible on modern Android) - kept as the
 * device identity value sent as `mac_id`/`device` in `RegisterDeviceModel`.
 */
@Singleton
class DeviceIdProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    @SuppressLint("HardwareIds")
    fun getDeviceId(): String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown-device"
}
