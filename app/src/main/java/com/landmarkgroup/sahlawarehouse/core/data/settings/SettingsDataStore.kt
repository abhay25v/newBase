package com.landmarkgroup.sahlawarehouse.core.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import android.content.Context

/** Single named DataStore instance for all app settings (replaces `Plugin.Settings` prefs file). */
val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "sahla_settings")
