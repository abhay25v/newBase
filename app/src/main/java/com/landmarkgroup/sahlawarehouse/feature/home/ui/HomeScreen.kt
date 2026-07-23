package com.landmarkgroup.sahlawarehouse.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.landmarkgroup.sahlawarehouse.R
import com.landmarkgroup.sahlawarehouse.core.common.BottomTabMenu
import com.landmarkgroup.sahlawarehouse.core.common.ScanType
import com.landmarkgroup.sahlawarehouse.core.common.ScrollMenuItem
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onNavigateToFeature: (ScanType) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is HomeEvent.NavigateToLogin -> onLogout()
                is HomeEvent.NavigateToFeature -> onNavigateToFeature(event.scanType)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.home_welcome, uiState.firstName),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (uiState.facilityName.isNotBlank()) {
                            Text(
                                text = uiState.facilityName,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::onLogoutClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = stringResource(R.string.common_logout)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                windowInsets = WindowInsets.systemBars
            )
        },
        bottomBar = {
            if (uiState.bottomTabMenu.isNotEmpty()) {
                HomeBottomTabBar(tabs = uiState.bottomTabMenu)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ScrollMenuGrid(
                items = uiState.scrollMenuItems,
                onTileClicked = viewModel::onTileClicked
            )
        }

        if (uiState.showLogoutConfirm) {
            AlertDialog(
                onDismissRequest = viewModel::onLogoutDismissed,
                title = { Text(stringResource(R.string.home_logout_confirm_title)) },
                text = { Text(stringResource(R.string.home_logout_confirm_message)) },
                confirmButton = {
                    TextButton(onClick = viewModel::onLogoutConfirmed) {
                        Text(stringResource(R.string.common_ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::onLogoutDismissed) {
                        Text(stringResource(R.string.common_cancel))
                    }
                }
            )
        }
    }
}

@Composable
private fun ScrollMenuGrid(
    items: List<ScrollMenuItem>,
    onTileClicked: (ScrollMenuItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        items(items) { item ->
            ScrollMenuTile(item = item, onClick = { onTileClicked(item) })
        }
    }
}

@Composable
private fun ScrollMenuTile(item: ScrollMenuItem, onClick: () -> Unit) {
    val topColor = runCatching { Color(android.graphics.Color.parseColor(item.menuTopColor)) }
        .getOrDefault(MaterialTheme.colorScheme.primary)
    val bottomColor = runCatching { Color(android.graphics.Color.parseColor(item.menuBottomColor)) }
        .getOrDefault(MaterialTheme.colorScheme.primaryContainer)
    val textColor = runCatching { Color(android.graphics.Color.parseColor(item.menuTextColor)) }
        .getOrDefault(MaterialTheme.colorScheme.onSurface)

    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(enabled = item.isActive, onClick = onClick)
            .background(
                brush = Brush.verticalGradient(listOf(topColor, bottomColor)),
                shape = RoundedCornerShape(16.dp)
            )

            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = item.menuTopTitle,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Text(
            text = item.menuDescription,
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}


@Composable
private fun HomeBottomTabBar(tabs: List<BottomTabMenu>) {
    NavigationBar {
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = tab.isSelected,
                onClick = {},
                icon = {},
                label = { Text(tab.tabName) }
            )
        }
    }
}

