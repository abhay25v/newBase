package com.landmarkgroup.sahlawarehouse.feature.home.ui

import com.landmarkgroup.sahlawarehouse.core.common.ScanType


sealed class HomeEvent {
    data class NavigateToFeature(val scanType: ScanType) : HomeEvent()
    data object NavigateToLogin : HomeEvent()
}
