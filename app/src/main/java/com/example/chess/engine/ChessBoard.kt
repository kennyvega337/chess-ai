package com.example.chess.engine

import com.example.chess.model.Move
import com.example.chess.model.Piece
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.model.Position

class ChessBoard(initialize: Boolean = true) {
    private val board: Array<Array<Piece?>> = Array(8) { Array(8) { null } }
    
    // Track castling eligibility
    var whiteKingMoved = false
    var whiteRookKingsideMoved = false
    var whiteRookQueensideMoved = false
    var blackKingMoved = false
    var blackRookKingsideMoved = false
    var blackRookQueensideMoved = false

    // Track En Passant target square (the square behind the pawn that just moved 2 squares)
    var enPassantTarget: Position? = null

    init {
        if (initialize) {
            setupInitialBoard()
        }
    }

    fun copy(): ChessBoard {
        val newBoard = ChessBoard(initialize = false)
        for (r in 0..7) {
            for (c in 0..7) {
                newBoard.board[r][c] = this.board[r][c]
            }
        }
        newBoard.whiteKingMoved = this.whiteKingMoved
        newBoard.whiteRookKingsideMoved = this.whiteRookKingsideMoved
        newBoard.whiteRookQueensideMoved = this.whiteRookQueensideMoved
        newBoard.blackKingMoved = this.blackKingMoved
        newBoard.blackRookKingsideMoved = this.blackRookKingsideMoved
        newBoard.blackRookQueensideMoved = this.blackRookQueensideMoved
        newBoard.enPassantTarget = this.enPassantTarget
        return newBoard
    }

    fun setupInitialBoard() {
        for (r in 0..7) {
            for (c in 0..7) {
                board[r][c] = null
            }
        }

        // Black pieces (row 0 and 1)
        board[0][0] = Piece(PieceType.ROOK, PieceColor.BLACK)
        board[0][1] = Piece(PieceType.KNIGHT, PieceColor.BLACK)
        board[0][2] = Piece(PieceType.BISHOP, PieceColor.BLACK)
        board[0][3] = Piece(PieceType.QUEEN, PieceColor.BLACK)
        board[0][4] = Piece(PieceType.KING, PieceColor.BLACK)
        board[0][5] = Piece(PieceType.BISHOP, PieceColor.BLACK)
        board[0][6] = Piece(PieceType.KNIGHT, PieceColor.BLACK)
        board[0][7] = Piece(PieceType.ROOK, PieceColor.BLACK)
        for (c in 0..7) {
            board[1][c] = Piece(PieceType.PAWN, PieceColor.BLACK)
        }

        // White pieces (row 6 and 7)
        for (c in 0..7) {
            board[6][c] = Piece(PieceType.PAWN, PieceColor.WHITE)
        }
        board[7][0] = Piece(PieceType.ROOK, PieceColor.WHITE)
        board[7][1] = Piece(PieceType.KNIGHT, PieceColor.WHITE)
        board[7][2] = Piece(PieceType.BISHOP, PieceColor.WHITE)
        board[7][3] = Piece(PieceType.QUEEN, PieceColor.WHITE)
        board[7][4] = Piece(PieceType.KING, PieceColor.WHITE)
        board[7][5] = Piece(PieceType.BISHOP, PieceColor.WHITE)
        board[7][6] = Piece(PieceType.KNIGHT, PieceColor.WHITE)
        board[7][7] = Piece(PieceType.ROOK, PieceColor.WHITE)

        whiteKingMoved = false
        whiteRookKingsideMoved = false
        whiteRookQueensideMoved = false
        blackKingMoved = false
        blackRookKingsideMoved = false
        blackRookQueensideMoved = false
        enPassantTarget = null
    }

    fun getPiece(pos: Position): Piece? {
        if (!pos.isValid) return null
        return board[pos.row][pos.col]
    }

    fun getPiece(row: Int, col: Int): Piece? {
        if (row !in 0..7 || col !in 0..7) return null
        return board[row][col]
    }

    fun setPiece(pos: Position, piece: Piece?) {
        if (pos.isValid) {
            board[pos.row][pos.col] = piece
        }
    }

    fun setPiece(row: Int, col: Int, piece: Piece?) {
        if (row in 0..7 && col in 0..7) {
            board[row][col] = piece
        }
    }

