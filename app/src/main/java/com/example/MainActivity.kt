package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.chess.model.DifficultyLevel
import com.example.chess.model.GameMode
import com.example.chess.model.PieceType
import com.example.chess.model.SideOption
import com.example.chess.ui.ChessScreen
import com.example.chess.ui.ChessViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ChessViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemNavigationBars()

        window.decorView.setOnSystemUiVisibilityChangeListener {
            hideSystemNavigationBars()
        }

        if (savedInstanceState == null) {
            handleIntent(intent)
        }

        setContent {
            MyApplicationTheme {
                ChessScreen(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return
        val gameModeStr = intent.getStringExtra(EXTRA_GAME_MODE)
        val sideOptionStr = intent.getStringExtra(EXTRA_SIDE_OPTION)
        val difficultyStr = intent.getStringExtra(EXTRA_DIFFICULTY)
        val tutorialPieceStr = intent.getStringExtra(EXTRA_TUTORIAL_PIECE)

        if (gameModeStr != null) {
            val gameMode = try { GameMode.valueOf(gameModeStr) } catch (e: Exception) { GameMode.VS_AI }
            val sideOption = try { SideOption.valueOf(sideOptionStr ?: "WHITE") } catch (e: Exception) { SideOption.WHITE }
            val difficulty = try { DifficultyLevel.valueOf(difficultyStr ?: "LEVEL_2") } catch (e: Exception) { DifficultyLevel.LEVEL_2 }

            if (gameMode == GameMode.TUTORIAL && tutorialPieceStr != null) {
                val pieceType = try { PieceType.valueOf(tutorialPieceStr) } catch (e: Exception) { PieceType.ROOK }
                viewModel.startTutorialMode(pieceType)
            } else {
                viewModel.startNewGame(sideOption, difficulty, gameMode)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemNavigationBars()
        viewModel.syncTheme()
    }

    override fun onDestroy() {
        if (isFinishing) {
            viewModel.handleAppQuitOrPause()
        }
        super.onDestroy()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        viewModel.handleAppQuitOrPause()
        super.onBackPressed()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        hideSystemNavigationBars()
    }

    private fun hideSystemNavigationBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false // White status bar icons
            isAppearanceLightNavigationBars = false
            show(WindowInsetsCompat.Type.statusBars()) // Keep status bar visible
            hide(WindowInsetsCompat.Type.navigationBars()) // Hide navigation bar completely
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    companion object {
        const val EXTRA_SIDE_OPTION = "extra_side_option"
        const val EXTRA_DIFFICULTY = "extra_difficulty"
        const val EXTRA_GAME_MODE = "extra_game_mode"
        const val EXTRA_TUTORIAL_PIECE = "extra_tutorial_piece"
        const val EXTRA_IS_GAME_IN_PROGRESS = "extra_is_game_in_progress"
    }
}


