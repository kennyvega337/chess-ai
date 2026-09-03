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
    PUZZLE,
    SCORING
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
    TUTORIAL("Hướng Dẫn", "Học cách đi cờ 6 quân"),
    SPECIAL_MOVE("Nước Đi Đặc Biệt", "Học các quy tắc đặc biệt"),
    SCORING("Ghi Điểm", "Thử thách ghi điểm tối đa")
}

enum class SpecialTutorialType(val displayNameVi: String, val description: String) {
    CASTLING_KINGSIDE("Vua nhập thành", "Nhập thành phía cánh Vua (Castling - King side)"),
    CASTLING_QUEENSIDE("Hậu nhập thành", "Nhập thành phía cánh Hậu (Castling - Queen side)"),
    PAWN_PROMOTION("Phong cấp Tốt", "Khi Tốt đi đến hàng cuối cùng (Pawn Promotion)"),
    EN_PASSANT("Bắt tốt qua đường", "Bắt Tốt của đối phương khi vừa tiến 2 ô (En Passant)")
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
    val displayName: String,

    // Bàn cờ
    val lightSquareColor: Long,
    val darkSquareColor: Long,

    // Giao diện (App Shell) - ĐÃ CHỈNH SỬA
    val backgroundColors: List<Long>,
    val surfaceColor: Long,
    val accentColor: Long,
    val onAccentColor: Long = 0xFFFFFFFF,

    val textColor: Long,
    val secondaryTextColor: Long,

    val borderColor: Long,
    val dividerColor: Long,

    val iconColor: Long,
    val iconActiveColor: Long,

    // Trạng thái bàn cờ
    val selectedSquareColor: Long,
    val lastMoveColor: Long,
    val legalMoveColor: Long,
    val captureColor: Long,
    val checkColor: Long,

    // Nút bấm
    val buttonColor: Long,
    val buttonPressedColor: Long,

