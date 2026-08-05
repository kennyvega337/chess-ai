package com.example.chess.engine

import com.example.chess.model.DifficultyLevel
import com.example.chess.model.Move
import com.example.chess.model.Piece
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.model.Position
import kotlin.random.Random

/**
 * Optimized Advanced Chess Engine for Hard AI Difficulty
 * Features:
 * - Minimax + Alpha-Beta Pruning with strict time budget (~800ms - 1200ms)
 * - Iterative Deepening (Depth 1 to 5) with PV Move prioritization
 * - Fast Quiescence Search for tactile capture stability
 * - Transposition Table with Zobrist Hashing
 * - Fast MVV-LVA Move Ordering & Early Pruning
 * - Positional Tables (PST) & Positional Evaluation
 */
class ChessAI(private val aiColor: PieceColor) {

    // --- Zobrist Hashing & Transposition Table ---
    private val zobristPieces = Array(64) { LongArray(12) { Random.nextLong() } }
    private val zobristWhiteTurn = Random.nextLong()

    private class TTEntry(
        val depth: Int,
        val score: Int,
        val flag: Int, // 0 = EXACT, 1 = LOWER, 2 = UPPER
        val bestMove: Move?
    )

    private val TT_EXACT = 0
    private val TT_LOWER = 1
    private val TT_UPPER = 2

    private val transpositionTable = HashMap<Long, TTEntry>(16384)

    // --- Positional Piece-Square Tables (White perspective) ---
    private val pawnTable = arrayOf(
        intArrayOf( 0,  0,  0,  0,  0,  0,  0,  0),
        intArrayOf(50, 50, 50, 50, 50, 50, 50, 50),
        intArrayOf(10, 10, 20, 30, 30, 20, 10, 10),
        intArrayOf( 5,  5, 10, 27, 27, 10,  5,  5),
        intArrayOf( 0,  0,  0, 22, 22,  0,  0,  0),
        intArrayOf( 5, -5,-10,  0,  0,-10, -5,  5),
        intArrayOf( 5, 10, 10,-20,-20, 10, 10,  5),
        intArrayOf( 0,  0,  0,  0,  0,  0,  0,  0)
    )

    private val knightTable = arrayOf(
        intArrayOf(-50,-40,-30,-30,-30,-30,-40,-50),
        intArrayOf(-40,-20,  0,  0,  0,  0,-20,-40),
        intArrayOf(-30,  0, 10, 15, 15, 10,  0,-30),
        intArrayOf(-30,  5, 15, 25, 25, 15,  5,-30),
        intArrayOf(-30,  0, 15, 25, 25, 15,  0,-30),
        intArrayOf(-30,  5, 10, 15, 15, 10,  5,-30),
        intArrayOf(-40,-20,  0,  5,  5,  0,-20,-40),
        intArrayOf(-50,-40,-30,-30,-30,-30,-40,-50)
    )

    private val bishopTable = arrayOf(
        intArrayOf(-20,-10,-10,-10,-10,-10,-10,-20),
        intArrayOf(-10,  0,  0,  0,  0,  0,  0,-10),
        intArrayOf(-10,  0,  5, 10, 10,  5,  0,-10),
        intArrayOf(-10,  5,  5, 10, 10,  5,  5,-10),
        intArrayOf(-10,  0, 10, 10, 10, 10,  0,-10),
        intArrayOf(-10, 10, 10, 10, 10, 10, 10,-10),
        intArrayOf(-10,  5,  0,  0,  0,  0,  5,-10),
        intArrayOf(-20,-10,-10,-10,-10,-10,-10,-20)
    )

    private val rookTable = arrayOf(
        intArrayOf( 0,  0,  0,  0,  0,  0,  0,  0),
        intArrayOf( 5, 10, 10, 10, 10, 10, 10,  5),
        intArrayOf(-5,  0,  0,  0,  0,  0,  0, -5),
        intArrayOf(-5,  0,  0,  0,  0,  0,  0, -5),
        intArrayOf(-5,  0,  0,  0,  0,  0,  0, -5),
        intArrayOf(-5,  0,  0,  0,  0,  0,  0, -5),
        intArrayOf(-5,  0,  0,  0,  0,  0,  0, -5),
        intArrayOf( 0,  0,  0,  5,  5,  0,  0,  0)
    )

