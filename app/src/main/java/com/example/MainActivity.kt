package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.chess.ui.ChessScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    hideSystemNavigationBars()

    window.decorView.setOnSystemUiVisibilityChangeListener {
      hideSystemNavigationBars()
    }

    setContent {
      MyApplicationTheme {
        ChessScreen()
      }
    }
  }

  override fun onResume() {
    super.onResume()
    hideSystemNavigationBars()
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    hideSystemNavigationBars()
  }

  private fun hideSystemNavigationBars() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    window.navigationBarColor = android.graphics.Color.TRANSPARENT
    window.statusBarColor = android.graphics.Color.TRANSPARENT

    @Suppress("DEPRECATION")
    window.decorView.systemUiVisibility = (
      android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
      or android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
      or android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
      or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
      or android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
      or android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    )

    WindowCompat.getInsetsController(window, window.decorView).apply {
      isAppearanceLightStatusBars = false // Set top status bar icons (time, battery, etc.) to white
      isAppearanceLightNavigationBars = false
      hide(WindowInsetsCompat.Type.navigationBars() or WindowInsetsCompat.Type.statusBars()) // Hide phone navigation bar completely
      systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
  }
}


