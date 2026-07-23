package com.landmarkgroup.sahlawarehouse

import android.app.Application
import com.landmarkgroup.sahlawarehouse.core.data.settings.SettingsRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Application entry point.
 */
@HiltAndroidApp
class SahlaApplication : Application() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Hydrate synchronous session cache from persisted DataStore values.
        applicationScope.launch {
            settingsRepository.hydrateSessionCache()
        }

        Timber.i("SahlaApplication started. Environment=%s", BuildConfig.ENVIRONMENT_NAME)
    }
}
