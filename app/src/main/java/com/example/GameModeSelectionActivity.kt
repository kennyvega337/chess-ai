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
import com.example.chess.model.ChessTheme
import com.example.chess.model.GameMode
import com.example.chess.model.GameStatus
import com.example.chess.ui.ChessThemeDialog
import com.example.chess.ui.GameHistoryDialog
import com.example.chess.ui.GameModeSelectionScreen
import com.example.chess.ui.GeneralSettingsDialog
import com.example.chess.ui.isThemeLight
import com.example.ui.theme.MyApplicationTheme

class GameModeSelectionActivity : ComponentActivity() {

    private lateinit var themeManager: com.example.chess.data.ChessThemeManager
    private val currentThemeState = mutableStateOf(ChessTheme.CLASSIC)
    private val isGameInProgressState = mutableStateOf(false)
    private val hasPersistedGameState = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        themeManager = com.example.chess.data.ChessThemeManager(this)
        currentThemeState.value = themeManager.getSelectedTheme()
        isGameInProgressState.value = intent.getBooleanExtra(MainActivity.EXTRA_IS_GAME_IN_PROGRESS, false)
        hasPersistedGameState.value = themeManager.getPersistedGameState() != null
        hideSystemNavigationBars()

        setContent {
            val currentTheme by currentThemeState
            val isGameInProgress by isGameInProgressState
            val hasPersistedGame by hasPersistedGameState

            SideEffect {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = isThemeLight(currentTheme)
                    isAppearanceLightNavigationBars = isThemeLight(currentTheme)
                }
            }

            MyApplicationTheme(selectedTheme = currentTheme) {
                var showHistory by remember { mutableStateOf(false) }
                var showSettings by remember { mutableStateOf(false) }
                var showTheme by remember { mutableStateOf(false) }

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
                    onOpenTheme = {
                        showTheme = true
                    },
                    gameStatus = if (isGameInProgress) GameStatus.IN_PROGRESS else GameStatus.NOT_STARTED,
                    onReturnToCurrentGame = {
                        val intent = Intent(this@GameModeSelectionActivity, MainActivity::class.java).apply {
                            putExtra("RETURN_TO_GAME", true)
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    },
                    hasPersistedGame = hasPersistedGame,
                    onLoadPersistedGame = {
                        launchPersistedGame()
                    },
                    selectedTheme = currentTheme
                )

                if (showHistory) {
                    GameHistoryDialog(
                        onDismiss = { showHistory = false },
                        selectedTheme = currentTheme
                    )
                }

                if (showTheme) {
                    ChessThemeDialog(
                        selectedTheme = currentTheme,
                        viewMode = themeManager.getSelectedViewMode(),
                        gameMode = GameMode.VS_AI, // Default for menu
                        onThemeSelect = { theme ->
                            themeManager.saveTheme(theme.name)
                            currentThemeState.value = theme
                        },
                        onViewModeChange = { mode ->
                            themeManager.saveViewMode(mode)
                        },
                        onDismiss = { showTheme = false }
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
                        onDismiss = { showSettings = false },
                        selectedTheme = currentTheme
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        currentThemeState.value = themeManager.getSelectedTheme()
        isGameInProgressState.value = intent.getBooleanExtra(MainActivity.EXTRA_IS_GAME_IN_PROGRESS, false)
        hasPersistedGameState.value = themeManager.getPersistedGameState() != null
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
        currentThemeState.value = themeManager.getSelectedTheme()
        hasPersistedGameState.value = themeManager.getPersistedGameState() != null
        hideSystemNavigationBars()
    }

    private fun hideSystemNavigationBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        WindowCompat.getInsetsController(window, window.decorView).apply {
            val currentTheme = themeManager.getSelectedTheme()
            isAppearanceLightStatusBars = isThemeLight(currentTheme)
            isAppearanceLightNavigationBars = isThemeLight(currentTheme)
            show(WindowInsetsCompat.Type.statusBars())
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}
