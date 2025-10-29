package com.coffeecat.animeplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coffeecat.animeplayer.ui.screen.MainScreen
import com.coffeecat.animeplayer.viewmodel.MainViewModel
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = viewModel() // 取得 ViewModel
            MainScreen(viewModel = mainViewModel)           // 傳入 MainScreen
        }
    }
    override fun onResume() {
        super.onResume()
        updateSystemUiForOrientation()
    }
    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        updateSystemUiForOrientation()
    }
}
fun ComponentActivity.updateSystemUiForOrientation() {
    val isLandscape = resources.configuration.orientation ==
            android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val controller = WindowInsetsControllerCompat(window, window.decorView)

    if (isLandscape) {
        controller.hide(
            WindowInsetsCompat.Type.statusBars() or
                    WindowInsetsCompat.Type.navigationBars()
        )
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    } else {
        controller.show(
            WindowInsetsCompat.Type.statusBars() or
                    WindowInsetsCompat.Type.navigationBars()
        )
    }
}

