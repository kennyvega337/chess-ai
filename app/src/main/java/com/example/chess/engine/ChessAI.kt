package com.example.chess.engine

import com.example.chess.model.Move
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.model.Position
import kotlin.random.Random

class ChessAI(private val aiColor: PieceColor) {

    // Piece square values for easy positional heuristics
    private val pawnTable = arrayOf(
        intArrayOf( 0,  0,  0,  0,  0,  0,  0,  0),
        intArrayOf(50, 50, 50, 50, 50, 50, 50, 50),
        intArrayOf(10, 10, 20, 30, 30, 20, 10, 10),
        intArrayOf( 5,  5, 10, 25, 25, 10,  5,  5),
        intArrayOf( 0,  0,  0, 20, 20,  0,  0,  0),
        intArrayOf( 5, -5,-10,  0,  0,-10, -5,  5),
        intArrayOf( 5, 10, 10,-20,-20, 10, 10,  5),
        intArrayOf( 0,  0,  0,  0,  0,  0,  0,  0)
    )

    private val knightTable = arrayOf(
        intArrayOf(-50,-40,-30,-30,-30,-30,-40,-50),
        intArrayOf(-40,-20,  0,  0,  0,  0,-20,-40),
        intArrayOf(-30,  0, 10, 15, 15, 10,  0,-30),
        intArrayOf(-30,  5, 15, 20, 20, 15,  5,-30),
        intArrayOf(-30,  0, 15, 20, 20, 15,  0,-30),
        intArrayOf(-30,  5, 10, 15, 15, 10,  5,-30),
        intArrayOf(-40,-20,  0,  5,  5,  0,-20,-40),
        intArrayOf(-50,-40,-30,-30,-30,-30,-40,-50)
    )

    fun chooseMove(board: ChessBoard): Move? {
        val legalMoves = board.getLegalMoves(aiColor)
        if (legalMoves.isEmpty()) return null

        var bestMove: Move? = null
        var maxScore = -999999

        for (move in legalMoves) {
            val nextBoard = board.copy()
            nextBoard.applyMove(move)

            // Checkmate delivered by AI is top priority
            if (nextBoard.getLegalMoves(aiColor.opposite).isEmpty() && nextBoard.isKingInCheck(aiColor.opposite)) {
                return move
            }

            var score = evaluateBoard(nextBoard)

            // Add material gain incentive
            if (move.capturedPiece != null) {
                score += move.capturedPiece.type.value - (move.piece.type.value / 10)
            }

            // Easy difficulty noise (gives human player a fair & fun chance)
            score += Random.nextInt(-35, 35)

            if (score > maxScore) {
                maxScore = score
                bestMove = move
            }
        }

        return bestMove ?: legalMoves.random()
    }

    private fun evaluateBoard(board: ChessBoard): Int {
        var score = 0
        for (r in 0..7) {
            for (c in 0..7) {
                val piece = board.getPiece(r, c) ?: continue
                var valP = piece.type.value

                // Positional bonus
                val tableRow = if (piece.color == PieceColor.WHITE) r else 7 - r
                when (piece.type) {
                    PieceType.PAWN -> valP += pawnTable[tableRow][c]
                    PieceType.KNIGHT -> valP += knightTable[tableRow][c]
                    else -> {}
                }

                if (piece.color == aiColor) {
                    score += valP
                } else {
                    score -= valP
                }
            }
        }
        return score
    }
}
