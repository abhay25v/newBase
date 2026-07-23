package com.landmarkgroup.sahlawarehouse.core.navigation

import com.landmarkgroup.sahlawarehouse.core.common.ScanType

/**
 * Route constants for Navigation-Compose.
 */
object SahlaDestinations {
    const val LOGIN = "login"
    const val HOME = "home"

    private const val COMING_SOON_ROUTE = "coming_soon"
    const val COMING_SOON_PATTERN = "$COMING_SOON_ROUTE/{scanType}"

    fun comingSoon(scanType: ScanType): String = "$COMING_SOON_ROUTE/${scanType.name}"
}
