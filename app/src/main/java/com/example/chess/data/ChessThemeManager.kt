package com.example.chess.data

import android.content.Context
import com.example.chess.model.ChessTheme

class ChessThemeManager(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("chess_theme_prefs", Context.MODE_PRIVATE)

    fun saveTheme(themeName: String) {
        prefs.edit().putString("selected_theme", themeName).apply()
    }

    fun getSelectedTheme(): ChessTheme {
        val themeName = prefs.getString("selected_theme", "Classic") ?: "Classic"
        return ChessTheme.fromName(themeName)
    }

    fun saveViewMode(mode: com.example.chess.model.BoardViewMode) {
        prefs.edit().putString("selected_view_mode", mode.name).apply()
    }

    fun getSelectedViewMode(): com.example.chess.model.BoardViewMode {
        val modeName = prefs.getString("selected_view_mode", com.example.chess.model.BoardViewMode.VIEW_2D.name)
        return try {
            com.example.chess.model.BoardViewMode.valueOf(modeName ?: com.example.chess.model.BoardViewMode.VIEW_2D.name)
        } catch (e: Exception) {
            com.example.chess.model.BoardViewMode.VIEW_2D
        }
    }

    fun saveGameMode(mode: com.example.chess.model.GameMode) {
        if (mode != com.example.chess.model.GameMode.TUTORIAL) {
            prefs.edit().putString("selected_game_mode", mode.name).apply()
        }
    }

    fun getSelectedGameMode(): com.example.chess.model.GameMode {
        val name = prefs.getString("selected_game_mode", com.example.chess.model.GameMode.VS_AI.name)
        return try { com.example.chess.model.GameMode.valueOf(name!!) } catch (e: Exception) { com.example.chess.model.GameMode.VS_AI }
    }

    fun saveDifficulty(level: com.example.chess.model.DifficultyLevel) {
        prefs.edit().putString("selected_difficulty", level.name).apply()
    }

    fun getSelectedDifficulty(): com.example.chess.model.DifficultyLevel {
        val name = prefs.getString("selected_difficulty", com.example.chess.model.DifficultyLevel.LEVEL_2.name)
        return try { com.example.chess.model.DifficultyLevel.valueOf(name!!) } catch (e: Exception) { com.example.chess.model.DifficultyLevel.LEVEL_2 }
    }

    fun saveSideOption(option: com.example.chess.model.SideOption) {
        prefs.edit().putString("selected_side_option", option.name).apply()
    }

    fun getSelectedSideOption(): com.example.chess.model.SideOption {
        val name = prefs.getString("selected_side_option", com.example.chess.model.SideOption.WHITE.name)
        return try { com.example.chess.model.SideOption.valueOf(name!!) } catch (e: Exception) { com.example.chess.model.SideOption.WHITE }
    }

    fun saveTimerOption(option: com.example.chess.model.GameTimerOption) {
        prefs.edit().putString("selected_timer_option", option.name).apply()
    }

    fun getSelectedTimerOption(): com.example.chess.model.GameTimerOption {
        val name = prefs.getString("selected_timer_option", com.example.chess.model.GameTimerOption.NONE.name)
        return try { com.example.chess.model.GameTimerOption.valueOf(name!!) } catch (e: Exception) { com.example.chess.model.GameTimerOption.NONE }
    }

    fun saveCustomMinutes(minutes: Int) {
        prefs.edit().putInt("selected_custom_minutes", minutes).apply()
    }

    fun getSelectedCustomMinutes(): Int {
        return prefs.getInt("selected_custom_minutes", 10)
    }

    fun saveSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("sound_enabled", enabled).apply()
    }

    fun isSoundEnabled(): Boolean {
        return prefs.getBoolean("sound_enabled", true)
    }

    fun saveMoveHintsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("move_hints_enabled", enabled).apply()
    }

    fun isMoveHintsEnabled(): Boolean {
        return prefs.getBoolean("move_hints_enabled", true)
    }

    fun saveGamePersistenceEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("game_persistence_enabled", enabled).apply()
    }

    fun isGamePersistenceEnabled(): Boolean {
        return prefs.getBoolean("game_persistence_enabled", false)
    }

    fun saveCurrentGameState(json: String) {
        prefs.edit().putString("persisted_game_state", json).apply()
    }

    fun getPersistedGameState(): String? {
        return prefs.getString("persisted_game_state", null)
    }

    fun clearPersistedGameState() {
        prefs.edit().remove("persisted_game_state").apply()
    }

    fun savePuzzleCompleted(category: String, level: Int) {
        val completed = prefs.getStringSet("completed_puzzles", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        completed.add("${category}_$level")
        prefs.edit().putStringSet("completed_puzzles", completed).apply()
    }

    fun getCompletedPuzzles(): Set<String> {
        return prefs.getStringSet("completed_puzzles", emptySet()) ?: emptySet()
    }
}