    private val queenTable = arrayOf(
        intArrayOf(-20,-10,-10, -5, -5,-10,-10,-20),
        intArrayOf(-10,  0,  0,  0,  0,  0,  0,-10),
        intArrayOf(-10,  0,  5,  5,  5,  5,  0,-10),
        intArrayOf( -5,  0,  5,  5,  5,  5,  0, -5),
        intArrayOf(  0,  0,  5,  5,  5,  5,  0, -5),
        intArrayOf(-10,  5,  5,  5,  5,  5,  0,-10),
        intArrayOf(-10,  0,  5,  0,  0,  0,  0,-10),
        intArrayOf(-20,-10,-10, -5, -5,-10,-10,-20)
    )

    private val kingMiddlegameTable = arrayOf(
        intArrayOf(-30,-40,-40,-50,-50,-40,-40,-30),
        intArrayOf(-30,-40,-40,-50,-50,-40,-40,-30),
        intArrayOf(-30,-40,-40,-50,-50,-40,-40,-30),
        intArrayOf(-30,-40,-40,-50,-50,-40,-40,-30),
        intArrayOf(-20,-30,-30,-40,-40,-30,-30,-20),
        intArrayOf(-10,-20,-20,-20,-20,-20,-20,-10),
        intArrayOf( 20, 20,  0,  0,  0,  0, 20, 20),
        intArrayOf( 20, 30, 10,  0,  0, 10, 30, 20)
    )

    private val kingEndgameTable = arrayOf(
        intArrayOf(-50,-40,-30,-20,-20,-30,-40,-50),
        intArrayOf(-30,-20,-10,  0,  0,-10,-20,-30),
        intArrayOf(-30,-10, 20, 30, 30, 20,-10,-30),
        intArrayOf(-30,-10, 30, 40, 40, 30,-10,-30),
        intArrayOf(-30,-10, 30, 40, 40, 30,-10,-30),
        intArrayOf(-30,-10, 20, 30, 30, 20,-10,-30),
        intArrayOf(-30,-30,  0,  0,  0,  0,-30,-30),
        intArrayOf(-50,-30,-30,-30,-30,-30,-30,-50)
    )

    fun chooseMove(board: ChessBoard, difficulty: DifficultyLevel = DifficultyLevel.MEDIUM): Move? {
        val legalMoves = board.getLegalMoves(aiColor)
        if (legalMoves.isEmpty()) return null

        return when (difficulty) {
            DifficultyLevel.EASY -> chooseEasyMove(board, legalMoves)
            DifficultyLevel.MEDIUM -> chooseMediumMove(board, legalMoves)
            DifficultyLevel.HARD -> chooseHardMove(board, legalMoves)
        }
    }

    private fun chooseEasyMove(board: ChessBoard, legalMoves: List<Move>): Move {
        if (Random.nextFloat() < 0.25f) {
            return legalMoves.random()
        }

        var bestMove: Move? = null
        var maxScore = -999999

        for (move in legalMoves) {
            val nextBoard = board.copy()
            nextBoard.applyMove(move)

            if (nextBoard.getLegalMoves(aiColor.opposite).isEmpty() && nextBoard.isKingInCheck(aiColor.opposite)) {
                return move
            }

            var score = evaluateBoard(nextBoard)
            if (move.capturedPiece != null) {
                score += move.capturedPiece.type.value - (move.piece.type.value / 10)
            }

            score += Random.nextInt(-45, 45)

            if (score > maxScore) {
                maxScore = score
                bestMove = move
            }
        }
        return bestMove ?: legalMoves.random()
    }

