package com.example.chess.engine

import android.content.Context
import android.util.Log
import com.example.chess.model.Move
import com.example.chess.model.Position
import java.io.*

/**
 * Stockfish Engine Wrapper for Android.
 * Communicates with the Stockfish binary via UCI protocol.
 */
class StockfishEngine(private val context: Context) {

    private var process: Process? = null
    private var reader: BufferedReader? = null
    private var writer: BufferedWriter? = null

    /**
     * Extracts and initializes the Stockfish binary if not already present.
     */
    private fun ensureBinaryExists(): String? {
        val binaryFile = File(context.filesDir, "stockfish")
        if (!binaryFile.exists()) {
            try {
                context.assets.open("stockfish").use { inputStream ->
                    FileOutputStream(binaryFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                binaryFile.setExecutable(true)
            } catch (e: Exception) {
                Log.e("StockfishEngine", "Failed to extract stockfish binary", e)
                return null
            }
        }
        return binaryFile.absolutePath
    }

    private fun startEngine(): Boolean {
        val path = ensureBinaryExists() ?: return false
        return try {
            process = ProcessBuilder(path).start()
            reader = process?.inputStream?.bufferedReader()
            writer = process?.outputStream?.bufferedWriter()
            
            sendCommand("uci")
            sendCommand("setoption name Use NNUE value false") // Disable NNUE as requested
            sendCommand("isready")
            
            // Wait for readyok
            var line: String?
            while (reader?.readLine().also { line = it } != null) {
                if (line == "readyok") break
            }
            true
        } catch (e: Exception) {
            Log.e("StockfishEngine", "Failed to start stockfish engine", e)
            false
        }
    }

    private fun stopEngine() {
        try {
            sendCommand("quit")
            process?.destroy()
        } catch (e: Exception) {
            // Ignore
        } finally {
            process = null
            reader = null
            writer = null
        }
    }

    private fun sendCommand(cmd: String) {
        writer?.write("$cmd\n")
        writer?.flush()
    }

    /**
     * Calculates the best move for a given FEN position.
     * depth: Calculation depth (higher is stronger but slower)
     */
    fun getBestMove(fen: String, depth: Int = 12): String? {
        if (!startEngine()) return null
        
        return try {
            sendCommand("position fen $fen")
            sendCommand("go depth $depth")
            
            var line: String?
            var bestMove: String? = null
            while (reader?.readLine().also { line = it } != null) {
                val currentLine = line ?: break
                if (currentLine.startsWith("bestmove")) {
                    bestMove = currentLine.split(" ")[1]
                    break
                }
            }
            bestMove
        } catch (e: Exception) {
            Log.e("StockfishEngine", "Error getting best move from Stockfish", e)
            null
        } finally {
            stopEngine()
        }
    }

    /**
     * Converts a UCI move string (e.g., "e2e4") to a Move object.
     */
    fun parseUciMove(board: ChessBoard, uciMove: String): Move? {
        if (uciMove.length < 4) return null
        
        val fromCol = uciMove[0] - 'a'
        val fromRow = 8 - (uciMove[1] - '0')
        val toCol = uciMove[2] - 'a'
        val toRow = 8 - (uciMove[3] - '0')
        
        val from = Position(fromRow, fromCol)
        val to = Position(toRow, toCol)
        
        val legalMoves = board.getLegalMovesForPosition(from)
        return legalMoves.find { it.to == to }
    }
}
