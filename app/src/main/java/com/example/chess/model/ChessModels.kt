package com.example.chess.model

enum class PieceType(val symbolWhite: String, val symbolBlack: String, val value: Int, val displayNameVi: String) {
    PAWN("♙", "♟", 100, "Tốt"),
    KNIGHT("♘", "♞", 320, "Mã"),
    BISHOP("♗", "♝", 330, "Tượng"),
    ROOK("♖", "♜", 500, "Xe"),
    QUEEN("♕", "♛", 900, "Hậu"),
    KING("♔", "♚", 20000, "Vua")
}

enum class PieceColor(val displayNameVi: String) {
    WHITE("Quân Trắng"),
    BLACK("Quân Đen");

    val opposite: PieceColor
        get() = if (this == WHITE) BLACK else WHITE
}

data class Piece(
    val type: PieceType,
    val color: PieceColor
) {
    val symbol: String
        get() = if (color == PieceColor.WHITE) type.symbolWhite else type.symbolBlack
}

data class Position(
    val row: Int,
    val col: Int
) {
    val isValid: Boolean
        get() = row in 0..7 && col in 0..7

    val algebraic: String
        get() = "${'a' + col}${8 - row}"
}

data class Move(
    val from: Position,
    val to: Position,
    val piece: Piece,
    val capturedPiece: Piece? = null,
    val promotion: PieceType? = null,
    val isCastling: Boolean = false,
    val isEnPassant: Boolean = false
)

enum class GameStatus {
    NOT_STARTED,
    IN_PROGRESS,
    CHECKMATE,
    STALEMATE,
    RESIGNED
}