    fun applyMove(move: Move) {
        val piece = move.piece
        
        // Remove from old square
        board[move.from.row][move.from.col] = null

        // Handle promotion
        val finalPiece = if (move.promotion != null) {
            Piece(move.promotion, piece.color)
        } else {
            piece
        }

        // Place at target
        board[move.to.row][move.to.col] = finalPiece

        // Handle En Passant capture (remove the captured pawn from its special position)
        if (move.isEnPassant) {
            // The captured pawn is on the same row as 'from' and same column as 'to'
            board[move.from.row][move.to.col] = null
        }

        // Castling rook movement
        if (move.isCastling) {
            if (move.to.col == 6) { // Kingside
                val rook = board[move.from.row][7]
                board[move.from.row][7] = null
                board[move.from.row][5] = rook
            } else if (move.to.col == 2) { // Queenside
                val rook = board[move.from.row][0]
                board[move.from.row][0] = null
                board[move.from.row][3] = rook
            }
        }

        // Update En Passant target square for the NEXT move
        // It's set only if a pawn moves 2 squares
        enPassantTarget = if (piece.type == PieceType.PAWN && Math.abs(move.to.row - move.from.row) == 2) {
            Position((move.from.row + move.to.row) / 2, move.from.col)
        } else {
            null
        }

        // Update movement flags
        if (piece.type == PieceType.KING) {
            if (piece.color == PieceColor.WHITE) whiteKingMoved = true
            else blackKingMoved = true
        } else if (piece.type == PieceType.ROOK) {
            if (piece.color == PieceColor.WHITE) {
                if (move.from.row == 7 && move.from.col == 7) whiteRookKingsideMoved = true
                if (move.from.row == 7 && move.from.col == 0) whiteRookQueensideMoved = true
            } else {
                if (move.from.row == 0 && move.from.col == 7) blackRookKingsideMoved = true
                if (move.from.row == 0 && move.from.col == 0) blackRookQueensideMoved = true
            }
        }

        // IMPORTANT: If a Rook is captured, it should also be marked as "moved"
        // so that castling is no longer possible for that side.
        val captured = move.capturedPiece
        if (captured != null && captured.type == PieceType.ROOK) {
            if (move.to.row == 7 && move.to.col == 7) whiteRookKingsideMoved = true
            if (move.to.row == 7 && move.to.col == 0) whiteRookQueensideMoved = true
            if (move.to.row == 0 && move.to.col == 7) blackRookKingsideMoved = true
            if (move.to.row == 0 && move.to.col == 0) blackRookQueensideMoved = true
        }
    }