    private fun chooseMediumMove(board: ChessBoard, legalMoves: List<Move>): Move {
        var bestMove = legalMoves.random()
        var maxScore = -999999

        val sortedMoves = orderMoves(legalMoves)

        for (move in sortedMoves) {
            val nextBoard = board.copy()
            nextBoard.applyMove(move)

            val opponentColor = aiColor.opposite
            val opponentMoves = nextBoard.getLegalMoves(opponentColor)

            var minOpponentScore = 999999
            if (opponentMoves.isEmpty()) {
                if (nextBoard.isKingInCheck(opponentColor)) {
                    return move
                } else {
                    minOpponentScore = 0
                }
            } else {
                for (oppMove in opponentMoves) {
                    val oppBoard = nextBoard.copy()
                    oppBoard.applyMove(oppMove)
                    val oppScore = evaluateBoard(oppBoard)
                    if (oppScore < minOpponentScore) {
                        minOpponentScore = oppScore
                    }
                }
            }

            var moveScore = minOpponentScore
            moveScore += Random.nextInt(-6, 6)

            if (moveScore > maxScore) {
                maxScore = moveScore
                bestMove = move
            }
        }
        return bestMove
    }

    /**
     * Fast Hard AI: Time-budgeted Iterative Deepening Minimax with Alpha-Beta Pruning
     * Maximum response time is strictly capped (~800ms - 1000ms) for snappy gameplay.
     */
    private fun chooseHardMove(board: ChessBoard, legalMoves: List<Move>): Move {
        transpositionTable.clear()

        val moveCandidates = orderMoves(legalMoves).toMutableList()
        var overallBestMove: Move = moveCandidates.first()

        val startTime = System.currentTimeMillis()
        val maxTimeMs = 1000L // 1.0 second strict target budget for maximum responsiveness
        val timeDeadline = startTime + maxTimeMs

        val maxTargetDepth = 5

        for (targetDepth in 1..maxTargetDepth) {
            if (System.currentTimeMillis() >= timeDeadline && targetDepth > 2) {
                break
            }

            var alpha = -99999999
            val beta = 99999999
            var bestMoveAtThisDepth: Move? = null

            for (i in moveCandidates.indices) {
                if (System.currentTimeMillis() >= timeDeadline && targetDepth > 2 && bestMoveAtThisDepth != null) {
                    break
                }

                val move = moveCandidates[i]
                val nextBoard = board.copy()
                nextBoard.applyMove(move)

                val oppColor = aiColor.opposite
                if (nextBoard.getLegalMoves(oppColor).isEmpty() && nextBoard.isKingInCheck(oppColor)) {
                    return move
                }

                val score = minimax(
                    board = nextBoard,
                    depth = targetDepth - 1,
                    alpha = alpha,
                    beta = beta,
                    isMaximizing = false,
                    currentTurn = oppColor,
                    timeDeadline = timeDeadline
                )

                if (score > alpha) {
                    alpha = score
                    bestMoveAtThisDepth = move
                }
            }

            if (bestMoveAtThisDepth != null) {
                overallBestMove = bestMoveAtThisDepth
                moveCandidates.remove(bestMoveAtThisDepth)
                moveCandidates.add(0, bestMoveAtThisDepth)
            }
        }

        return overallBestMove
    }

