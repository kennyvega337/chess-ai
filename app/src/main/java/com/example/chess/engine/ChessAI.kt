package com.example.chess.engine

import android.content.Context
import com.example.chess.model.DifficultyLevel
import com.example.chess.model.Move
import com.example.chess.model.Piece
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.model.Position
import kotlin.random.Random

/**
 * Optimized Advanced Chess Engine for Hard AI Difficulty
 */
class ChessAI(private val aiColor: PieceColor, private val context: Context? = null) {

    // ... (rest of the properties)

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

    fun chooseMove(board: ChessBoard, difficulty: DifficultyLevel = DifficultyLevel.LEVEL_2, isScoringMode: Boolean = false): Move? {
        val legalMoves = board.getLegalMoves(aiColor)
        if (legalMoves.isEmpty()) return null

        if (isScoringMode) {
            return chooseScoringMove(board, legalMoves)
        }

        // Cấp độ 7 sử dụng Stockfish (không NNUE) nếu có context
        if (difficulty == DifficultyLevel.LEVEL_7 && context != null) {
            val stockfish = StockfishEngine(context)
            val fen = board.toFen(aiColor)
            val bestUci = stockfish.getBestMove(fen, depth = 14)
            if (bestUci != null) {
                val move = stockfish.parseUciMove(board, bestUci)
                if (move != null) return move
            }
        }

        return when (difficulty) {
            DifficultyLevel.LEVEL_1 -> chooseEasyMove(board, legalMoves)
            DifficultyLevel.LEVEL_2 -> chooseMediumMove(board, legalMoves)
            else -> chooseHardMove(board, legalMoves, maxTargetDepth = difficulty.level)
        }
    }

    private fun chooseScoringMove(board: ChessBoard, legalMoves: List<Move>): Move {
        val userColor = aiColor.opposite

        // Tìm TẤT CẢ quân người chơi (không phải Vua) để tính khoảng cách
        val userPiecePositions = findAllUserPieces(board, userColor)
        if (userPiecePositions.isEmpty()) return legalMoves.random()

        // 1. Luôn ưu tiên ăn quân người chơi nếu có thể (ăn quân gần nhất trước)
        val sortedByCapture = legalMoves
            .filter { it.capturedPiece != null }
            .sortedBy { move -> userPiecePositions.minOf { getManhattanDistance(move.to, it) } }
        if (sortedByCapture.isNotEmpty()) return sortedByCapture.first()

        val aiPieces = board.getPieces(aiColor)
        val isMultiPieceSquad = aiPieces.size >= 4

        // Nếu máy có ít hơn 4 quân: Giữ thuật toán tiến lại gần cơ bản
        if (!isMultiPieceSquad) {
            val minDistPerMove = legalMoves.associateWith { move ->
                userPiecePositions.minOf { getManhattanDistance(move.to, it) }
            }
            val minDist = minDistPerMove.values.min()
            val candidates = legalMoves.filter { minDistPerMove[it] == minDist }
            return candidates.random()
        }

        // =========================================================================
        // KHI CÓ TỪ 4 QUÂN TRỞ LÊN:
        // Thuật toán: "Săn bầy đàn & Áp sát có sơ hở (Hunter-Pack & Vulnerability Window)"
        // Mục tiêu: Áp sát mãnh liệt, tăng độ khó và đe dọa, nhưng tuyệt đối không trốn tìm,
        // luôn mở ra khoảng trống để người chơi có cơ hội ăn quân ghi điểm.
        // =========================================================================

        // Xác định vị trí quân tiên phong (quân AI gần người chơi nhất hiện tại)
        val vanguardPos = aiPieces.minByOrNull { (pos, _) ->
            userPiecePositions.minOf { userPos -> getManhattanDistance(pos, userPos) }
        }?.first

        var bestScore = -999999
        val candidateMoves = mutableListOf<Move>()

        for (move in legalMoves) {
            val targetUserPos = userPiecePositions.minByOrNull { getManhattanDistance(move.to, it) } ?: userPiecePositions.first()
            val newDist = getManhattanDistance(move.to, targetUserPos)
            val oldDist = getManhattanDistance(move.from, targetUserPos)

            // 1. Chặn hành vi chạy trốn (Anti-Coward Constraint)
            // Không cho phép máy tháo chạy xa khỏi người chơi (>= 4 ô) khi có từ 4 quân
            if (newDist > oldDist && newDist >= 4) {
                continue
            }

            val nextBoard = board.copy()
            nextBoard.applyMove(move)

            var score = 0

            // 2. Thưởng áp sát cự ly gần (Proximity Bonus)
            when (newDist) {
                1 -> score += 600
                2 -> score += 400
                3 -> score += 200
                else -> score += (8 - newDist) * 30
            }

            // 3. Thưởng tạo thế đe dọa thật sự (Threat Bonus):
            // Nước đi này có thể ăn quân của người chơi ở lượt sau không?
            val threatensAnyUserPiece = userPiecePositions.any { nextBoard.isSquareAttacked(it, aiColor) }
            if (threatensAnyUserPiece) {
                score += 1000 // Tăng độ khó: Buộc người chơi phải chú ý né hoặc ăn lại ngay!
            }

            // 4. Cơ chế tạo khoảng trống (Vulnerability & Tactical Bait):
            // Quân người chơi có thể ăn được quân AI ở ô đích không?
            val isExposedToUser = nextBoard.isSquareAttacked(move.to, userColor)
            val isProtectedByOtherAi = board.isSquareAttacked(move.to, aiColor)

            if (isExposedToUser) {
                if (isProtectedByOtherAi) {
                    // Bẫy đổi quân: Người chơi ăn được, nhưng quân máy khác bảo kê
                    score += 450
                } else {
                    // Nếu là quân tiên phong: Rất khuyến khích lao vào áp sát làm mồi nhử!
                    if (move.from == vanguardPos) {
                        score += 500 // Tạo khoảng trống ăn điểm rõ ràng cho người chơi
                    } else {
                        score += 150
                    }
                }
            }

            // 5. Thưởng siết vòng vây / khóa ô thoát (Mobility restriction)
            val userLegalMovesCount = nextBoard.getLegalMoves(userColor).size
            score += (25 - userLegalMovesCount).coerceAtLeast(0) * 15

            // 6. Yếu tố ngẫu nhiên nhỏ để các quân không di chuyển rập khuôn
            score += Random.nextInt(0, 50)

            if (score > bestScore) {
                bestScore = score
                candidateMoves.clear()
                candidateMoves.add(move)
            } else if (score == bestScore) {
                candidateMoves.add(move)
            }
        }

        return if (candidateMoves.isNotEmpty()) {
            candidateMoves.random()
        } else {
            legalMoves.minByOrNull { move ->
                userPiecePositions.minOf { getManhattanDistance(move.to, it) }
            } ?: legalMoves.random()
        }
    }

