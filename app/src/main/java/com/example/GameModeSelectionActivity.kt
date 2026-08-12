package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.chess.model.GameMode
import com.example.chess.model.GameStatus
import com.example.chess.ui.GameHistoryDialog
import com.example.chess.ui.GameModeSelectionScreen
import com.example.chess.ui.GeneralSettingsDialog
import com.example.ui.theme.MyApplicationTheme

class GameModeSelectionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemNavigationBars()

        val themeManager = com.example.chess.data.ChessThemeManager(this)

        setContent {
            MyApplicationTheme {
                var showHistory by remember { mutableStateOf(false) }
                var showSettings by remember { mutableStateOf(false) }
                
                // Get current game status from intent if returning from game
                val isGameInProgress = intent.getBooleanExtra(MainActivity.EXTRA_IS_GAME_IN_PROGRESS, false)

                GameModeSelectionScreen(
                    onSelectMode = { mode ->
                        launchSetup(mode)
                    },
                    onOpenHistory = {
                        showHistory = true
                    },
                    onOpenSettings = {
                        showSettings = true
                    },
                    gameStatus = if (isGameInProgress) GameStatus.IN_PROGRESS else GameStatus.NOT_STARTED,
                    onReturnToCurrentGame = {
                        finish()
                    },
                    hasPersistedGame = themeManager.getPersistedGameState() != null,
                    onLoadPersistedGame = {
                        launchPersistedGame()
                    }
                )

                if (showHistory) {
                    GameHistoryDialog(
                        onDismiss = { showHistory = false }
                    )
                }

                if (showSettings) {
                    var sound by remember { mutableStateOf(themeManager.isSoundEnabled()) }
                    var hints by remember { mutableStateOf(themeManager.isMoveHintsEnabled()) }
                    var save by remember { mutableStateOf(themeManager.isGamePersistenceEnabled()) }

                    GeneralSettingsDialog(
                        isSoundEnabled = sound,
                        isMoveHintsEnabled = hints,
                        isSaveGameEnabled = save,
                        onSoundToggled = { 
                            themeManager.saveSoundEnabled(it)
                            sound = it
                        },
                        onMoveHintsToggled = { 
                            themeManager.saveMoveHintsEnabled(it)
                            hints = it
                        },
                        onSaveGameToggled = { 
                            themeManager.saveGamePersistenceEnabled(it)
                            save = it
                        },
                        onDismiss = { showSettings = false }
                    )
                }
            }
        }
    }

    private fun launchSetup(mode: GameMode) {
        val intent = Intent(this, GameSetupActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_GAME_MODE, mode.name)
        }
        startActivity(intent)
    }

    private fun launchPersistedGame() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("LOAD_PERSISTED", true)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        hideSystemNavigationBars()
    }

    private fun hideSystemNavigationBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
            show(WindowInsetsCompat.Type.statusBars())
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}