    fun getPseudoLegalMoves(pos: Position): List<Move> {
        val piece = getPiece(pos) ?: return emptyList()
        val moves = mutableListOf<Move>()

        when (piece.type) {
            PieceType.PAWN -> getPawnMoves(pos, piece, moves)
            PieceType.KNIGHT -> getKnightMoves(pos, piece, moves)
            PieceType.BISHOP -> getSlidingMoves(pos, piece, arrayOf(Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1)), moves)
            PieceType.ROOK -> getSlidingMoves(pos, piece, arrayOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1)), moves)
            PieceType.QUEEN -> getSlidingMoves(pos, piece, arrayOf(
                Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1),
                Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1)
            ), moves)
            PieceType.KING -> getKingMoves(pos, piece, moves)
        }

        return moves
    }

    private fun getPawnMoves(pos: Position, piece: Piece, moves: MutableList<Move>) {
        val direction = if (piece.color == PieceColor.WHITE) -1 else 1
        val startRow = if (piece.color == PieceColor.WHITE) 6 else 1
        val promotionRow = if (piece.color == PieceColor.WHITE) 0 else 7

        // 1 step forward
        val oneStepRow = pos.row + direction
        if (oneStepRow in 0..7 && getPiece(oneStepRow, pos.col) == null) {
            val target = Position(oneStepRow, pos.col)
            if (oneStepRow == promotionRow) {
                addPromotionMoves(pos, target, piece, null, moves)
            } else {
                moves.add(Move(pos, target, piece))
                
                // 2 steps forward from start
                val twoStepRow = pos.row + (2 * direction)
                if (pos.row == startRow && getPiece(twoStepRow, pos.col) == null) {
                    moves.add(Move(pos, Position(twoStepRow, pos.col), piece))
                }
            }
        }

        // Diagonal captures
        val captureCols = intArrayOf(pos.col - 1, pos.col + 1)
        for (c in captureCols) {
            if (c in 0..7) {
                val target = Position(oneStepRow, c)
                val targetPiece = getPiece(target)
                
                if (targetPiece != null && targetPiece.color != piece.color) {
                    if (oneStepRow == promotionRow) {
                        addPromotionMoves(pos, target, piece, targetPiece, moves)
                    } else {
                        moves.add(Move(pos, target, piece, targetPiece))
                    }
                } else if (target == enPassantTarget) {
                    // En Passant capture
                    val capturedPawn = getPiece(pos.row, c) // The pawn being captured is on the same row as attacker
                    moves.add(Move(pos, target, piece, capturedPawn, isEnPassant = true))
                }
            }
        }
    }

    private fun addPromotionMoves(from: Position, to: Position, piece: Piece, captured: Piece?, moves: MutableList<Move>) {
        val promotions = arrayOf(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT)
        for (promo in promotions) {
            moves.add(Move(from, to, piece, captured, promotion = promo))
        }
    }

    private fun getKnightMoves(pos: Position, piece: Piece, moves: MutableList<Move>) {
        val offsets = arrayOf(
            Pair(-2, -1), Pair(-2, 1), Pair(-1, -2), Pair(-1, 2),
            Pair(1, -2), Pair(1, 2), Pair(2, -1), Pair(2, 1)
        )
        for ((dr, dc) in offsets) {
            val nr = pos.row + dr
            val nc = pos.col + dc
            if (nr in 0..7 && nc in 0..7) {
                val targetPiece = getPiece(nr, nc)
                if (targetPiece == null || targetPiece.color != piece.color) {
                    moves.add(Move(pos, Position(nr, nc), piece, targetPiece))
                }
            }
        }
    }

    private fun getSlidingMoves(pos: Position, piece: Piece, directions: Array<Pair<Int, Int>>, moves: MutableList<Move>) {
        for ((dr, dc) in directions) {
            var nr = pos.row + dr
            var nc = pos.col + dc
            while (nr in 0..7 && nc in 0..7) {
                val targetPiece = getPiece(nr, nc)
                if (targetPiece == null) {
                    moves.add(Move(pos, Position(nr, nc), piece))
                } else {
                    if (targetPiece.color != piece.color) {
                        moves.add(Move(pos, Position(nr, nc), piece, targetPiece))
                    }
                    break // Blocked
                }
                nr += dr
                nc += dc
            }
        }
    }

    private fun getKingMoves(pos: Position, piece: Piece, moves: MutableList<Move>) {
        val offsets = arrayOf(
            Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
            Pair(0, -1),               Pair(0, 1),
            Pair(1, -1),  Pair(1, 0),  Pair(1, 1)
        )
        for ((dr, dc) in offsets) {
            val nr = pos.row + dr
            val nc = pos.col + dc
            if (nr in 0..7 && nc in 0..7) {
                val targetPiece = getPiece(nr, nc)
                if (targetPiece == null || targetPiece.color != piece.color) {
                    moves.add(Move(pos, Position(nr, nc), piece, targetPiece))
                }
            }
        }

        // Castling
        val isWhite = piece.color == PieceColor.WHITE
        val kingMoved = if (isWhite) whiteKingMoved else blackKingMoved
        val rookKingsideMoved = if (isWhite) whiteRookKingsideMoved else blackRookKingsideMoved
        val rookQueensideMoved = if (isWhite) whiteRookQueensideMoved else blackRookQueensideMoved
        val row = if (isWhite) 7 else 0

        if (!kingMoved && !isSquareAttacked(Position(row, 4), piece.color.opposite)) {
            // Kingside castling
            val kingsideRook = getPiece(row, 7)
            if (!rookKingsideMoved && kingsideRook?.type == PieceType.ROOK && kingsideRook.color == piece.color &&
                getPiece(row, 5) == null && getPiece(row, 6) == null) {
                if (!isSquareAttacked(Position(row, 5), piece.color.opposite) && !isSquareAttacked(Position(row, 6), piece.color.opposite)) {
                    moves.add(Move(pos, Position(row, 6), piece, isCastling = true))
                }
            }
            // Queenside castling
            val queensideRook = getPiece(row, 0)
            if (!rookQueensideMoved && queensideRook?.type == PieceType.ROOK && queensideRook.color == piece.color &&
                getPiece(row, 1) == null && getPiece(row, 2) == null && getPiece(row, 3) == null) {
                if (!isSquareAttacked(Position(row, 3), piece.color.opposite) && !isSquareAttacked(Position(row, 2), piece.color.opposite)) {
                    moves.add(Move(pos, Position(row, 2), piece, isCastling = true))
                }
            }
        }
    }

    fun isSquareAttacked(pos: Position, attackerColor: PieceColor): Boolean {
        val r = pos.row
        val c = pos.col

        // 1. Pawn attacks
        val pawnDir = if (attackerColor == PieceColor.WHITE) 1 else -1
        val pawnRow = r + pawnDir
        if (pawnRow in 0..7) {
            if (c - 1 >= 0) {
                val p = board[pawnRow][c - 1]
                if (p != null && p.color == attackerColor && p.type == PieceType.PAWN) return true
            }
            if (c + 1 <= 7) {
                val p = board[pawnRow][c + 1]
                if (p != null && p.color == attackerColor && p.type == PieceType.PAWN) return true
            }
        }

        // 2. Knight attacks
        val knightOffsets = arrayOf(
            Pair(-2, -1), Pair(-2, 1), Pair(-1, -2), Pair(-1, 2),
            Pair(1, -2), Pair(1, 2), Pair(2, -1), Pair(2, 1)
        )
        for ((dr, dc) in knightOffsets) {
            val nr = r + dr
            val nc = c + dc
            if (nr in 0..7 && nc in 0..7) {
                val p = board[nr][nc]
                if (p != null && p.color == attackerColor && p.type == PieceType.KNIGHT) return true
            }
        }

        // 3. Sliding diagonal (Bishop / Queen)
        val diagDirs = arrayOf(Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1))
        for ((dr, dc) in diagDirs) {
            var nr = r + dr
            var nc = c + dc
            while (nr in 0..7 && nc in 0..7) {
                val p = board[nr][nc]
                if (p != null) {
                    if (p.color == attackerColor && (p.type == PieceType.BISHOP || p.type == PieceType.QUEEN)) {
                        return true
                    }
                    break
                }
                nr += dr
                nc += dc
            }
        }

        // 4. Sliding straight (Rook / Queen)
        val straightDirs = arrayOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))
        for ((dr, dc) in straightDirs) {
            var nr = r + dr
            var nc = c + dc
            while (nr in 0..7 && nc in 0..7) {
                val p = board[nr][nc]
                if (p != null) {
                    if (p.color == attackerColor && (p.type == PieceType.ROOK || p.type == PieceType.QUEEN)) {
                        return true
                    }
                    break
                }
                nr += dr
                nc += dc
            }
        }

        // 5. King attacks
        val kingOffsets = arrayOf(
            Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
            Pair(0, -1),               Pair(0, 1),
            Pair(1, -1),  Pair(1, 0),  Pair(1, 1)
        )
        for ((dr, dc) in kingOffsets) {
            val nr = r + dr
            val nc = c + dc
            if (nr in 0..7 && nc in 0..7) {
                val p = board[nr][nc]
                if (p != null && p.color == attackerColor && p.type == PieceType.KING) return true
            }
        }

        return false
    }

    fun findKing(color: PieceColor): Position? {
        for (r in 0..7) {
            for (c in 0..7) {
                val p = board[r][c]
                if (p != null && p.type == PieceType.KING && p.color == color) {
                    return Position(r, c)
                }
            }
        }
        return null
    }

    fun isKingInCheck(color: PieceColor): Boolean {
        val kingPos = findKing(color) ?: return false
        return isSquareAttacked(kingPos, color.opposite)
    }

    fun getCheckingPieces(color: PieceColor): List<Position> {
        val kingPos = findKing(color) ?: return emptyList()
        val attackerColor = color.opposite
        val checkingPieces = mutableListOf<Position>()

        val r = kingPos.row
        val c = kingPos.col

        // 1. Pawn attacks
        val pawnDir = if (attackerColor == PieceColor.WHITE) 1 else -1
        val pawnRow = r + pawnDir
        if (pawnRow in 0..7) {
            if (c - 1 >= 0) {
                val p = board[pawnRow][c - 1]
                if (p != null && p.color == attackerColor && p.type == PieceType.PAWN) checkingPieces.add(Position(pawnRow, c - 1))
            }
            if (c + 1 <= 7) {
                val p = board[pawnRow][c + 1]
                if (p != null && p.color == attackerColor && p.type == PieceType.PAWN) checkingPieces.add(Position(pawnRow, c + 1))
            }
        }

        // 2. Knight attacks
        val knightOffsets = arrayOf(
            Pair(-2, -1), Pair(-2, 1), Pair(-1, -2), Pair(-1, 2),
            Pair(1, -2), Pair(1, 2), Pair(2, -1), Pair(2, 1)
        )
        for ((dr, dc) in knightOffsets) {
            val nr = r + dr
            val nc = c + dc
            if (nr in 0..7 && nc in 0..7) {
                val p = board[nr][nc]
                if (p != null && p.color == attackerColor && p.type == PieceType.KNIGHT) checkingPieces.add(Position(nr, nc))
            }
        }

        // 3. Sliding diagonal (Bishop / Queen)
        val diagDirs = arrayOf(Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1))
        for ((dr, dc) in diagDirs) {
            var nr = r + dr
            var nc = c + dc
            while (nr in 0..7 && nc in 0..7) {
                val p = board[nr][nc]
                if (p != null) {
                    if (p.color == attackerColor && (p.type == PieceType.BISHOP || p.type == PieceType.QUEEN)) {
                        checkingPieces.add(Position(nr, nc))
                    }
                    break
                }
                nr += dr
                nc += dc
            }
        }

        // 4. Sliding straight (Rook / Queen)
        val straightDirs = arrayOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))
        for ((dr, dc) in straightDirs) {
            var nr = r + dr
            var nc = c + dc
            while (nr in 0..7 && nc in 0..7) {
                val p = board[nr][nc]
                if (p != null) {
                    if (p.color == attackerColor && (p.type == PieceType.ROOK || p.type == PieceType.QUEEN)) {
                        checkingPieces.add(Position(nr, nc))
                    }
                    break
                }
                nr += dr
                nc += dc
            }
        }

        return checkingPieces
    }

    fun getLegalMoves(color: PieceColor): List<Move> {
        val allLegal = mutableListOf<Move>()
        for (r in 0..7) {
            for (c in 0..7) {
                val p = board[r][c]
                if (p != null && p.color == color) {
                    val pos = Position(r, c)
                    val pseudo = getPseudoLegalMoves(pos)
                    for (m in pseudo) {
                        val testBoard = this.copy()
                        testBoard.applyMove(m)
                        if (!testBoard.isKingInCheck(color)) {
                            allLegal.add(m)
                        }
                    }
                }
            }
        }
        return allLegal
    }

    fun getLegalMovesForPosition(pos: Position): List<Move> {
        val piece = getPiece(pos) ?: return emptyList()
        val pseudo = getPseudoLegalMoves(pos)
        val legal = mutableListOf<Move>()
        for (m in pseudo) {
            val testBoard = this.copy()
            testBoard.applyMove(m)
            if (!testBoard.isKingInCheck(piece.color)) {
                legal.add(m)
            }
        }
        return legal
    }

    /**
     * Set board state from a FEN string.
     * Note: This is a simplified implementation primarily for piece positions.
     */
    fun loadFromFen(fen: String) {
        // Clear board
        for (r in 0..7) {
            for (c in 0..7) {
                board[r][c] = null
            }
        }

        val parts = fen.split(" ")
        val rows = parts[0].split("/")

        for (r in 0 until 8) {
            var c = 0
            for (char in rows[r]) {
                if (char.isDigit()) {
                    c += char.digitToInt()
                } else {
                    val color = if (char.isUpperCase()) PieceColor.WHITE else PieceColor.BLACK
                    val type = when (char.lowercaseChar()) {
                        'p' -> PieceType.PAWN
                        'n' -> PieceType.KNIGHT
                        'b' -> PieceType.BISHOP
                        'r' -> PieceType.ROOK
                        'q' -> PieceType.QUEEN
                        'k' -> PieceType.KING
                        else -> PieceType.PAWN
                    }
                    board[r][c] = Piece(type, color)
                    c++
                }
            }
        }

        // Active color (simplified)
        // val activeColor = if (parts.size > 1 && parts[1] == "w") PieceColor.WHITE else PieceColor.BLACK

        // Castling rights
        if (parts.size > 2) {
            val castling = parts[2]
            whiteKingMoved = !castling.contains("K") && !castling.contains("Q")
            whiteRookKingsideMoved = !castling.contains("K")
            whiteRookQueensideMoved = !castling.contains("Q")
            blackKingMoved = !castling.contains("k") && !castling.contains("q")
            blackRookKingsideMoved = !castling.contains("k")
            blackRookQueensideMoved = !castling.contains("q")
        }

        // En passant
        if (parts.size > 3 && parts[3] != "-") {
            enPassantTarget = Position.fromAlgebraic(parts[3])
        } else {
            enPassantTarget = null
        }
    }

    /**
     * Generates a Forsyth-Edwards Notation (FEN) string for the current board state.
     * Required for communication with external engines like Stockfish.
     */
    fun toFen(currentTurnColor: PieceColor): String {
        val fen = StringBuilder()
        
        // 1. Piece placement
        for (r in 0..7) {
            var emptySquares = 0
            for (c in 0..7) {
                val piece = board[r][c]
                if (piece == null) {
                    emptySquares++
                } else {
                    if (emptySquares > 0) {
                        fen.append(emptySquares)
                        emptySquares = 0
                    }
                    val char = when (piece.type) {
                        PieceType.PAWN -> 'p'
                        PieceType.KNIGHT -> 'n'
                        PieceType.BISHOP -> 'b'
                        PieceType.ROOK -> 'r'
                        PieceType.QUEEN -> 'q'
                        PieceType.KING -> 'k'
                    }
                    fen.append(if (piece.color == PieceColor.WHITE) char.uppercaseChar() else char)
                }
            }
            if (emptySquares > 0) {
                fen.append(emptySquares)
            }
            if (r < 7) {
                fen.append('/')
            }
        }
        
        // 2. Active color
        fen.append(if (currentTurnColor == PieceColor.WHITE) " w " else " b ")
        
        // 3. Castling availability
        var castling = ""
        if (!whiteKingMoved) {
            if (!whiteRookKingsideMoved) castling += "K"
            if (!whiteRookQueensideMoved) castling += "Q"
        }
        if (!blackKingMoved) {
            if (!blackRookKingsideMoved) castling += "k"
            if (!blackRookQueensideMoved) castling += "q"
        }
        fen.append(if (castling.isEmpty()) "-" else castling)
        
        // 4. En passant target square
        fen.append(" ")
        if (enPassantTarget == null) {
            fen.append("-")
        } else {
            fen.append(enPassantTarget!!.algebraic)
        }
        fen.append(" ")
        
        // 5. Halfmove clock and 6. Fullmove number (simplified)
        fen.append("0 1")
        
        return fen.toString()
    }

    /**
     * Checks if a specific color side has sufficient material to win the game.
     * Insufficient material scenarios (as per standard chess rules simplified):
     * - Only King
     * - King + Knight
     * - King + Bishop
     */
    fun hasInsufficientMatingMaterial(color: PieceColor): Boolean {
        val pieces = mutableListOf<Piece>()
        for (r in 0..7) {
            for (c in 0..7) {
                board[r][c]?.let { 
                    if (it.color == color) pieces.add(it)
                }
            }
        }

        // Side has only King
        if (pieces.size <= 1) return true

        // Side has exactly two pieces: King + (Knight or Bishop)
        if (pieces.size == 2) {
            val nonKing = pieces.find { it.type != PieceType.KING }
            if (nonKing?.type == PieceType.KNIGHT || nonKing?.type == PieceType.BISHOP) {
                return true
            }
        }

        return false
    }

    /**
     * Checks for basic insufficient material scenarios:
     * - King vs King
     * - King + Knight vs King
     * - King + Bishop vs King
     * - (Simplified) does not check for same-color bishops.
     */
    fun hasInsufficientMaterial(): Boolean {
        val allPieces = mutableListOf<Piece>()
        for (r in 0..7) {
            for (c in 0..7) {
                board[r][c]?.let { allPieces.add(it) }
            }
        }

        if (allPieces.size <= 2) return true // King vs King

        if (allPieces.size == 3) {
            val nonKing = allPieces.find { it.type != PieceType.KING }
            if (nonKing?.type == PieceType.KNIGHT || nonKing?.type == PieceType.BISHOP) {
                return true
            }
        }

        return false
    }

    /**
     * Returns a unique signature for the current piece arrangement and castling rights.
     * Used for threefold repetition detection.
     */
    fun getBoardSignature(): String {
        val sb = StringBuilder()
        for (r in 0..7) {
            for (c in 0..7) {
                val p = board[r][c]
                sb.append(p?.type?.name ?: "E")
                sb.append(p?.color?.name ?: "N")
            }
        }
        sb.append(whiteKingMoved)
        sb.append(blackKingMoved)
        sb.append(whiteRookKingsideMoved)
        sb.append(whiteRookQueensideMoved)
        sb.append(blackRookKingsideMoved)
        sb.append(blackRookQueensideMoved)
        return sb.toString()
    }
}