    private fun getManhattanDistance(p1: Position, p2: Position): Int {
        return kotlin.math.abs(p1.row - p2.row) + kotlin.math.abs(p1.col - p2.col)
    }

    private fun findUserPiece(board: ChessBoard, color: PieceColor): Position? {
        for (r in 0..7) {
            for (c in 0..7) {
                val p = board.getPiece(r, c)
                if (p != null && p.color == color && p.type != PieceType.KING) {
                    return Position(r, c)
                }
            }
        }
        for (r in 0..7) {
            for (c in 0..7) {
                val p = board.getPiece(r, c)
                if (p != null && p.color == color) return Position(r, c)
            }
        }
        return null
    }

    private fun findAllUserPieces(board: ChessBoard, color: PieceColor): List<Position> {
        val positions = mutableListOf<Position>()
        for (r in 0..7) {
            for (c in 0..7) {
                val p = board.getPiece(r, c)
                if (p != null && p.color == color && p.type != PieceType.KING) {
                    positions.add(Position(r, c))
                }
            }
        }
        if (positions.isEmpty()) {
            for (r in 0..7) {
                for (c in 0..7) {
                    val p = board.getPiece(r, c)
                    if (p != null && p.color == color) {
                        positions.add(Position(r, c))
                    }
                }
            }
        }
        return positions
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
    private fun chooseHardMove(board: ChessBoard, legalMoves: List<Move>, maxTargetDepth: Int): Move {
        transpositionTable.clear()

        val moveCandidates = orderMoves(legalMoves).toMutableList()
        var overallBestMove: Move = moveCandidates.first()

        val startTime = System.currentTimeMillis()
        val maxTimeMs = 1500L // Slightly more time for deeper levels
        val timeDeadline = startTime + maxTimeMs

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
