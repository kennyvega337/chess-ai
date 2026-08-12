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
import com.example.chess.model.GameTimerOption
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
        
        if (intent.getBooleanExtra("LOAD_PERSISTED", false)) {
            viewModel.loadPersistedGame()
            return
        }

        val gameModeStr = intent.getStringExtra(EXTRA_GAME_MODE)
        val puzzleFen = intent.getStringExtra("PUZZLE_FEN")
        val puzzleCategory = intent.getStringExtra("PUZZLE_CATEGORY")
        val puzzleLevel = intent.getIntExtra("PUZZLE_LEVEL", -1)
        val sideOptionStr = intent.getStringExtra(EXTRA_SIDE_OPTION)
        val difficultyStr = intent.getStringExtra(EXTRA_DIFFICULTY)
        val tutorialPieceStr = intent.getStringExtra(EXTRA_TUTORIAL_PIECE)
        val timerOptionStr = intent.getStringExtra(EXTRA_TIMER_OPTION)
        val customMinutes = if (intent.hasExtra(EXTRA_CUSTOM_MINUTES)) intent.getIntExtra(EXTRA_CUSTOM_MINUTES, 10) else null

        if (gameModeStr != null) {
            val gameMode = try { GameMode.valueOf(gameModeStr) } catch (e: Exception) { GameMode.VS_AI }
            val sideOption = try { SideOption.valueOf(sideOptionStr ?: "WHITE") } catch (e: Exception) { SideOption.WHITE }
            val difficulty = try { DifficultyLevel.valueOf(difficultyStr ?: "LEVEL_2") } catch (e: Exception) { DifficultyLevel.LEVEL_2 }
            val timerOption = try { GameTimerOption.valueOf(timerOptionStr ?: "NONE") } catch (e: Exception) { GameTimerOption.NONE }

            if (gameMode == GameMode.PUZZLE && puzzleFen != null) {
                viewModel.startPuzzleMode(puzzleFen, puzzleCategory, if (puzzleLevel != -1) puzzleLevel else null)
            } else if (gameMode == GameMode.TUTORIAL && tutorialPieceStr != null) {
                val pieceType = try { PieceType.valueOf(tutorialPieceStr) } catch (e: Exception) { PieceType.ROOK }
                viewModel.startTutorialMode(pieceType)
            } else {
                viewModel.startNewGame(sideOption, difficulty, gameMode, timerOption, customMinutes)
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
        const val EXTRA_TIMER_OPTION = "extra_timer_option"
        const val EXTRA_CUSTOM_MINUTES = "extra_custom_minutes"
        const val EXTRA_IS_GAME_IN_PROGRESS = "extra_is_game_in_progress"
    }
}


