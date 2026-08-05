package com.example.chess.data

import android.content.Context
import com.example.chess.model.GameMode
import com.example.chess.model.GameStatus
import com.example.chess.model.PieceColor
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class GameHistoryItem(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val dateFormatted: String,
    val gameMode: GameMode,
    val text: String
)

class GameHistoryManager(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("chess_game_history_prefs", Context.MODE_PRIVATE)

    fun getHistoryList(): List<GameHistoryItem> {
        val jsonStr = prefs.getString("history_list_json", "[]") ?: "[]"
        val list = mutableListOf<GameHistoryItem>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    GameHistoryItem(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        dateFormatted = obj.optString("dateFormatted", ""),
                        gameMode = try { GameMode.valueOf(obj.optString("gameMode", "VS_AI")) } catch (e: Exception) { GameMode.VS_AI },
                        text = obj.optString("text", "")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list.sortedByDescending { it.timestamp }
    }

    fun addHistoryItem(item: GameHistoryItem) {
        val currentList = getHistoryList().toMutableList()
        currentList.add(0, item) // Add newest at top
        val maxItems = 100
        val trimmedList = if (currentList.size > maxItems) currentList.take(maxItems) else currentList

        val jsonArray = JSONArray()
        for (h in trimmedList) {
            val obj = JSONObject().apply {
                put("id", h.id)
                put("timestamp", h.timestamp)
                put("dateFormatted", h.dateFormatted)
                put("gameMode", h.gameMode.name)
                put("text", h.text)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString("history_list_json", jsonArray.toString()).apply()
    }

    fun clearHistory() {
        prefs.edit().remove("history_list_json").apply()
    }

    companion object {
        fun formatDate(timestamp: Long = System.currentTimeMillis()): String {
            val sdf = SimpleDateFormat("d/M/yy HH:mm", Locale.getDefault())
            return "Ngày ${sdf.format(Date(timestamp))}"
        }

        fun generateHistoryText(
            gameMode: GameMode,
            userColor: PieceColor,
            gameStatus: GameStatus,
            winner: PieceColor?,
            isQuitOrAppClosed: Boolean
        ): String {
            if (gameMode == GameMode.VS_AI) {
                val colorStr = if (userColor == PieceColor.WHITE) "Trắng" else "Đen"
                return if (isQuitOrAppClosed || gameStatus == GameStatus.RESIGNED) {
                    "$colorStr bỏ cuộc"
                } else if (gameStatus == GameStatus.CHECKMATE) {
                    if (winner == userColor) "$colorStr thắng" else "$colorStr thua"
                } else {
                    "$colorStr hòa"
                }
            } else { // TWO_PLAYERS
                return if (isQuitOrAppClosed) {
                    "Trắng hòa Đen hòa (2 người chơi)"
                } else if (gameStatus == GameStatus.CHECKMATE || (gameStatus == GameStatus.RESIGNED && winner != null)) {
                    if (winner == PieceColor.WHITE) {
                        "Trắng thắng Đen thua (2 người chơi)"
                    } else {
                        "Đen thắng Trắng thua (2 người chơi)"
                    }
                } else if (gameStatus == GameStatus.RESIGNED) {
                    // This case might not be reachable if winner is always set on resign
                    "Bỏ cuộc (2 người chơi)"
                } else {
                    "Trắng hòa Đen hòa (2 người chơi)"
                }
            }
        }
    }
}