    private fun minimax(
        board: ChessBoard,
        depth: Int,
        alpha: Int,
        beta: Int,
        isMaximizing: Boolean,
        currentTurn: PieceColor,
        timeDeadline: Long
    ): Int {
        if (System.currentTimeMillis() >= timeDeadline) {
            return evaluateBoard(board)
        }

        var currAlpha = alpha
        var currBeta = beta

        val boardHash = computeHash(board, currentTurn)
        val ttEntry = transpositionTable[boardHash]
        if (ttEntry != null && ttEntry.depth >= depth) {
            when (ttEntry.flag) {
                TT_EXACT -> return ttEntry.score
                TT_LOWER -> {
                    if (ttEntry.score >= currBeta) return ttEntry.score
                    currAlpha = maxOf(currAlpha, ttEntry.score)
                }
                TT_UPPER -> {
                    if (ttEntry.score <= currAlpha) return ttEntry.score
                    currBeta = minOf(currBeta, ttEntry.score)
                }
            }
            if (currAlpha >= currBeta) return ttEntry.score
        }

        val legalMoves = board.getLegalMoves(currentTurn)

        if (legalMoves.isEmpty()) {
            if (board.isKingInCheck(currentTurn)) {
                return if (isMaximizing) -100000 - depth else 100000 + depth
            }
            return 0
        }

        if (depth <= 0) {
            return quiescenceSearch(board, currAlpha, currBeta, isMaximizing, currentTurn, maxDepth = 2, timeDeadline = timeDeadline)
        }

        val sortedMoves = orderMoves(legalMoves, ttEntry?.bestMove)
        var bestMoveLocal: Move? = null
        val originalAlpha = alpha

        if (isMaximizing) {
            var maxEval = -99999999
            for (move in sortedMoves) {
                val nextBoard = board.copy()
                nextBoard.applyMove(move)
                val eval = minimax(
                    board = nextBoard,
                    depth = depth - 1,
                    alpha = currAlpha,
                    beta = currBeta,
                    isMaximizing = false,
                    currentTurn = currentTurn.opposite,
                    timeDeadline = timeDeadline
                )
                if (eval > maxEval) {
                    maxEval = eval
                    bestMoveLocal = move
                }
                currAlpha = maxOf(currAlpha, eval)
                if (currBeta <= currAlpha) break
            }

            val flag = when {
                maxEval <= originalAlpha -> TT_UPPER
                maxEval >= beta -> TT_LOWER
                else -> TT_EXACT
            }
            transpositionTable[boardHash] = TTEntry(depth, maxEval, flag, bestMoveLocal)

            return maxEval
        } else {
            var minEval = 99999999
            for (move in sortedMoves) {
                val nextBoard = board.copy()
                nextBoard.applyMove(move)
                val eval = minimax(
                    board = nextBoard,
                    depth = depth - 1,
                    alpha = currAlpha,
                    beta = currBeta,
                    isMaximizing = true,
                    currentTurn = currentTurn.opposite,
                    timeDeadline = timeDeadline
                )
                if (eval < minEval) {
                    minEval = eval
                    bestMoveLocal = move
                }
                currBeta = minOf(currBeta, eval)
                if (currBeta <= currAlpha) break
            }

            val flag = when {
                minEval <= originalAlpha -> TT_UPPER
                minEval >= beta -> TT_LOWER
                else -> TT_EXACT
            }
            transpositionTable[boardHash] = TTEntry(depth, minEval, flag, bestMoveLocal)

            return minEval
        }
    }

    private fun quiescenceSearch(
        board: ChessBoard,
        alpha: Int,
        beta: Int,
        isMaximizing: Boolean,
        currentTurn: PieceColor,
        maxDepth: Int,
        timeDeadline: Long
    ): Int {
        val standPat = evaluateBoard(board)
        var currAlpha = alpha
        var currBeta = beta

        if (isMaximizing) {
            if (standPat >= currBeta) return currBeta
            if (standPat > currAlpha) currAlpha = standPat
        } else {
            if (standPat <= currAlpha) return currAlpha
            if (standPat < currBeta) currBeta = standPat
        }

        if (maxDepth <= 0 || System.currentTimeMillis() >= timeDeadline) return standPat

        val captureMoves = board.getLegalMoves(currentTurn)
            .filter { it.capturedPiece != null || it.promotion != null }
            .take(4) // Only consider top 4 tactical captures to keep search ultra fast

        if (captureMoves.isEmpty()) return standPat

        val sortedCaptures = orderMoves(captureMoves)

        if (isMaximizing) {
            var maxEval = standPat
            for (move in sortedCaptures) {
                val nextBoard = board.copy()
                nextBoard.applyMove(move)
                val eval = quiescenceSearch(
                    board = nextBoard,
                    alpha = currAlpha,
                    beta = currBeta,
                    isMaximizing = false,
                    currentTurn = currentTurn.opposite,
                    maxDepth = maxDepth - 1,
                    timeDeadline = timeDeadline
                )
                maxEval = maxOf(maxEval, eval)
                currAlpha = maxOf(currAlpha, eval)
                if (currBeta <= currAlpha) break
            }
            return maxEval
        } else {
            var minEval = standPat
            for (move in sortedCaptures) {
                val nextBoard = board.copy()
                nextBoard.applyMove(move)
                val eval = quiescenceSearch(
                    board = nextBoard,
                    alpha = currAlpha,
                    beta = currBeta,
                    isMaximizing = true,
                    currentTurn = currentTurn.opposite,
                    maxDepth = maxDepth - 1,
                    timeDeadline = timeDeadline
                )
                minEval = minOf(minEval, eval)
                currBeta = minOf(currBeta, eval)
                if (currBeta <= currAlpha) break
            }
            return minEval
        }
    }

