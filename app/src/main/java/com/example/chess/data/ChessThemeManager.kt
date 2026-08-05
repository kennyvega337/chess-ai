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
        val name = prefs.getString("selected_difficulty", com.example.chess.model.DifficultyLevel.MEDIUM.name)
        return try { com.example.chess.model.DifficultyLevel.valueOf(name!!) } catch (e: Exception) { com.example.chess.model.DifficultyLevel.MEDIUM }
    }

    fun saveSideOption(option: com.example.chess.model.SideOption) {
        prefs.edit().putString("selected_side_option", option.name).apply()
    }

    fun getSelectedSideOption(): com.example.chess.model.SideOption {
        val name = prefs.getString("selected_side_option", com.example.chess.model.SideOption.WHITE.name)
        return try { com.example.chess.model.SideOption.valueOf(name!!) } catch (e: Exception) { com.example.chess.model.SideOption.WHITE }
    }
}
