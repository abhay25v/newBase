package com.landmarkgroup.sahlawarehouse.core.common

/**
 * Direct port of `SahlaWH.Models.CustomViewsModel.ScrollMenuItem` - a single home-screen tile.
 * Colors are kept as hex strings (as in the old JSON resources) and parsed at render time.
 */
data class ScrollMenuItem(
    val menuIndex: ScanType,
    val menuTopTitle: String,
    val menuDescription: String,
    val menuTopColor: String = "#FFFFFF",
    val menuBottomColor: String = "#FFFFFF",
    val menuTextColor: String = "#4a4a4a",
    val isActive: Boolean = true
)

/**
 * Direct port of `SahlaWH.Models.CustomViewsModel.BottomTabMenu` - bottom tab bar entry.
 */
data class BottomTabMenu(
    val tabIndex: Int,
    val tabName: String,
    val isSelected: Boolean = false
)
