package com.landmarkgroup.sahlawarehouse.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.landmarkgroup.sahlawarehouse.core.auth.AuthManager
import com.landmarkgroup.sahlawarehouse.core.common.BottomTabMenu
import com.landmarkgroup.sahlawarehouse.core.common.ScanType
import com.landmarkgroup.sahlawarehouse.core.common.ScrollMenuItem
import com.landmarkgroup.sahlawarehouse.core.data.settings.SessionCache
import com.landmarkgroup.sahlawarehouse.core.data.settings.SettingsRepository
import com.landmarkgroup.sahlawarehouse.feature.home.data.HomeMenuData
import com.landmarkgroup.sahlawarehouse.feature.login.data.repository.LoginRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Direct port of the old `LandingViewModel` (`SahlaWH.Core.ViewModels.Home.LandingViewModel`) -
 * role-filtered scroll-menu / bottom-tab dashboard. `InitViewModel()` -> [init]/[loadMenus],
 * `OnScrollMenuSelected(ScrollMenuItem)` -> [onTileClicked], `OnLogoutSelected()` -> [onLogoutConfirmed].
 */
data class HomeUiState(
    val firstName: String = "",
    val facilityName: String = "",
    val scrollMenuItems: List<ScrollMenuItem> = emptyList(),
    val bottomTabMenu: List<BottomTabMenu> = emptyList(),
    val showLogoutConfirm: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sessionCache: SessionCache,
    private val settingsRepository: SettingsRepository,
    private val loginRepository: LoginRepository,
    private val authManager: AuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeEvent>(extraBufferCapacity = 4)
    val events: SharedFlow<HomeEvent> = _events

    init {
        loadMenus()
    }

    /** Direct port of `InitViewModel()`'s role-based scroll/tab menu population. */
    private fun loadMenus() {
        val loggedInRoles = sessionCache.roles
            .split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val isRoleAuthorized = loggedInRoles.isEmpty() ||
            HomeMenuData.AUTHORIZED_ROLES.any { authorized ->
                loggedInRoles.any { it.equals(authorized, ignoreCase = true) }
            }

        val scrollMenu = if (isRoleAuthorized) HomeMenuData.defaultScrollMenu else emptyList()
        val tabMenu = if (isRoleAuthorized) HomeMenuData.defaultTabMenu else emptyList()

        _uiState.update {
            it.copy(
                firstName = sessionCache.firstName,
                facilityName = sessionCache.facilityName,
                scrollMenuItems = scrollMenu,
                bottomTabMenu = tabMenu
            )
        }
    }

    /** Direct port of `OnScrollMenuSelected(ScrollMenuItem)`. */
    fun onTileClicked(item: ScrollMenuItem) {
        if (!item.isActive) return
        _events.tryEmit(HomeEvent.NavigateToFeature(item.menuIndex))
    }

    fun onLogoutClicked() {
        _uiState.update { it.copy(showLogoutConfirm = true) }
    }

    fun onLogoutDismissed() {
        _uiState.update { it.copy(showLogoutConfirm = false) }
    }

    /**
     * Direct port of `OnLogoutSelected()`: de-registers the device, clears local session/WebView
     * cookies, then signals navigation back to Login.
     */
    fun onLogoutConfirmed() = viewModelScope.launch {
        _uiState.update { it.copy(showLogoutConfirm = false) }
        try {
            loginRepository.deRegisterDevice(sessionCache.userName, false)
        } catch (_: Throwable) {
            // Best-effort, mirrors the old app's try/catch around DeRegsiterDevice().
        }

        settingsRepository.clearSession()
        authManager.clearWebViewSession()
        _events.tryEmit(HomeEvent.NavigateToLogin)
    }
}

private inline fun MutableStateFlow<HomeUiState>.update(block: (HomeUiState) -> HomeUiState) {
    value = block(value)
}
