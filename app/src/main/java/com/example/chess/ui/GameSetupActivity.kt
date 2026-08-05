package com.example.chess.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.MainActivity
import com.example.chess.model.DifficultyLevel
import com.example.chess.model.GameMode
import com.example.chess.model.GameStatus
import com.example.chess.model.PieceType
import com.example.chess.model.SideOption
import com.example.ui.theme.MyApplicationTheme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
class GameSetupActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemNavigationBars()

        val themeManager = com.example.chess.data.ChessThemeManager(this)

        val initialGameModeStr = intent.getStringExtra(MainActivity.EXTRA_GAME_MODE) ?: themeManager.getSelectedGameMode().name
        val initialSideStr = intent.getStringExtra(MainActivity.EXTRA_SIDE_OPTION) ?: themeManager.getSelectedSideOption().name
        val initialDifficultyStr = intent.getStringExtra(MainActivity.EXTRA_DIFFICULTY) ?: themeManager.getSelectedDifficulty().name
        val isGameInProgress = intent.getBooleanExtra(MainActivity.EXTRA_IS_GAME_IN_PROGRESS, false)

        val initialGameMode = try { GameMode.valueOf(initialGameModeStr) } catch (e: Exception) { GameMode.VS_AI }
        val initialSide = try { SideOption.valueOf(initialSideStr) } catch (e: Exception) { SideOption.WHITE }
        val initialDifficulty = try { DifficultyLevel.valueOf(initialDifficultyStr) } catch (e: Exception) { DifficultyLevel.MEDIUM }

        setContent {
            MyApplicationTheme {
                var showHistory by remember { mutableStateOf(false) }

                GameSetupScreen(
                    initialSideOption = initialSide,
                    initialDifficulty = initialDifficulty,
                    initialGameMode = initialGameMode,
                    gameStatus = if (isGameInProgress) GameStatus.IN_PROGRESS else GameStatus.NOT_STARTED,
                    onStartGame = { sideOption, difficulty, gameMode ->
                        launchGame(sideOption, difficulty, gameMode, null)
                    },
                    onStartTutorialPiece = { pieceType ->
                        launchGame(SideOption.WHITE, DifficultyLevel.EASY, GameMode.TUTORIAL, pieceType)
                    },
                    onReturnToCurrentGame = {
                        finish()
                    },
                    onOpenHistory = {
                        showHistory = true
                    }
                )

                if (showHistory) {
                    GameHistoryDialog(
                        onDismiss = { showHistory = false }
                    )
                }
            }
        }
    }

    private fun launchGame(
        sideOption: SideOption,
        difficulty: DifficultyLevel,
        gameMode: GameMode,
        tutorialPiece: PieceType?
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_SIDE_OPTION, sideOption.name)
            putExtra(MainActivity.EXTRA_DIFFICULTY, difficulty.name)
            putExtra(MainActivity.EXTRA_GAME_MODE, gameMode.name)
            if (tutorialPiece != null) {
                putExtra(MainActivity.EXTRA_TUTORIAL_PIECE, tutorialPiece.name)
            }
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
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

        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
            show(WindowInsetsCompat.Type.statusBars())
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}
