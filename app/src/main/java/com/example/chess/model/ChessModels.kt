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
    TUTORIAL("Hướng Dẫn", "Học cách đi cờ 6 quân"),
    SPECIAL_MOVE("Nước Đi Đặc Biệt", "Học các quy tắc đặc biệt")
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
    val buttonPressedColor: Long
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

            // 🔥 Đã chỉnh: Từ tối sang sáng ấm
            backgroundColors = listOf(0xFFE8DDD0, 0xFFD5C8B8),  // Từ #3A3530
            surfaceColor = 0xFFF0E8DC,                           // Từ #4A4540
            accentColor = 0xFF7CAE5A,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFF2C2824,          // Đổi từ trắng sang đen
            secondaryTextColor = 0xFF6A655E,  // Đổi từ xám nhạt sang xám đậm

            borderColor = 0xFFC8BDB0,        // Từ #5A5550
            dividerColor = 0xFFD5C8BC,        // Từ #4A4540

            iconColor = 0xFF6A655E,
            iconActiveColor = 0xFF7CAE5A,

            selectedSquareColor = 0x60C7D96C,
            lastMoveColor = 0x50F5F682,
            legalMoveColor = 0x28000000,
            captureColor = 0x40000000,
            checkColor = 0xCCE74C3C,

            buttonColor = 0xFF7CAE5A,
            buttonPressedColor = 0xFF5E8A42
        )

        // ============================================================
        // 2. DARK - TỐI GIẢN (Xám sáng thanh lịch)
        // ============================================================
        val DARK = ChessTheme(
            name = "Dark",
            displayName = "Tối giản",
            lightSquareColor = 0xFFA8BCC4,
            darkSquareColor = 0xFF5A7A8A,

            // 🔥 Đã chỉnh: Từ đen sang xám sáng
            backgroundColors = listOf(0xFFD5D8DC, 0xFFC4C8CC),  // Từ #2A2E32
            surfaceColor = 0xFFE0E4E8,                           // Từ #363A3E
            accentColor = 0xFF5A9AD9,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFF1C2024,          // Đổi sang đen
            secondaryTextColor = 0xFF5A646E,  // Đổi sang xám đậm

            borderColor = 0xFFB0B8C0,        // Từ #4A5058
            dividerColor = 0xFFC0C8D0,        // Từ #3A4048

            iconColor = 0xFF5A646E,
            iconActiveColor = 0xFF5A9AD9,

            selectedSquareColor = 0x607AA9E6,
            lastMoveColor = 0x505A9AE6,
            legalMoveColor = 0x28000000,
            captureColor = 0x40000000,
            checkColor = 0xCCE05050,

            buttonColor = 0xFF4A90C9,
            buttonPressedColor = 0xFF3A80B5
        )

        // ============================================================
        // 3. MYSTIC - HUYỀN BÍ (Tím pastel nhẹ nhàng)
        // ============================================================
        val MYSTIC = ChessTheme(
            name = "Mystic",
            displayName = "Huyền bí",
            lightSquareColor = 0xFFD8CCE6,
            darkSquareColor = 0xFF7A5A9A,

            // 🔥 Đã chỉnh: Từ tím đậm sang tím pastel
            backgroundColors = listOf(0xFFE8DCF0, 0xFFDCCCE6),  // Từ #2A2238
            surfaceColor = 0xFFF0E8F8,                           // Từ #3A304A
            accentColor = 0xFF9A7AD6,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFF1C1428,          // Đổi sang đen
            secondaryTextColor = 0xFF6A5A7A,  // Đổi sang tím đậm

            borderColor = 0xFFD0C0DE,        // Từ #4A3A60
            dividerColor = 0xFFDCCCE6,        // Từ #3A2E4A

            iconColor = 0xFF6A5A7A,
            iconActiveColor = 0xFF9A7AD6,

            selectedSquareColor = 0x60B08CE6,
            lastMoveColor = 0x50C89AE6,
            legalMoveColor = 0x28000000,
            captureColor = 0x40000000,
            checkColor = 0xCCE05060,

            buttonColor = 0xFF9A7AD6,
            buttonPressedColor = 0xFF7A5AB5
        )

        // ============================================================
        // 4. FIRE - ĐỎ ĐẤT (Kem ấm áp, nhẹ nhàng)
        // ============================================================
        val FIRE = ChessTheme(
            name = "Fire",
            displayName = "Đỏ đất",
            lightSquareColor = 0xFFEAD0C8,
            darkSquareColor = 0xFF9A4A3E,

            // 🔥 Đã chỉnh: Từ đỏ đậm sang kem ấm
            backgroundColors = listOf(0xFFF0E0D8, 0xFFE8D0C8),  // Từ #2A1A18
            surfaceColor = 0xFFF8ECE4,                           // Từ #3A2622
            accentColor = 0xFFD05A4A,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFF1C0E0C,          // Đổi sang đen
            secondaryTextColor = 0xFF6A4A42,  // Đổi sang nâu đỏ

            borderColor = 0xFFDCC0B8,        // Từ #54322C
            dividerColor = 0xFFE8D0C8,        // Từ #442622

            iconColor = 0xFF6A4A42,
            iconActiveColor = 0xFFD05A4A,

            selectedSquareColor = 0x60E86A5A,
            lastMoveColor = 0x50E88A5A,
            legalMoveColor = 0x28000000,
            captureColor = 0x40000000,
            checkColor = 0xFFFF3333,

            buttonColor = 0xFFD05A4A,
            buttonPressedColor = 0xFFB03A2E
        )

        // ============================================================
        // 5. CYBER - CÔNG NGHỆ (Xanh dương sáng hiện đại)
        // ============================================================
        val CYBER = ChessTheme(
            name = "Cyber",
            displayName = "Công nghệ",
            lightSquareColor = 0xFF9AB4C8,
            darkSquareColor = 0xFF2A4A62,

            // 🔥 Đã chỉnh: Từ xanh đậm sang xanh dương sáng
            backgroundColors = listOf(0xFFD8E8F0, 0xFFC8D8E4),  // Từ #141C24
            surfaceColor = 0xFFE4F0F8,                           // Từ #1E2A36
            accentColor = 0xFF2AC8D8,
            onAccentColor = 0xFF0A1A22,

            textColor = 0xFF0C1420,          // Đổi sang đen
            secondaryTextColor = 0xFF4A6278,  // Đổi sang xanh đậm

            borderColor = 0xFFB0C8D8,        // Từ #1A4260
            dividerColor = 0xFFC4D8E8,        // Từ #123048

            iconColor = 0xFF4A6278,
            iconActiveColor = 0xFF2AC8D8,

            selectedSquareColor = 0x602AC8D8,
            lastMoveColor = 0x502AB0C0,
            legalMoveColor = 0x28000000,
            captureColor = 0x402AC8D8,
            checkColor = 0xCCFF2266,

            buttonColor = 0xFF22B0C0,
            buttonPressedColor = 0xFF1A90A0
        )

        // ============================================================
        // 6. FOREST - THIÊN NHIÊN (Xanh lá tươi mát)
        // ============================================================
        val FOREST = ChessTheme(
            name = "Forest",
            displayName = "Rừng xanh",
            lightSquareColor = 0xFFE0E8CC,
            darkSquareColor = 0xFF628A56,

            // 🔥 Đã chỉnh: Từ xanh rêu đậm sang xanh lá sáng
            backgroundColors = listOf(0xFFE4ECD8, 0xFFD4DEC8),  // Từ #1E281C
            surfaceColor = 0xFFEFF4E4,                           // Từ #2A3826
            accentColor = 0xFF66AA54,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFF141A10,          // Đổi sang đen
            secondaryTextColor = 0xFF4A6240,  // Đổi sang xanh đậm

            borderColor = 0xFFC4D0B8,        // Từ #38503C
            dividerColor = 0xFFD4DEC8,        // Từ #2A3D2E

            iconColor = 0xFF4A6240,
            iconActiveColor = 0xFF66AA54,

            selectedSquareColor = 0x6090BC70,
            lastMoveColor = 0x50C8D84A,
            legalMoveColor = 0x28000000,
            captureColor = 0x40000000,
            checkColor = 0xCCD84A4A,

            buttonColor = 0xFF66AA54,
            buttonPressedColor = 0xFF4A8A3A
        )

        // ============================================================
        // 7. OCEAN - ĐẠI DƯƠNG (Xanh ngọc trong lành)
        // ============================================================
        val OCEAN = ChessTheme(
            name = "Ocean",
            displayName = "Đại dương",
            lightSquareColor = 0xFFD8EEE8,
            darkSquareColor = 0xFF468A96,

            // 🔥 Đã chỉnh: Từ xanh đậm sang xanh ngọc sáng
            backgroundColors = listOf(0xFFE0F0EC, 0xFFD0E4E0),  // Từ #162A30
            surfaceColor = 0xFFECF8F4,                           // Từ #1E3840
            accentColor = 0xFF26B8A8,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFF0E1C20,          // Đổi sang đen
            secondaryTextColor = 0xFF3A6A72,  // Đổi sang xanh đậm

            borderColor = 0xFFB8D0CC,        // Từ #285862
            dividerColor = 0xFFC8DCD8,        // Từ #1A424A

            iconColor = 0xFF3A6A72,
            iconActiveColor = 0xFF26B8A8,

            selectedSquareColor = 0x6038CCBC,
            lastMoveColor = 0x503AB0EA,
            legalMoveColor = 0x28000000,
            captureColor = 0x40000000,
            checkColor = 0xCCE86A7A,

            buttonColor = 0xFF26B8A8,
            buttonPressedColor = 0xFF1A8A7E
        )

        // ============================================================
        // 8. COSMIC - VŨ TRỤ (Tím xanh huyền ảo sáng)
        // ============================================================
        val COSMIC = ChessTheme(
            name = "Cosmic",
            displayName = "Vũ trụ",
            lightSquareColor = 0xFFC8D4EA,
            darkSquareColor = 0xFF50608A,

            // 🔥 Đã chỉnh: Từ xanh đậm sang tím xanh sáng
            backgroundColors = listOf(0xFFE0E4F4, 0xFFD0D4E8),  // Từ #181C2E
            surfaceColor = 0xFFECE8F8,                           // Từ #222842
            accentColor = 0xFF6A6EE6,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFF0C1020,          // Đổi sang đen
            secondaryTextColor = 0xFF484C72,  // Đổi sang tím đậm

            borderColor = 0xFFC0C8DE,        // Từ #304066
            dividerColor = 0xFFD0D4E8,        // Từ #222A4E

            iconColor = 0xFF484C72,
            iconActiveColor = 0xFF6A6EE6,

            selectedSquareColor = 0x607A82F0,
            lastMoveColor = 0x509AA8F0,
            legalMoveColor = 0x28000000,
            captureColor = 0x40000000,
            checkColor = 0xCCE84A5E,

            buttonColor = 0xFF6A6EE6,
            buttonPressedColor = 0xFF4A4ECC
        )

        // ============================================================
        // 9. DESERT - SA MẠC (Vàng cát ấm áp)
        // ============================================================
        val DESERT = ChessTheme(
            name = "Desert",
            displayName = "Sa mạc",
            lightSquareColor = 0xFFEADCC8,
            darkSquareColor = 0xFFB08860,

            // 🔥 Đã chỉnh: Từ nâu đậm sang vàng cát
            backgroundColors = listOf(0xFFECE0D0, 0xFFE0D0BC),  // Từ #302620
            surfaceColor = 0xFFF4E8D8,                           // Từ #42342A
            accentColor = 0xFFCC8844,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFF1A1410,          // Đổi sang đen
            secondaryTextColor = 0xFF6A5A44,  // Đổi sang nâu

            borderColor = 0xFFD4C0B0,        // Từ #544438
            dividerColor = 0xFFE0D0BC,        // Từ #42342A

            iconColor = 0xFF6A5A44,
            iconActiveColor = 0xFFCC8844,

            selectedSquareColor = 0x60D09468,
            lastMoveColor = 0x50E09452,
            legalMoveColor = 0x28000000,
            captureColor = 0x40000000,
            checkColor = 0xCCD84A4A,

            buttonColor = 0xFFCC8844,
            buttonPressedColor = 0xFFB07034
        )

        // ============================================================
        // 10. ARCTIC - BĂNG GIÁ (Trắng xanh mát lạnh)
        // ============================================================
        val ARCTIC = ChessTheme(
            name = "Arctic",
            displayName = "Băng giá",
            lightSquareColor = 0xFFE8F0F2,
            darkSquareColor = 0xFF7A9AAA,

            // 🔥 Đã chỉnh: Từ xanh đậm sang trắng xanh
            backgroundColors = listOf(0xFFE8F0F4, 0xFFD8E4E8),  // Từ #1A2630
            surfaceColor = 0xFFF0F8FC,                           // Từ #243846
            accentColor = 0xFF3AB8E6,
            onAccentColor = 0xFF0A2E48,

            textColor = 0xFF0A1A22,          // Đổi sang đen
            secondaryTextColor = 0xFF4A6A78,  // Đổi sang xanh

            borderColor = 0xFFC0D0D8,        // Từ #2A4A58
            dividerColor = 0xFFD0DCE0,        // Từ #1E3A44

            iconColor = 0xFF4A6A78,
            iconActiveColor = 0xFF3AB8E6,

            selectedSquareColor = 0x606AC8F0,
            lastMoveColor = 0x50AAD8F0,
            legalMoveColor = 0x28000000,
            captureColor = 0x40000000,
            checkColor = 0xCCE86A7A,

            buttonColor = 0xFF3AB8E6,
            buttonPressedColor = 0xFF1A90C8
        )

        // ============================================================
        // 11. SAKURA - HOA ANH ĐÀO (Hồng pastel ngọt ngào)
        // ============================================================
        val SAKURA = ChessTheme(
            name = "Sakura",
            displayName = "Anh đào",
            lightSquareColor = 0xFFF8E4E8,
            darkSquareColor = 0xFFB07A88,

            // 🔥 Đã chỉnh: Từ hồng đậm sang hồng pastel
            backgroundColors = listOf(0xFFF8E8EC, 0xFFF0DCE0),  // Từ #2A1C20
            surfaceColor = 0xFFFCF0F4,                           // Từ #3A2630
            accentColor = 0xFFE8708E,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFF1A0E12,          // Đổi sang đen
            secondaryTextColor = 0xFF6A4A54,  // Đổi sang hồng đậm

            borderColor = 0xFFE0C8D0,        // Từ #4E3640
            dividerColor = 0xFFE8D4DC,        // Từ #3E2A32

            iconColor = 0xFF6A4A54,
            iconActiveColor = 0xFFE8708E,

            selectedSquareColor = 0x60E884A4,
            lastMoveColor = 0x50F0A0BA,
            legalMoveColor = 0x28000000,
            captureColor = 0x40000000,
            checkColor = 0xCCD84A5A,

            buttonColor = 0xFFE8708E,
            buttonPressedColor = 0xFFCC4A6A
        )

        // ============================================================
        // 12. OBSIDIAN - ĐEN KIM LOẠI (Xám sang trọng)
        // ============================================================
        val OBSIDIAN = ChessTheme(
            name = "Obsidian",
            displayName = "Obsidian",
            lightSquareColor = 0xFFB8BCC0,
            darkSquareColor = 0xFF4A4E52,

            // 🔥 Đã chỉnh: Từ đen sang xám sáng
            backgroundColors = listOf(0xFFD0D2D4, 0xFFC0C2C4),  // Từ #1C1C1E
            surfaceColor = 0xFFDCDEDE,                           // Từ #28282A
            accentColor = 0xFFC8A838,
            onAccentColor = 0xFF1A1608,

            textColor = 0xFF0A0A0C,          // Đổi sang đen
            secondaryTextColor = 0xFF505054,  // Đổi sang xám đậm

            borderColor = 0xFFB0B2B4,        // Từ #3A3A3E
            dividerColor = 0xFFC0C2C4,        // Từ #2C2C2E

            iconColor = 0xFF505054,
            iconActiveColor = 0xFFC8A838,

            selectedSquareColor = 0x60D0B048,
            lastMoveColor = 0x50C0A838,
            legalMoveColor = 0x28000000,
            captureColor = 0x40000000,
            checkColor = 0xCCE04848,

            buttonColor = 0xFFC8A838,
            buttonPressedColor = 0xFFA88828
        )

        val NIGHT = ChessTheme(
            name = "Night",
            displayName = "Đêm thanh bình",

            // Bàn cờ: Tối vừa, không bị lóa
            lightSquareColor = 0xFFB8C4D0,    // Xám xanh sáng
            darkSquareColor = 0xFF3A5068,      // Xanh đậm vừa

            // Background: Tối ấm, không bị đen tuyền
            backgroundColors = listOf(0xFF1E242B, 0xFF14191F),
            surfaceColor = 0xFF2A323C,

            // Accent: Xanh dương dịu, không chói
            accentColor = 0xFF4A90D9,
            onAccentColor = 0xFFFFFFFF,

            // Text: Trắng ngà, dễ đọc
            textColor = 0xFFE8ECF0,
            secondaryTextColor = 0xFF8A949E,

            borderColor = 0xFF3A4450,
            dividerColor = 0xFF2E3842,

            iconColor = 0xFF8A949E,
            iconActiveColor = 0xFF4A90D9,

            // Highlight: Rõ ràng nhưng không gắt
            selectedSquareColor = 0x607AA9E6,  // 38%
            lastMoveColor = 0x505A9AE6,        // 31%
            legalMoveColor = 0x28FFFFFF,       // White dot
            captureColor = 0x40FF6B6B,         // Đỏ nhạt
            checkColor = 0xCCFF4444,

            buttonColor = 0xFF4A90D9,
            buttonPressedColor = 0xFF3A7AC9
        )

        // ============================================================
        // 14. AMBER - HỔ PHÁCH (Tối ấm áp, vàng ánh đèn)
        // ============================================================
        val AMBER = ChessTheme(
            name = "Amber",
            displayName = "Hổ phách",

            // Bàn cờ: Vàng nâu ấm áp
            lightSquareColor = 0xFFD4C4A8,    // Vàng kem
            darkSquareColor = 0xFF7A6548,      // Nâu vàng

            // Background: Nâu tối ấm
            backgroundColors = listOf(0xFF241E18, 0xFF181410),
            surfaceColor = 0xFF342A20,

            // Accent: Vàng cam nhẹ
            accentColor = 0xFFD4A048,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFFF5EDE0,
            secondaryTextColor = 0xFFA89880,

            borderColor = 0xFF4A3C30,
            dividerColor = 0xFF3A3026,

            iconColor = 0xFFA89880,
            iconActiveColor = 0xFFD4A048,

            selectedSquareColor = 0x60D4A048,
            lastMoveColor = 0x50D4A048,
            legalMoveColor = 0x28FFFFFF,
            captureColor = 0x40FF6B4A,
            checkColor = 0xCCFF4444,

            buttonColor = 0xFFD4A048,
            buttonPressedColor = 0xFFB88838
        )

        // ============================================================
        // 15. GRAPHITE - THAN CHÌ (Tối trung tính, hiện đại)
        // ============================================================
        val GRAPHITE = ChessTheme(
            name = "Graphite",
            displayName = "Than chì",

            // Bàn cờ: Xám thanh lịch
            lightSquareColor = 0xFFB8C0C8,    // Xám sáng
            darkSquareColor = 0xFF4A525A,      // Xám đậm

            // Background: Xám tối, không đen
            backgroundColors = listOf(0xFF22262A, 0xFF181C20),
            surfaceColor = 0xFF2E3238,

            // Accent: Xám xanh hiện đại
            accentColor = 0xFF6A8A9E,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFFE8ECF0,
            secondaryTextColor = 0xFF88929C,

            borderColor = 0xFF3A4048,
            dividerColor = 0xFF2E343A,

            iconColor = 0xFF88929C,
            iconActiveColor = 0xFF6A8A9E,

            selectedSquareColor = 0x607A9AAE,
            lastMoveColor = 0x506A8A9E,
            legalMoveColor = 0x28FFFFFF,
            captureColor = 0x40FF6B6B,
            checkColor = 0xCCFF4444,

            buttonColor = 0xFF6A8A9E,
            buttonPressedColor = 0xFF5A7A8E
        )

        // ============================================================
        // 16. BURGUNDY - VANG BORDEAUX (Tối sang trọng, đỏ quý phái)
        // ============================================================
        val BURGUNDY = ChessTheme(
            name = "Burgundy",
            displayName = "Bordeaux",

            // Bàn cờ: Hồng đỏ quý phái
            lightSquareColor = 0xFFE0C8C8,    // Hồng nhạt
            darkSquareColor = 0xFF7A4A50,      // Đỏ burgundy

            // Background: Đỏ đậm sang trọng
            backgroundColors = listOf(0xFF22161A, 0xFF180E12),
            surfaceColor = 0xFF322026,

            // Accent: Vàng hồng
            accentColor = 0xFFD48A7A,
            onAccentColor = 0xFFFFFFFF,

            textColor = 0xFFF5EAE8,
            secondaryTextColor = 0xFFA88882,

            borderColor = 0xFF4A3238,
            dividerColor = 0xFF3A262A,

            iconColor = 0xFFA88882,
            iconActiveColor = 0xFFD48A7A,

            selectedSquareColor = 0x60D48A7A,
            lastMoveColor = 0x50D48A7A,
            legalMoveColor = 0x28FFFFFF,
            captureColor = 0x40FF6B6B,
            checkColor = 0xCCFF3333,

            buttonColor = 0xFFD48A7A,
            buttonPressedColor = 0xFFB86A5A
        )

        val MIDNIGHT = ChessTheme(
            name = "Midnight",
            displayName = "Nửa đêm",

            // Bàn cờ: Xanh đêm huyền ảo
            lightSquareColor = 0xFFA8BED4,    // Xanh nhạt
            darkSquareColor = 0xFF2A4A6A,      // Xanh đêm

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

            selectedSquareColor = 0x605AAAD4,
            lastMoveColor = 0x503A9AD4,
            legalMoveColor = 0x28FFFFFF,
            captureColor = 0x40FF6B8A,
            checkColor = 0xCCFF4466,

            buttonColor = 0xFF3A9AD4,
            buttonPressedColor = 0xFF2A80B8
        )

        val themes = listOf(
            // Theme sáng
            CLASSIC,
            DARK,
            MYSTIC,
            FIRE,
            CYBER,
            FOREST,
            OCEAN,
            COSMIC,
            DESERT,
            ARCTIC,
            SAKURA,
            NIGHT,
            AMBER,
            GRAPHITE,
            BURGUNDY,
            MIDNIGHT,
            OBSIDIAN
        )

        fun fromName(name: String): ChessTheme {
            return themes.find { it.name.equals(name, ignoreCase = true) } ?: CLASSIC
        }
    }
}