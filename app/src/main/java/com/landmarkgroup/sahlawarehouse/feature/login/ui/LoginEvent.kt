package com.landmarkgroup.sahlawarehouse.feature.login.ui

import androidx.annotation.StringRes

sealed class LoginEvent {
    data class ShowToast(@StringRes val messageRes: Int) : LoginEvent()
    data object NavigateHome : LoginEvent()
}
