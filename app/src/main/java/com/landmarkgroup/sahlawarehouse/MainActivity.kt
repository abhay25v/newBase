package com.landmarkgroup.sahlawarehouse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.landmarkgroup.sahlawarehouse.core.navigation.SahlaNavGraph
import com.landmarkgroup.sahlawarehouse.core.ui.theme.SahlaWarehouseTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * All screens are Composables hosted via Navigation-Compose in [SahlaNavGraph].
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        setContent {
            SahlaWarehouseTheme {
                val navController = rememberNavController()
                Surface {
                    SahlaNavGraph(navController = navController)
                }
            }
        }
    }
}
