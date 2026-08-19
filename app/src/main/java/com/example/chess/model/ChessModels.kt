package com.example.chess.model

enum class PieceType(val symbolWhite: String, val symbolBlack: String, val value: Int, val pointValue: Int, val displayNameVi: String) {
    PAWN("♙", "♟", 100, 1, "Tốt"),
    KNIGHT("♘", "♞", 320, 3, "Mã"),
    BISHOP("♗", "♝", 330, 3, "Tượng"),
    ROOK("♖", "♜", 500, 5, "Xe"),
    QUEEN("♕", "♛", 900, 9, "Hậu"),
    KING("♔", "♚", 20000, 0, "Vua")
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

    companion object {
        fun fromAlgebraic(s: String): Position? {
            if (s.length < 2) return null
            val col = s[0] - 'a'
            val row = 8 - s[1].digitToInt()
            return if (col in 0..7 && row in 0..7) Position(row, col) else null
        }
    }
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
    DRAW,
    RESIGNED
}

enum class AppScreen {
    SETUP,
    GAME,
    PUZZLE
}

enum class BoardViewMode {
    VIEW_2D,
    VIEW_3D
}

enum class NavigationTarget {
    SETUP,
    MENU
}

enum class GameMode(val displayNameVi: String, val subtitleVi: String) {
    VS_AI("Đấu Với Máy AI", "1 Người chơi đấu với máy"),
    TWO_PLAYERS("2 Người Chơi", "Chơi 2 người trên cùng 1 máy"),
    PUZZLE("Giải Đố", "Thử thách chiếu bí trong X nước"),
    ONE_MOVE("1 Nước Đi", "Thử thách chiếu bí trong 1 nước"),
    TUTORIAL("Hướng Dẫn", "Học cách đi cờ 6 quân")
}

enum class SideOption(val displayNameVi: String, val subtitleVi: String, val iconSymbol: String) {
    WHITE("Bạch Vương (Trắng)", "Đi trước - Chủ động tấn công", "♔"),
    RANDOM("Ngẫu Nhiên", "Hệ thống tự chọn Trắng hoặc Đen", "🎲"),
    BLACK("Hắc Vương (Đen)", "Đi sau - Phòng thủ phản công", "♚")
}

enum class GameTimerOption(val displayNameVi: String, val minutes: Int?) {
    NONE("None", null),
    M10("10p", 10),
    M15("15p", 15),
    M30("30p", 30),
    CUSTOM("Custom", -1)
}

enum class DifficultyLevel(val level: Int, val displayNameVi: String) {
    LEVEL_1(1, "Dễ"),
    LEVEL_2(2, "T.Bình"),
    LEVEL_3(3, "Khó 3"),
    LEVEL_4(4, "Khó 4"),
    LEVEL_5(5, "Khó 5"),
    LEVEL_6(6, "Khó 6"),
    LEVEL_7(7, "Khó 7");

    companion object {
        fun fromInt(level: Int): DifficultyLevel {
            return entries.find { it.level == level } ?: LEVEL_2
        }
    }
}

data class ChessTheme(
    val name: String,
    val lightSquareColor: Long,
    val darkSquareColor: Long,
    val displayName: String
) {
    companion object {
        val CLASSIC = ChessTheme("Classic", 0xFFEBECD0, 0xFF779556, "Cổ điển")
        val WOOD = ChessTheme("Wood", 0xFFDDB88C, 0xFFA06F3E, "Gỗ")
        val BLUE = ChessTheme("Blue", 0xFFDEE3E6, 0xFF8CA2AD, "Xanh biển")
        val DARK = ChessTheme("Dark", 0xFF707070, 0xFF303030, "Tối")
        val SAND = ChessTheme("Sand", 0xFFE4C16F, 0xFFB88B4A, "Cát")
        val ROYAL = ChessTheme("Royal", 0xFFE8D19F, 0xFF1F1F1F, "Hoàng Gia")
        val MARBLE = ChessTheme("Marble", 0xFFF0F0F0, 0xFF708090, "Cẩm Thạch")
        val MIDNIGHT = ChessTheme("Midnight", 0xFF2C3E50, 0xFF000000, "Bóng Đêm")
        val FIRE = ChessTheme("Fire", 0xFFFFEAEA, 0xFFAA0000, "Đỏ cháy")
        val SKY = ChessTheme("Sky", 0xFFE0F7FA, 0xFF0288D1, "Xanh trời")
        val MYSTIC = ChessTheme("Mystic", 0xFFF3E5F5, 0xFF4A148C, "Tím huyền bí")

        val themes = listOf(CLASSIC, WOOD, BLUE, DARK, SAND, ROYAL, MARBLE, MIDNIGHT, FIRE, SKY, MYSTIC)

        fun fromName(name: String): ChessTheme {
            return themes.find { it.name == name } ?: CLASSIC
        }
    }
}
