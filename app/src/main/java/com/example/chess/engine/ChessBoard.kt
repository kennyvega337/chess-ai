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

        // Update movement flags
        if (piece.type == PieceType.KING) {
            if (piece.color == PieceColor.WHITE) whiteKingMoved = true
            else blackKingMoved = true
        } else if (piece.type == PieceType.ROOK) {
            if (piece.color == PieceColor.WHITE) {
                if (move.from.col == 7) whiteRookKingsideMoved = true
                if (move.from.col == 0) whiteRookQueensideMoved = true
            } else {
                if (move.from.col == 7) blackRookKingsideMoved = true
                if (move.from.col == 0) blackRookQueensideMoved = true
            }
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
                val targetPiece = getPiece(oneStepRow, c)
                if (targetPiece != null && targetPiece.color != piece.color) {
                    val target = Position(oneStepRow, c)
                    if (oneStepRow == promotionRow) {
                        addPromotionMoves(pos, target, piece, targetPiece, moves)
                    } else {
                        moves.add(Move(pos, target, piece, targetPiece))
                    }
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
            if (!rookKingsideMoved && getPiece(row, 5) == null && getPiece(row, 6) == null) {
                if (!isSquareAttacked(Position(row, 5), piece.color.opposite) && !isSquareAttacked(Position(row, 6), piece.color.opposite)) {
                    moves.add(Move(pos, Position(row, 6), piece, isCastling = true))
                }
            }
            // Queenside castling
            if (!rookQueensideMoved && getPiece(row, 1) == null && getPiece(row, 2) == null && getPiece(row, 3) == null) {
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
}
