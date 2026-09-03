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

    fun saveHintEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("hint_enabled", enabled).apply()
    }

    fun isHintEnabled(): Boolean {
        return prefs.getBoolean("hint_enabled", true)
    }

    fun saveResignEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("resign_enabled", enabled).apply()
    }

    fun isResignEnabled(): Boolean {
        return prefs.getBoolean("resign_enabled", true)
    }

    fun saveUndoEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("undo_enabled", enabled).apply()
    }

    fun isUndoEnabled(): Boolean {
        return prefs.getBoolean("undo_enabled", true)
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

    fun hasValidPersistedGame(): Boolean {
        if (!isGamePersistenceEnabled()) return false
        val json = getPersistedGameState() ?: return false
        return try {
            val obj = org.json.JSONObject(json)
            val mode = obj.optString("gameMode")
            val isEligible = mode == "VS_AI" || mode == "TWO_PLAYERS"
            val hasBoard = obj.optJSONArray("board") != null
            if (isEligible && hasBoard) {
                true
            } else {
                clearPersistedGameState()
                false
            }
        } catch (e: Exception) {
            clearPersistedGameState()
            false
        }
    }

    fun savePuzzleCompleted(category: String, level: Int, mode: com.example.chess.model.GameMode = com.example.chess.model.GameMode.PUZZLE) {
        val key = if (mode == com.example.chess.model.GameMode.ONE_MOVE) "completed_one_move_puzzles" else "completed_puzzles"
        val completed = prefs.getStringSet(key, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        completed.add("${category}_$level")
        prefs.edit().putStringSet(key, completed).apply()
    }

    fun getCompletedPuzzles(mode: com.example.chess.model.GameMode = com.example.chess.model.GameMode.PUZZLE): Set<String> {
        val key = if (mode == com.example.chess.model.GameMode.ONE_MOVE) "completed_one_move_puzzles" else "completed_puzzles"
        return prefs.getStringSet(key, emptySet()) ?: emptySet()
    }

    // Scoring Mode Specific Persistence
    fun saveScoringSideOption(option: com.example.chess.model.SideOption) {
        prefs.edit().putString("scoring_side_option", option.name).apply()
    }

    fun getScoringSideOption(): com.example.chess.model.SideOption {
        val name = prefs.getString("scoring_side_option", com.example.chess.model.SideOption.WHITE.name)
        return try {
            com.example.chess.model.SideOption.valueOf(name!!)
        } catch (e: Exception) {
            com.example.chess.model.SideOption.WHITE
        }
    }

    fun saveScoringPieceType(type: com.example.chess.model.PieceType) {
        prefs.edit().putString("scoring_piece_type", type.name).apply()
    }

    fun getScoringPieceType(): com.example.chess.model.PieceType {
        val name = prefs.getString("scoring_piece_type", com.example.chess.model.PieceType.QUEEN.name)
        return try {
            com.example.chess.model.PieceType.valueOf(name!!)
        } catch (e: Exception) {
            com.example.chess.model.PieceType.QUEEN
        }
    }

    fun saveScoringSeconds(seconds: Int) {
        prefs.edit().putInt("scoring_seconds", seconds).apply()
    }

    fun getScoringSeconds(): Int {
        return prefs.getInt("scoring_seconds", 30)
    }
}