    private fun orderMoves(moves: List<Move>, pvMove: Move? = null): List<Move> {
        return moves.sortedByDescending { move ->
            var score = 0

            if (pvMove != null && move.from == pvMove.from && move.to == pvMove.to) {
                score += 1000000
            }

            if (move.promotion != null) {
                score += move.promotion.value + 20000
            }

            if (move.capturedPiece != null) {
                score += 10 * move.capturedPiece.type.value - move.piece.type.value + 10000
            }

            val r = move.to.row
            val c = move.to.col
            if ((r in 2..5) && (c in 2..5)) {
                score += 150
            }

            score
        }
    }

    private fun evaluateBoard(board: ChessBoard): Int {
        var score = 0

        var whiteBishops = 0
        var blackBishops = 0
        var totalNonPawnMaterial = 0

        val whitePawnColumns = IntArray(8)
        val blackPawnColumns = IntArray(8)

        for (r in 0..7) {
            for (c in 0..7) {
                val piece = board.getPiece(r, c) ?: continue
                var pieceVal = piece.type.value

                if (piece.type != PieceType.PAWN && piece.type != PieceType.KING) {
                    totalNonPawnMaterial += pieceVal
                }

                if (piece.type == PieceType.BISHOP) {
                    if (piece.color == PieceColor.WHITE) whiteBishops++ else blackBishops++
                }

                if (piece.type == PieceType.PAWN) {
                    if (piece.color == PieceColor.WHITE) whitePawnColumns[c]++ else blackPawnColumns[c]++
                }

                val tableRow = if (piece.color == PieceColor.WHITE) r else 7 - r
                when (piece.type) {
                    PieceType.PAWN -> pieceVal += pawnTable[tableRow][c]
                    PieceType.KNIGHT -> pieceVal += knightTable[tableRow][c]
                    PieceType.BISHOP -> pieceVal += bishopTable[tableRow][c]
                    PieceType.ROOK -> pieceVal += rookTable[tableRow][c]
                    PieceType.QUEEN -> pieceVal += queenTable[tableRow][c]
                    PieceType.KING -> {
                        val isEndgame = totalNonPawnMaterial < 1500
                        pieceVal += if (isEndgame) kingEndgameTable[tableRow][c] else kingMiddlegameTable[tableRow][c]
                    }
                }

                if (piece.color == aiColor) {
                    score += pieceVal
                } else {
                    score -= pieceVal
                }
            }
        }

        val isAiWhite = (aiColor == PieceColor.WHITE)
        val aiBishops = if (isAiWhite) whiteBishops else blackBishops
        val oppBishops = if (isAiWhite) blackBishops else whiteBishops

        if (aiBishops >= 2) score += 35
        if (oppBishops >= 2) score -= 35

        val aiPawns = if (isAiWhite) whitePawnColumns else blackPawnColumns
        val oppPawns = if (isAiWhite) blackPawnColumns else whitePawnColumns

        for (c in 0..7) {
            if (aiPawns[c] > 1) score -= (aiPawns[c] - 1) * 15
            if (oppPawns[c] > 1) score += (oppPawns[c] - 1) * 15
        }

        return score
    }

    private fun computeHash(board: ChessBoard, currentTurn: PieceColor): Long {
        var hash = 0L
        for (r in 0..7) {
            for (c in 0..7) {
                val piece = board.getPiece(r, c) ?: continue
                val sq = (r shl 3) or c
                val pIdx = when (piece.type) {
                    PieceType.PAWN -> 0
                    PieceType.KNIGHT -> 1
                    PieceType.BISHOP -> 2
                    PieceType.ROOK -> 3
                    PieceType.QUEEN -> 4
                    PieceType.KING -> 5
                } + (if (piece.color == PieceColor.WHITE) 0 else 6)

                hash = hash xor zobristPieces[sq][pIdx]
            }
        }
        if (currentTurn == PieceColor.WHITE) {
            hash = hash xor zobristWhiteTurn
        }
        return hash
    }
}