    val isDarkStatusBarIcons: Boolean

) {
    companion object {

        // ============================================================
        // 1. CLASSIC - CỔ ĐIỂN (Background ấm áp, sáng tự nhiên)
        // ============================================================
        val CLASSIC = ChessTheme(
            name = "Classic",
            displayName = "Cổ điển",
            lightSquareColor = 0xFFEBE3D5,
            darkSquareColor = 0xFF6B8A5E,

            backgroundColors = listOf(0xFFE8DDD0, 0xFFD5C8B8),
            surfaceColor = 0xFFF0E8DC,
            accentColor = 0xFF7CAE5A,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFF2C2824,
            secondaryTextColor = 0xFF6A655E,

            borderColor = 0xFFC8BDB0,
            dividerColor = 0xFFD5C8BC,

            iconColor = 0xFF6A655E,
            iconActiveColor = 0xFF7CAE5A,

            // 🔥 Đã chỉnh: Tăng độ tương phản, không trùng màu bàn cờ
            selectedSquareColor = 0x60FFD54F,    // Vàng sáng
            lastMoveColor = 0x50FFAB91,          // Cam nhạt
            legalMoveColor = 0xFFFFFFFF,         // Trắng trong suốt
            captureColor = 0x40FF5252,           // Đỏ tươi
            checkColor = 0xCCD32F2F,             // Đỏ đậm

            buttonColor = 0xFF7CAE5A,
            buttonPressedColor = 0xFF5E8A42,
            isDarkStatusBarIcons = true
        )

        // ============================================================
        // 2. DARK - TỐI GIẢN (Xám sáng thanh lịch)
        // ============================================================
        val DARK = ChessTheme(
            name = "Dark",
            displayName = "Tối giản",
            lightSquareColor = 0xFFA8BCC4,
            darkSquareColor = 0xFF5A7A8A,

            backgroundColors = listOf(0xFFD5D8DC, 0xFFC4C8CC),
            surfaceColor = 0xFFE0E4E8,
            accentColor = 0xFF5A9AD9,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFF1C2024,
            secondaryTextColor = 0xFF5A646E,

            borderColor = 0xFFB0B8C0,
            dividerColor = 0xFFC0C8D0,

            iconColor = 0xFF5A646E,
            iconActiveColor = 0xFF5A9AD9,

            // 🔥 Đã chỉnh: Dùng cam/vàng để tương phản với xanh
            selectedSquareColor = 0x60FFD740,    // Vàng
            lastMoveColor = 0x50FFAB91,          // Cam nhạt
            legalMoveColor = 0xFFFFFFFF,         // Trắng
            captureColor = 0x40FF5252,           // Đỏ
            checkColor = 0xCCD32F2F,             // Đỏ đậm

            buttonColor = 0xFF4A90C9,
            buttonPressedColor = 0xFF3A80B5,
            isDarkStatusBarIcons = true
        )

        // ============================================================
        // 3. MYSTIC - HUYỀN BÍ (Tím pastel nhẹ nhàng)
        // ============================================================
        val MYSTIC = ChessTheme(
            name = "Mystic",
            displayName = "Huyền bí",
            lightSquareColor = 0xFFD8CCE6,
            darkSquareColor = 0xFF7A5A9A,

            backgroundColors = listOf(0xFFE8DCF0, 0xFFDCCCE6),
            surfaceColor = 0xFFF0E8F8,
            accentColor = 0xFF9A7AD6,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFF1C1428,
            secondaryTextColor = 0xFF6A5A7A,

            borderColor = 0xFFD0C0DE,
            dividerColor = 0xFFDCCCE6,

            iconColor = 0xFF6A5A7A,
            iconActiveColor = 0xFF9A7AD6,

            // 🔥 Đã chỉnh: Dùng vàng/trắng cho tương phản với tím
            selectedSquareColor = 0x60FFD740,    // Vàng
            lastMoveColor = 0x50FFE082,          // Vàng nhạt
            legalMoveColor = 0xFFFFFFFF,         // Trắng
            captureColor = 0x40FF5252,           // Đỏ
            checkColor = 0xCCD32F2F,             // Đỏ đậm

            buttonColor = 0xFF9A7AD6,
            buttonPressedColor = 0xFF7A5AB5,
            isDarkStatusBarIcons = true
        )

        // ============================================================
        // 4. FIRE - ĐỎ ĐẤT (Kem ấm áp, nhẹ nhàng)
        // ============================================================
        val FIRE = ChessTheme(
            name = "Fire",
            displayName = "Đỏ đất",
            lightSquareColor = 0xFFEAD0C8,
            darkSquareColor = 0xFF9A4A3E,

            backgroundColors = listOf(0xFFF0E0D8, 0xFFE8D0C8),
            surfaceColor = 0xFFF8ECE4,
            accentColor = 0xFFD05A4A,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFF1C0E0C,
            secondaryTextColor = 0xFF6A4A42,

            borderColor = 0xFFDCC0B8,
            dividerColor = 0xFFE8D0C8,

            iconColor = 0xFF6A4A42,
            iconActiveColor = 0xFFD05A4A,

            // 🔥 Đã chỉnh: Dùng xanh/xanh dương để tương phản với đỏ
            selectedSquareColor = 0x6044D4A0,    // Xanh ngọc
            lastMoveColor = 0x504AD4D4,          // Xanh cyan
            legalMoveColor = 0xFFFFFFFF,         // Trắng
            captureColor = 0x40FF1744,           // Đỏ hồng
            checkColor = 0xCCD50000,             // Đỏ

            buttonColor = 0xFFD05A4A,
            buttonPressedColor = 0xFFB03A2E,
            isDarkStatusBarIcons = true
        )

        // ============================================================
        // 5. CYBER - CÔNG NGHỆ (Xanh dương sáng hiện đại)
        // ============================================================
        val CYBER = ChessTheme(
            name = "Cyber",
            displayName = "Công nghệ",
            lightSquareColor = 0xFF9AB4C8,
            darkSquareColor = 0xFF2A4A62,

            backgroundColors = listOf(0xFFD8E8F0, 0xFFC8D8E4),
            surfaceColor = 0xFFE4F0F8,
            accentColor = 0xFF2AC8D8,
            onAccentColor = 0xFF0A1A22,

            textColor = 0xFF0C1420,
            secondaryTextColor = 0xFF4A6278,

            borderColor = 0xFFB0C8D8,
            dividerColor = 0xFFC4D8E8,

            iconColor = 0xFF4A6278,
            iconActiveColor = 0xFF2AC8D8,

            // 🔥 Đã chỉnh: Dùng cam/vàng cho nổi bật
            selectedSquareColor = 0x60FFAB00,    // Cam đậm
            lastMoveColor = 0x50FFD740,          // Vàng
            legalMoveColor = 0xFFFFFFFF,         // Trắng
            captureColor = 0x40FF5252,           // Đỏ
            checkColor = 0xCCE84A40,             // Cam đỏ

            buttonColor = 0xFF22B0C0,
            buttonPressedColor = 0xFF1A90A0,
            isDarkStatusBarIcons = true
        )

        // ============================================================
        // 6. FOREST - THIÊN NHIÊN (Xanh lá tươi mát)
        // ============================================================
        val FOREST = ChessTheme(
            name = "Forest",
            displayName = "Rừng xanh",
            lightSquareColor = 0xFFE0E8CC,
            darkSquareColor = 0xFF628A56,

            backgroundColors = listOf(0xFFE4ECD8, 0xFFD4DEC8),
            surfaceColor = 0xFFEFF4E4,
            accentColor = 0xFF66AA54,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFF141A10,
            secondaryTextColor = 0xFF4A6240,

            borderColor = 0xFFC4D0B8,
            dividerColor = 0xFFD4DEC8,

            iconColor = 0xFF4A6240,
            iconActiveColor = 0xFF66AA54,

            // 🔥 Đã chỉnh: Dùng vàng cam tương phản với xanh lá
            selectedSquareColor = 0x60FFD740,    // Vàng
            lastMoveColor = 0x50FFAB91,          // Cam nhạt
            legalMoveColor = 0xFFFFFFFF,         // Trắng
            captureColor = 0x40FF5252,           // Đỏ
            checkColor = 0xCCD32F2F,             // Đỏ đậm

            buttonColor = 0xFF66AA54,
            buttonPressedColor = 0xFF4A8A3A,
            isDarkStatusBarIcons = true
        )

        // ============================================================
        // 7. OCEAN - ĐẠI DƯƠNG (Xanh ngọc trong lành)
        // ============================================================
        val OCEAN = ChessTheme(
            name = "Ocean",
            displayName = "Đại dương",
            lightSquareColor = 0xFFD8EEE8,
            darkSquareColor = 0xFF468A96,

            backgroundColors = listOf(0xFFE0F0EC, 0xFFD0E4E0),
            surfaceColor = 0xFFECF8F4,
            accentColor = 0xFF26B8A8,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFF0E1C20,
            secondaryTextColor = 0xFF3A6A72,

            borderColor = 0xFFB8D0CC,
            dividerColor = 0xFFC8DCD8,

            iconColor = 0xFF3A6A72,
            iconActiveColor = 0xFF26B8A8,

            // 🔥 Đã chỉnh: Dùng cam/đào tương phản với xanh ngọc
            selectedSquareColor = 0x60FFAB91,    // Cam nhạt
            lastMoveColor = 0x50FFCCBC,          // Hồng đào
            legalMoveColor = 0xFFFFFFFF,         // Trắng
            captureColor = 0x40FF5252,           // Đỏ
            checkColor = 0xCCD32F2F,             // Đỏ đậm

            buttonColor = 0xFF26B8A8,
            buttonPressedColor = 0xFF1A8A7E,
            isDarkStatusBarIcons = true
        )

        // ============================================================
        // 8. COSMIC - VŨ TRỤ (Tím xanh huyền ảo sáng)
        // ============================================================
        val COSMIC = ChessTheme(
            name = "Cosmic",
            displayName = "Vũ trụ",
            lightSquareColor = 0xFFC8D4EA,
            darkSquareColor = 0xFF50608A,

            backgroundColors = listOf(0xFFE0E4F4, 0xFFD0D4E8),
            surfaceColor = 0xFFECE8F8,
            accentColor = 0xFF6A6EE6,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFF0C1020,
            secondaryTextColor = 0xFF484C72,

            borderColor = 0xFFC0C8DE,
            dividerColor = 0xFFD0D4E8,

            iconColor = 0xFF484C72,
            iconActiveColor = 0xFF6A6EE6,

            // 🔥 Đã chỉnh: Dùng vàng/cam tương phản với tím xanh
            selectedSquareColor = 0x60FFD740,    // Vàng
            lastMoveColor = 0x50FFE082,          // Vàng nhạt
            legalMoveColor = 0xFFFFFFFF,         // Trắng
            captureColor = 0x40FF5252,           // Đỏ
            checkColor = 0xCCD32F2F,             // Đỏ đậm

            buttonColor = 0xFF6A6EE6,
            buttonPressedColor = 0xFF4A4ECC,
            isDarkStatusBarIcons = true
        )

        // ============================================================
        // 9. DESERT - SA MẠC (Vàng cát ấm áp)
        // ============================================================
        val DESERT = ChessTheme(
            name = "Desert",
            displayName = "Sa mạc",
            lightSquareColor = 0xFFEADCC8,
            darkSquareColor = 0xFFB08860,

            backgroundColors = listOf(0xFFECE0D0, 0xFFE0D0BC),
            surfaceColor = 0xFFF4E8D8,
            accentColor = 0xFFCC8844,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFF1A1410,
            secondaryTextColor = 0xFF6A5A44,

            borderColor = 0xFFD4C0B0,
            dividerColor = 0xFFE0D0BC,

            iconColor = 0xFF6A5A44,
            iconActiveColor = 0xFFCC8844,

            // 🔥 Đã chỉnh: Dùng xanh dương tương phản với vàng
            selectedSquareColor = 0x604490D4,    // Xanh dương
            lastMoveColor = 0x505A9AD4,          // Xanh dương nhạt
            legalMoveColor = 0xFFFFFFFF,         // Trắng
            captureColor = 0x40FF5252,           // Đỏ
            checkColor = 0xCCD32F2F,             // Đỏ đậm

            buttonColor = 0xFFCC8844,
            buttonPressedColor = 0xFFB07034,
            isDarkStatusBarIcons = true
        )

        // ============================================================
        // 10. ARCTIC - BĂNG GIÁ (Trắng xanh mát lạnh)
        // ============================================================
        val ARCTIC = ChessTheme(
            name = "Arctic",
            displayName = "Băng giá",
            lightSquareColor = 0xFFE8F0F2,
            darkSquareColor = 0xFF7A9AAA,

            backgroundColors = listOf(0xFFE8F0F4, 0xFFD8E4E8),
            surfaceColor = 0xFFF0F8FC,
            accentColor = 0xFF3AB8E6,
            onAccentColor = 0xFF0A2E48,

            textColor = 0xFF0A1A22,
            secondaryTextColor = 0xFF4A6A78,

            borderColor = 0xFFC0D0D8,
            dividerColor = 0xFFD0DCE0,

            iconColor = 0xFF4A6A78,
            iconActiveColor = 0xFF3AB8E6,

            // 🔥 Đã chỉnh: Dùng cam/đào tạo ấm áp giữa băng giá
            selectedSquareColor = 0x60FFAB91,    // Cam nhạt
            lastMoveColor = 0x50FFCCBC,          // Hồng đào
            legalMoveColor = 0xFFFFFFFF,         // Trắng
            captureColor = 0x40FF5252,           // Đỏ
            checkColor = 0xCCD32F2F,             // Đỏ đậm

            buttonColor = 0xFF3AB8E6,
            buttonPressedColor = 0xFF1A90C8,
            isDarkStatusBarIcons = true
        )

        // ============================================================
        // 11. SAKURA - HOA ANH ĐÀO (Hồng pastel ngọt ngào)
        // ============================================================
        val SAKURA = ChessTheme(
            name = "Sakura",
            displayName = "Anh đào",
            lightSquareColor = 0xFFF8E4E8,
            darkSquareColor = 0xFFB07A88,

            backgroundColors = listOf(0xFFF8E8EC, 0xFFF0DCE0),
            surfaceColor = 0xFFFCF0F4,
            accentColor = 0xFFE8708E,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFF1A0E12,
            secondaryTextColor = 0xFF6A4A54,

            borderColor = 0xFFE0C8D0,
            dividerColor = 0xFFE8D4DC,

            iconColor = 0xFF6A4A54,
            iconActiveColor = 0xFFE8708E,

            // 🔥 Đã chỉnh: Dùng xanh mint tương phản với hồng
            selectedSquareColor = 0x6044D4A0,    // Xanh ngọc
            lastMoveColor = 0x504AD4D4,          // Xanh cyan
            legalMoveColor = 0xFFFFFFFF,         // Trắng
            captureColor = 0x40FF4081,           // Hồng đậm
            checkColor = 0xCCC2185B,             // Hồng tím

            buttonColor = 0xFFE8708E,
            buttonPressedColor = 0xFFCC4A6A,
            isDarkStatusBarIcons = true
        )

        // ============================================================
        // 12. OBSIDIAN - ĐEN KIM LOẠI (Xám sang trọng)
        // ============================================================
        val OBSIDIAN = ChessTheme(
            name = "Obsidian",
            displayName = "Obsidian",
            lightSquareColor = 0xFFB8BCC0,
            darkSquareColor = 0xFF4A4E52,

            backgroundColors = listOf(0xFFD0D2D4, 0xFFC0C2C4),
            surfaceColor = 0xFFDCDEDE,
            accentColor = 0xFFC8A838,
            onAccentColor = 0xFF1A1608,

            textColor = 0xFF0A0A0C,
            secondaryTextColor = 0xFF505054,

            borderColor = 0xFFB0B2B4,
            dividerColor = 0xFFC0C2C4,

            iconColor = 0xFF000000,
            iconActiveColor = 0xFFC8A838,

            // 🔥 Đã chỉnh: Dùng xanh dương tương phản với vàng
            selectedSquareColor = 0x604A90D4,    // Xanh dương
            lastMoveColor = 0x506AAAD4,          // Xanh dương nhạt
            legalMoveColor = 0xFFFFFFFF,         // Trắng
            captureColor = 0x40FF5252,           // Đỏ
            checkColor = 0xCCD32F2F,             // Đỏ đậm

            buttonColor = 0xFFC8A838,
            buttonPressedColor = 0xFFA88828,
            isDarkStatusBarIcons = true
        )

        // ============================================================
        // 13. NIGHT - ĐÊM THANH BÌNH
        // ============================================================
        val NIGHT = ChessTheme(
            name = "Night",
            displayName = "Đêm thanh bình",
            lightSquareColor = 0xFFB8C4D0,
            darkSquareColor = 0xFF3A5068,

            backgroundColors = listOf(0xFF1E242B, 0xFF14191F),
            surfaceColor = 0xFF2A323C,
            accentColor = 0xFF4A90D9,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFFE8ECF0,
            secondaryTextColor = 0xFF8A949E,

            borderColor = 0xFF3A4450,
            dividerColor = 0xFF2E3842,

            iconColor = 0xFFFFFFFF,
            iconActiveColor = 0xFF4A90D9,

            // 🔥 Đã chỉnh: Dùng vàng/cam tạo điểm nhấn
            selectedSquareColor = 0x60FFD740,    // Vàng
            lastMoveColor = 0x50FFAB91,          // Cam nhạt
            legalMoveColor = 0xFFFFFFFF,         // Trắng
            captureColor = 0x40FF5252,           // Đỏ
            checkColor = 0xCCD32F2F,             // Đỏ đậm

            buttonColor = 0xFF4A90D9,
            buttonPressedColor = 0xFF3A7AC9,
            isDarkStatusBarIcons = false
        )

        // ============================================================
        // 14. AMBER - HỔ PHÁCH (Tối ấm áp, vàng ánh đèn)
        // ============================================================
        val AMBER = ChessTheme(
            name = "Amber",
            displayName = "Hổ phách",
            lightSquareColor = 0xFFD4C4A8,
            darkSquareColor = 0xFF7A6548,

            backgroundColors = listOf(0xFF241E18, 0xFF181410),
            surfaceColor = 0xFF342A20,
            accentColor = 0xFFD4A048,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFFF5EDE0,
            secondaryTextColor = 0xFFA89880,

            borderColor = 0xFF4A3C30,
            dividerColor = 0xFF3A3026,

            iconColor = 0xFFFFFFFF,
            iconActiveColor = 0xFFD4A048,

            // 🔥 Đã chỉnh: Dùng xanh dương tương phản với vàng
            selectedSquareColor = 0x604A90D4,    // Xanh dương
            lastMoveColor = 0x506AAAD4,          // Xanh dương nhạt
            legalMoveColor = 0xFFFFFFFF,         // Trắng
            captureColor = 0x40FF5252,           // Đỏ
            checkColor = 0xCCD32F2F,             // Đỏ đậm

            buttonColor = 0xFFD4A048,
            buttonPressedColor = 0xFFB88838,
            isDarkStatusBarIcons = false
        )

        // ============================================================
        // 15. GRAPHITE - THAN CHÌ (Tối trung tính, hiện đại)
        // ============================================================
        val GRAPHITE = ChessTheme(
            name = "Graphite",
            displayName = "Than chì",
            lightSquareColor = 0xFFB8C0C8,
            darkSquareColor = 0xFF4A525A,

            backgroundColors = listOf(0xFF22262A, 0xFF181C20),
            surfaceColor = 0xFF2E3238,
            accentColor = 0xFF6A8A9E,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFFE8ECF0,
            secondaryTextColor = 0xFF88929C,

            borderColor = 0xFF3A4048,
            dividerColor = 0xFF2E343A,

            iconColor = 0xFFFFFFFF,
            iconActiveColor = 0xFF6A8A9E,

            // 🔥 Đã chỉnh: Dùng vàng/cam tạo điểm nhấn
            selectedSquareColor = 0x60FFD740,    // Vàng
            lastMoveColor = 0x50FFAB91,          // Cam nhạt
            legalMoveColor = 0xFFFFFFFF,         // Trắng
            captureColor = 0x40FF5252,           // Đỏ
            checkColor = 0xCCD32F2F,             // Đỏ đậm

            buttonColor = 0xFF6A8A9E,
            buttonPressedColor = 0xFF5A7A8E,
            isDarkStatusBarIcons = false
        )

        // ============================================================
        // 16. BURGUNDY - VANG BORDEAUX (Tối sang trọng, đỏ quý phái)
        // ============================================================
        val BURGUNDY = ChessTheme(
            name = "Burgundy",
            displayName = "Bordeaux",
            lightSquareColor = 0xFFE0C8C8,
            darkSquareColor = 0xFF7A4A50,

            backgroundColors = listOf(0xFF22161A, 0xFF180E12),
            surfaceColor = 0xFF322026,
            accentColor = 0xFFD48A7A,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFFF5EAE8,
            secondaryTextColor = 0xFFA88882,

            borderColor = 0xFF4A3238,
            dividerColor = 0xFF3A262A,

            iconColor = 0xFFFFFFFF,
            iconActiveColor = 0xFFD48A7A,

            // 🔥 Đã chỉnh: Dùng xanh/vàng tương phản với đỏ
            selectedSquareColor = 0x6044D4A0,    // Xanh ngọc
            lastMoveColor = 0x504AD4D4,          // Xanh cyan
            legalMoveColor = 0xFFFFFFFF,         // Trắng
            captureColor = 0x40FF5252,           // Đỏ
            checkColor = 0xCCD32F2F,             // Đỏ đậm

            buttonColor = 0xFFD48A7A,
            buttonPressedColor = 0xFFB86A5A,
            isDarkStatusBarIcons = false
        )

        // ============================================================
        // 17. MIDNIGHT - NỬA ĐÊM
        // ============================================================
        val MIDNIGHT = ChessTheme(
            name = "Midnight",
            displayName = "Nửa đêm",
            lightSquareColor = 0xFFA8BED4,
            darkSquareColor = 0xFF2A4A6A,

            backgroundColors = listOf(0xFF121C2A, 0xFF0A121C),
            surfaceColor = 0xFF1C2A3A,
            accentColor = 0xFF3A9AD4,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFFE8F0F8,
            secondaryTextColor = 0xFF7A92AA,

            borderColor = 0xFF2A4258,
            dividerColor = 0xFF1E3448,

            iconColor = 0xFF7A92AA,
            iconActiveColor = 0xFF3A9AD4,

            // 🔥 Đã chỉnh: Dùng cam/vàng tương phản với xanh đêm
            selectedSquareColor = 0x60FFAB00,    // Cam đậm
            lastMoveColor = 0x50FFD740,          // Vàng
            legalMoveColor = 0xFFFFFFFF,         // Trắng
            captureColor = 0x40FF5252,           // Đỏ
            checkColor = 0xCCD32F2F,             // Đỏ đậm

            buttonColor = 0xFF3A9AD4,
            buttonPressedColor = 0xFF2A80B8,
            isDarkStatusBarIcons = false
        )

        val themes = listOf(
            CLASSIC, DARK, MYSTIC, FIRE, CYBER, FOREST, OCEAN, COSMIC,
            DESERT, ARCTIC, SAKURA, NIGHT, AMBER, GRAPHITE, BURGUNDY, MIDNIGHT, OBSIDIAN
        )

        fun fromName(name: String): ChessTheme {
            return themes.find { it.name.equals(name, ignoreCase = true) } ?: CLASSIC
        }
    }
}

data class ChessScoreMode(
    val name: String,
    val time: Long, // seconds
    val score: Int, // target score for 3 stars
    val score1Start: Int, // min score for 1 star
    val score2Start: Int, // min score for 2 stars
    val progressLevel: List<Int>
) {
    companion object {
        val Score15s = ChessScoreMode("15s", 15, 20, 10, 20, listOf(5, 10, 15))
        val Score30s = ChessScoreMode("30s", 30, 30, 15, 30, listOf(15,20, 25))
        val Score45s = ChessScoreMode("45s", 45, 50, 25, 50, listOf(20, 30, 40))
        val Score60s = ChessScoreMode("1m", 60, 60, 30, 60, listOf(20, 30,40, 50))
        val Score300s = ChessScoreMode("5m", 300, 150, 50, 150, listOf(20, 50, 75, 100))

        val modes = listOf(Score15s, Score30s, Score45s, Score60s, Score300s)
        
        fun fromTime(seconds: Int): ChessScoreMode {
            return modes.find { it.time.toInt() == seconds } ?: Score30s
        }
    }
}