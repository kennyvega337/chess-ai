package com.example.chess.data

data class MessageEndGame(
    val Mode: String,
    val IsWon: Boolean,
    val Title: String,
    val Message: String,
    val Level: Int = 0
)
object EndGameMessages {
    val list = listOf(
        MessageEndGame(Mode = "AI", IsWon = false, Title = "THẬT SỰ LUÔN?", Message = "Bạn vừa thua Bot Cấp 1 đó... Nó thậm chí còn chưa học hết luật đi quân mà!", Level = 1),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "TAY NGHỀ TẬP BỘI", Message = "Cấp độ này tạo ra là để thắng, vậy mà bạn vẫn tìm được cách thua. Tài năng đấy!", Level = 1),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "NHẦM NÚT À?", Message = "Chắc là bạn bấm nhầm nước đi đúng không? Bấm lại xem nào, tôi chờ.", Level = 1),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "CỜ VUA LÀ BẰNG GỖ", Message = "Đừng lo, bàn cờ không có lỗi, lỗi là ở cách bạn đi thôi!", Level = 1),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "KHỞI ĐỘNG CHƯA XONG", Message = "Thua Cấp 1 coi như là chạy nháp đi. Giờ mới bắt đầu chơi thật này!", Level = 1),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "HỌC LẠI LUẬT BÀN CỜ", Message = "Con Vua đi được 1 ô, còn bạn thì vừa đi tong cả ván cờ rồi.", Level = 1),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "MẮT MÃI TRÊN MÂY", Message = "Bot vừa đi những nước ngô nghê nhất có thể, vậy mà bạn vẫn dính bẫy sao?", Level = 1),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "QUÁ DỄ CHO BOT", Message = "Bot Cấp 1 vừa tự hào ghi nhận chiến thắng đầu đời trước bạn đấy.", Level = 1),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "BẤT BỜ CHƯA?", Message = "Thua Cấp 1 là một trải nghiệm hiếm có. Bạn nên chụp màn hình làm kỷ niệm.", Level = 1),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "NÊN HẠ THÊM CẤP?", Message = "Rất tiếc game không có Cấp 0. Thử lại ván nữa để gỡ gạc nào!", Level = 1),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "VẪN CHƯA ĐỦ SỨC", Message = "Mức Trung Bình mà đã khiến bạn ngộp thở rồi sao? Bình tĩnh suy nghĩ lại nào!", Level = 2),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "ĐI QUÂN HAY ĐI BỤI?", Message = "Nước đi của bạn rất nghệ thuật, tiếc là nghệ thuật thua cuộc!", Level = 2),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "BIẾT NGƯỜI BIẾT TA", Message = "Tấn công hay đấy, nhưng phòng thủ thì lại mở cửa đón khách rồi.", Level = 2),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "CỜ BÍ NƯỚC BẢY", Message = "Bạn suy nghĩ 3 phút chỉ để đưa quân vào mồm Bot sao?", Level = 2),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "THUA TRONG TÍCH TẮC", Message = "Thêm một ván thua nhẹ nhàng. Có vẻ Cấp 2 vẫn hơi quá sức nhỉ?", Level = 2),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "SƠ HỞ LÀ MẤT VUA", Message = "Bảo vệ Vua khó đến thế sao? Hậu của bạn khóc nát con mắt rồi kìa.", Level = 2),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "NẮM CHẮC PHẦN THUA", Message = "Thấy bạn đi nước cờ đó, Bot đã mỉm cười từ 5 lượt trước rồi.", Level = 2),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "THIẾU CHÚT MAY MẮN", Message = "Trận này do bạn đen thôi... hoặc do bạn tính thiếu chục nước đi gì đó.", Level = 2),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "CẦN LẮM MỘT PHÁP THUẬT", Message = "Không có phép thuật nào cứu được ván cờ vừa rồi đâu. Thử lại nhé!", Level = 2),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "VỀ LẠI CẤP 1 CHƯA?", Message = "Có lẽ bạn nên xuống Cấp 1 để lấy lại tự tin trước khi quay lại đây.", Level = 2),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "QUÁ SỨC RỒI SAO?", Message = "Mới cấp 3 đã làm khó bạn rồi sao, hãy hạ cấp độ cho game đấu dễ hơn nào!", Level = 3),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "CỐ CẮM ĐẦU CỐ BẮT RÙA", Message = "Càng về tàn cuộc bạn càng cuống. Hít thở sâu và tính toán lại xem!", Level = 3),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "BẮT BẢY THẤT BẠI", Message = "Bẫy của bạn giăng ra đẹp lắm, tiếc là tự bạn bước chân vào!", Level = 3),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "CƠ HỘI MỤT MẤT", Message = "Bạn từng có thế thắng ở nước thứ 12, nhưng bạn đã từ chối nó.", Level = 3),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "HẬU ĐI VẮNG", Message = "Nước đi mất Hậu ngớ ngẩn đó đã chấm dứt mọi hy vọng của bạn.", Level = 3),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "MÊ CUNG CỜ VUA", Message = "Bạn đã lạc lối hoàn toàn giữa các thế cờ của Cấp 3 rồi.", Level = 3),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "TOAN TÍNH TRẮC TRỞ", Message = "Tính trước 2 nước là tốt, nhưng Bot ở đây tính trước 5 nước cơ!", Level = 3),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "THIÊN THỜI KHÔNG HÒA", Message = "Đã cố gắng hết sức chưa? Nếu rồi thì... trình độ bạn tới đây thôi sao?", Level = 3),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "ĐỔI QUÂN LỖ VỐN", Message = "Mỗi lần đổi quân là một lần bạn tiến gần hơn đến tấm vé thua cuộc.", Level = 3),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "THẮNG TROI NÚI MỜ", Message = "Hy vọng chiến thắng lóe lên rồi vụt tắt. Đau đớn thật đấy!", Level = 3),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "BẠN ĐANG TÍNH GÌ?", Message = "Những nước đi của bạn làm Bot cũng phải hoang mang... vì nó quá yếu!", Level = 4),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "NGƯỜI CHƠI HỆ MƠ MỘNG", Message = "Nghĩ rằng có thể qua mặt Cấp 4 dễ dàng vậy sao? Tỉnh giấc đi bạn ơi!", Level = 4),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "PHÒNG THỦ MỎNG DANH", Message = "Hàng phòng thủ của bạn giòn tan như bánh tráng vậy. Bị đâm thủng rồi!", Level = 4),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "SA LẦY TÀN CUỘC", Message = "Khai cuộc tạm ổn, trung cuộc loay hoay, tàn cuộc... đầu hàng!", Level = 4),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "BẢN BẢN VƯỢT TẦM", Message = "Cấp 4 bắt đầu biết 'tư duy' rồi, còn bạn dường như đang dừng lại.", Level = 4),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "CẤP ĐỘ CỦA SỰ THỰC TẾ", Message = "Đừng trách AI ác, hãy trách bản thân quá chủ quan!", Level = 4),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "MẤT TẬP TRUNG", Message = "Chỉ một phút lơ đễnh, cả cơ đồ cờ vua tan thành mây khói.", Level = 4),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "ÁP LỰC THỜI GIAN", Message = "Càng nghĩ lâu đi càng sai. Bạn đang tự làm khó chính mình đấy.", Level = 4),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "THUA TRONG DANH DỰ?", Message = "Không, thua thế này thì không gọi là danh dự được đâu. Thử lại nhé!", Level = 4),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "GIỚI HẠN HIỆN TẠI", Message = "Có vẻ như Cấp 4 là bức tường bêtông chặn đứng bạn rồi.", Level = 4),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "MẠO HIỂM BẤT THÀNH", Message = "Muốn chơi chiêu với Cấp 5 sao? Chiêu này Bot xài từ năm ngoái rồi!", Level = 5),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "TÂM LÝ CHƯA VỮNG", Message = "Một chút áp lực đã làm bạn rối loạn. Cần rèn luyện thêm bản lĩnh!", Level = 5),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "HỌC PHÍ CAO RỒI", Message = "Mỗi ván thua Cấp 5 là một bài học đắt giá. Hôm nay bạn đóng hơi nhiều học phí đấy.", Level = 5),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "BẮT BÀI HOÀN TOÀN", Message = "Mọi ý đồ của bạn đều lộ rõ như ban ngày. Bot đọc bạn như một cuốn sách mở!", Level = 5),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "CƠ BẢN LÀ CHƯA ĐỦ", Message = "Chơi cờ mẹo không giúp bạn thắng được Cấp 5 đâu. Cần chiến thuật thật sự!", Level = 5),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "BẾ TẮC HOÀN TOÀN", Message = "Bạn không còn nước nào để đi nữa đúng không? Chiếu hết cực kỳ đẹp mắt!", Level = 5),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "CỜ CAO MỘT TRƯỢNG", Message = "Bạn tính 5 nước, Bot tính 10 nước. Khoảng cách nằm ở đó đấy!", Level = 5),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "SUY TƯ VÔ ÍCH", Message = "Ngồi ngẫm nghĩ rõ lâu nhưng kết quả vẫn là một trận thua ê chề.", Level = 5),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "KIỂM SOÁT BÀN CỜ", Message = "Bot không cho bạn một khoảng trống nào để thở. Nhập cuộc lại nào!", Level = 5),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "SẮP CHẠM TỚI ĐÁY", Message = "Chơi tiếp hay dừng lại? Cấp 5 đang bắt đầu thấy chán rồi đấy.", Level = 5),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "CHẠM TRẦN KỸ NĂNG", Message = "Đến được Cấp 6 là giỏi rồi, nhưng làm con mồi cho Bot thì vẫn thế thôi!", Level = 6),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "SỰ KHÁC BIỆT ĐẲNG CẤP", Message = "Bạn đánh như một người chơi giỏi, nhưng Bot ở đây đánh như một cỗ máy!", Level = 6),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "ĐẮT GIÁ MỖI SAI LẦM", Message = "Ở cấp độ này, chỉ một sơ hở nhỏ cũng đủ để bạn nhận kết cục cay đắng.", Level = 6),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "BỊ BÓP NGHẸT", Message = "Cảm giác không còn quân nào để nhúc nhích thế nào? Thoải mái lên, ai rồi cũng thua thôi.", Level = 6),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "TỰ SÁT CHIẾN THUẬT", Message = "Dâng Hậu để đổi lấy khoảng trống? Một quyết định sai lầm lịch sử!", Level = 6),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "BỨC TƯỜNG CỜ VUA", Message = "Cấp 6 không dành cho người chơi bằng cảm xúc. Hãy dùng cái đầu lạnh!", Level = 6),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "THUẬT TOÁN ÁP ĐẢO", Message = "Chống lại máy tính ở Cấp 6 cần nhiều hơn là sự cố gắng suông.", Level = 6),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "THẢ THÍCH VÀ BẮT", Message = "Bot cố tình nhường bạn vài nước đầu đấy, bạn có nhận ra không?", Level = 6),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "HOÀNG HẬU TẮT NẮNG", Message = "Quân cờ mạnh nhất của bạn đã ngã xuống mà không làm nên trò trống gì.", Level = 6),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "XIN CHÙA VÀI VÁN?", Message = "Thua vài ván nữa chắc bạn mới quen được nhịp độ của Kiện Tướng nhỉ?", Level = 6),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "BẠN MỜI TÔI CHƠI À?", Message = "Cấp 7 chỉ tốn 0.01% khả năng để hạ gục bạn. Thử lại ván nữa không?", Level = 7),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "HOÀN HẢO KHÔNG DẤU VẾT", Message = "Thua Cấp 7 không có gì phải xấu hổ. Xấu hổ là bạn nghĩ mình có cửa thắng!", Level = 7),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "KẾT QUẢ DỰ BÁO TRƯỚC", Message = "Trận đấu đã an bài ngay từ khi bạn bấm nút 'Bắt đầu' rồi.", Level = 7),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "SỰ HỦY DIỆT TUYỆT ĐỐI", Message = "Không một vết xước, không một cơ hội lật kèo. Bot Cấp 7 gửi lời chào thân ái!", Level = 7),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "THẦN CỜ CŨNG CHỊU", Message = "Đưa Garry Kasparov tới đây may ra có cửa, còn bạn thì... chúc may mắn lần sau!", Level = 7),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "NHƯ MỘT TRÒ ĐÙA", Message = "Cảm ơn vì đã giải trí cho Bot Cấp 7 trong vài phút ngắn ngủi vừa qua.", Level = 7),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "MÁY TÍNH THÔNG MINH HƠN", Message = "Thuật toán tối thượng đã chứng minh: Con người vẫn còn một khoảng cách rất xa!", Level = 7),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "BẢN HÙNG CA THẤT BẠI", Message = "Bạn đã chiến đấu rất kiên cường... cho đến khi bị chiếu hết ở nước thứ 20.", Level = 7),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "TẬP LẠI TỪ CẤP 1 ĐI", Message = "Muốn chinh phục Cấp 7? Có lẽ bạn nên cày lại từ Cấp 1 khoảng 100 lần nữa.", Level = 7),
        MessageEndGame(Mode = "AI", IsWon = false, Title = "VÔ VỌNG!", Message = "Mọi ngõ ngách đều bị chặn đứng. Bạn vừa nếm trải sức mạnh của đấng tối thượng!", Level = 7),

                MessageEndGame(
                Mode = "AI", IsWon = true,
        Title = "KHỞI ĐỘNG XONG CHƯA?",
        Message = "Thắng Bot Cấp 1 chỉ là bước tập tễnh thôi! Tăng lên Cấp 2 để xem bạn có làm nên chuyện không nào.",
        Level = 1
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "ĂN RỜI MẤY HẠT BỤI",
    Message = "Chiến thắng quá nhẹ nhàng đúng không? Đừng ngâm mình ở ao làng nữa, thử ngay Cấp 2 đi!",
    Level = 1
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "CHƯA ĐỦ ĐÃ TAY",
    Message = "Bot Cấp 1 vừa đi quân vừa ngủ gật đấy. Lên Cấp 2 để gặp đối thủ thực sự biết suy nghĩ nhé!",
    Level = 1
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "ĐỪNG VỘI TỰ HÀO",
    Message = "Đội mương đánh thắng em bé 3 tuổi cũng không có gì khoe đâu. Tăng độ khó lên Cấp 2 thôi!",
    Level = 1
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "MỞ BÁT THUẬN LỢI",
    Message = "Chiến thắng đầu tay rồi! Nhưng ở Cấp 1 thì ai cũng thắng được, lên Cấp 2 xem tài nghệ ra sao nào.",
    Level = 1
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "CỜ DỄ NHƯ ĂN BÁNH",
    Message = "Thắng dễ thế này thì chán chết! Hãy thử thách bản thân ở Cấp 2 ngay để lấy lại cảm giác hồi hộp.",
    Level = 1
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "TẬP BỘI XONG RỒI",
    Message = "Chúc mừng bạn đã thuộc luật đi quân! Giờ là lúc nâng cấp độ để bắt đầu chơi cờ thực sự.",
    Level = 1
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "BOT TỰ SÁT",
    Message = "Ván này Bot đi bừa cho bạn thắng đấy. Lên Cấp 2 xem nó có chịu nhường bạn nữa không!",
    Level = 1
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "THẮNG DỄ QuÁ?",
    Message = "Đừng chôn chân ở Cấp 1 mãi thế, tài năng của bạn xứng đáng được thử thách ở Cấp 2 đấy!",
    Level = 1
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "THÀNH TÍCH ĐẦU TIÊN",
    Message = "Có điểm rồi nhé! Giờ thì dũng cảm bấm nâng lên Cấp 2 để xem bối cảnh có khác không nào.",
    Level = 1
    ),

    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "VẪN DỄ CHÁN!",
    Message = "Mức Trung Bình vẫn chưa đủ làm bạn toát mồ hôi sao? Nhảy sang Cấp 3 để nếm mùi thử thách thật sự nhé!",
    Level = 2
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "BẮT ĐẦU CÓ NÉT",
    Message = "Đánh nét đấy! Nhưng Cấp 2 vẫn còn nhường bạn lắm. Tăng Cấp 3 xem có bị gạt chân không nào.",
    Level = 2
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "THỦ THUẬT NGHỆ THUẬT",
    Message = "Mấy cái bẫy nhỏ ở Cấp 2 qua mắt sao được bạn. Hãy xem Cấp 3 gài bẫy bạn thế nào nhé!",
    Level = 2
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "LÀM CHỦ BÀN CỜ",
    Message = "Bạn đè bẹp Cấp 2 không thương tiếc. Đã đến lúc bước lên Cấp 3 để chứng tỏ bản lĩnh rồi!",
    Level = 2
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "BẬC THẦY MỨC DỄ",
    Message = "Ở Cấp 2 bạn là bá chủ rồi đấy. Nhưng dám đặt chân lên Cấp 3 để thử sức không?",
    Level = 2
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "BOT ĐANG TÍNH LẠI",
    Message = "Thắng Cấp 2 khá mượt đấy. Tăng cấp độ lên Cấp 3 ngay kẻo Bot nó học thuộc nước đi của bạn!",
    Level = 2
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "TỰ TIN LÊN RỒI",
    Message = "Tự tin có thừa rồi đúng không? Chuyển sang Cấp 3 ngay để xem sự tự tin đó kéo dài bao lâu!",
    Level = 2
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "VƯỢT QUA VÙNG AN TOÀN",
    Message = "Cấp 2 chỉ là món khai vị thôi. Vào Cấp 3 để bắt đầu thưởng thức món chính nào!",
    Level = 2
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "SỨC MẠNH TIỀM ẨN",
    Message = "Cách bạn hạ Cấp 2 rất ấn tượng! Hãy nâng lên Cấp 3 để khai phá hết tiềm năng của mình.",
    Level = 2
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "CÒN CHỜ GÌ NỮA?",
    Message = "Thắng Cấp 2 rồi thì còn ở lại làm gì nữa? Chuyển sang Cấp 3 đấu trận hoành tráng hơn đi!",
    Level = 2
    ),

    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "BẮT ĐẦU CÓ TAY NGHỀ",
    Message = "Vượt qua Cấp 3 không phải dạng vừa đâu! Nhưng Cấp 4 là một vùng trời hoàn toàn khác đấy, dám thử không?",
    Level = 3
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "LẬT KÈO NGHỆ THUẬT",
    Message = "Mấy nước tàn cuộc ở Cấp 3 hay đấy. Tăng độ khó lên Cấp 4 xem tính toán của bạn sâu đến đâu!",
    Level = 3
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "HẠ GỤC BẢN THỬ NGHỆM",
    Message = "Cấp 3 đã phải quỳ gối! Đã đến lúc thách thức Cấp 4 để xem ai mới là cỗ máy tính toán thực sự.",
    Level = 3
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "TIẾN BỘ RÕ RỆT",
    Message = "Trình độ cờ của bạn đang lên đấy. Chuyển sang Cấp 4 để thử cảm giác căng não đích thực nào!",
    Level = 3
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "TAY CỜ TIỀM NĂNG",
    Message = "Hạ gục Cấp 3 nhẹ nhàng vậy sao? Tăng ngay lên Cấp 4 để xem bạn trụ được bao nhiêu nước!",
    Level = 3
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "THUẬN BUỒM XUYÔI GIÓ",
    Message = "Thắng Cấp 3 dễ hơn bạn nghĩ đúng không? Đừng ngần ngại bấm Cấp 4 để xem bất ngờ gì đang đợi.",
    Level = 3
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "BÁ CHỦ PHÒNG CỜ",
    Message = "Cấp 3 chính thức bị bạn chinh phục. Hãy để Cấp 4 dạy cho bạn một bài học về chiến thuật nâng cao!",
    Level = 3
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "CHIẾN THUẬT SẮC BẮC",
    Message = "Những nước cờ rất sắc bén! Lên Cấp 4 ngay để xem độ sắc của bạn có cắt đứt được AI không.",
    Level = 3
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "ĐỪNG KHỰA LẠI ĐÂY",
    Message = "Thắng Cấp 3 chưa đủ để tự xưng cao thủ đâu. Lên Cấp 4 chứng tỏ giá trị bản thân đi nào!",
    Level = 3
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "CỜ RẤT SÁNG",
    Message = "Nước đi rất sáng sáng! Đã đến lúc bước vào vùng tối thử thách của Cấp 4 rồi đấy.",
    Level = 3
    ),

    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "TIN NỔI KHÔNG",
    Message = "Có vẻ như Cấp 4 vẫn quá dễ với bạn, hãy tăng lên Cấp 5 xem ván cờ có đảo chiều không!",
    Level = 4
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "ĐẮNG CHO AI CẤP 4",
    Message = "Bot Cấp 4 đã vò đầu bứt tóc trước bạn. Thử độ lì của mình với Cấp 5 ngay và luôn nào!",
    Level = 4
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "CAO THỦ ẨN DANH?",
    Message = "Đánh gục Cấp 4 thuyết phục đấy! Nhưng ở Cấp 5, chỉ một sai lầm nhỏ là bạn biến thành cặn bã ngay.",
    Level = 4
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "VỚI TỚI ĐẲNG CẤP",
    Message = "Cấp 4 đã nằm dưới chân bạn. Bước sang Cấp 5 để trải nghiệm cảm giác bị ép đến nghẹt thở!",
    Level = 4
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "TẠM TẮT NỤ CƯỜI BOT",
    Message = "Bot Cấp 4 vừa tắt nụ cười khinh bỉnh. Lên Cấp 5 để xem ai mới là người cười sau cùng!",
    Level = 4
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "BẤT BỜ CHƯA?",
    Message = "Bạn thực sự đã vượt qua Cấp 4! Nâng cấp độ ngay lên Cấp 5 xem có giữ nổi chuỗi thắng không.",
    Level = 4
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "ĐÃ ĐẾN LÚC TĂNG TỐC",
    Message = "Trình độ của bạn vượt tầm Cấp 4 rồi. Đừng phí thời gian ở đây nữa, tiến lên Cấp 5 thôi!",
    Level = 4
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "TƯ DUY ĐỈNH CAO",
    Message = "Nước đi tàn cuộc quá tuyệt vời! Nhưng Cấp 5 sẽ không cho bạn nhiều khoảng trống như vậy đâu.",
    Level = 4
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "SỨC ÉP GIA TĂNG",
    Message = "Chinh phục Cấp 4 thành công! Bạn có đủ dũng khí đối mặt với những cạm bẫy hóc húa ở Cấp 5?",
    Level = 4
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "THỬ THÁCH VƯỢT QUA",
    Message = "Thắng Cấp 4 là một cột mốc lớn. Bấm thử Cấp 5 để xem chân lý thuộc về ai nhé!",
    Level = 4
    ),

    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "CAO THỦ XUẤT LỘ",
    Message = "Quá dữ! Bạn đã hạ gục Cấp 5. Nhưng Cấp 6 là đẳng cấp Kiện tướng đấy, dám vượt ngưỡng không?",
    Level = 5
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "MÁY TÍNH CŨNG KHÓC",
    Message = "AI Cấp 5 cay đắng chấp nhận thất bại. Hãy nâng lên Cấp 6 để xem bản lĩnh thực sự của Kiện tướng!",
    Level = 5
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "MẤT ĂN MẤT NGỦ",
    Message = "Bạn vừa làm cho thuật toán Cấp 5 bị lỗi vì quá bất ngờ! Tiến lên Cấp 6 để khiêu chiến quái vật tiếp theo.",
    Level = 5
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "SUY TƯ CHÍNH XÁC",
    Message = "Mọi tính toán ở Cấp 5 đều bị bạn đọc vị! Cấp 6 sẽ không dễ bị bắt bài như thế đâu.",
    Level = 5
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "XUYÊN THỦNG PHÒNG THỦ",
    Message = "Chiến thuật của bạn đã đánh tan hàng phòng thủ Cấp 5. Lên Cấp 6 để đối đầu với bức tường thép!",
    Level = 5
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "RỜI KHỎI BẠN BÈ",
    Message = "Bạn đã bỏ xa các người chơi thông thường khi thắng Cấp 5. Cấp 6 đang đợi tân Kiện tướng đấy!",
    Level = 5
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "THẮNG TRONG TẤM MẮT",
    Message = "Một ván cờ Cấp 5 quá mãn nhãn! Chuyển sang Cấp 6 để xem sức chịu đựng của bạn tới đâu.",
    Level = 5
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "ĐÒN KẾT LIỄU ĐẸP",
    Message = "Nước chiếu hết Cấp 5 đẹp như trong sách giáo khoa! Lên Cấp 6 viết tiếp lịch sử đi nào.",
    Level = 5
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "QUÁ NGHỆ THUẬT",
    Message = "Cấp 5 cũng phải ngả nón bái phục. Tiến lên Cấp 6 – nơi dành riêng cho những bộ óc kiệt xuất!",
    Level = 5
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "BƯỚC CHÂN CAO THỦ",
    Message = "Hạ Cấp 5 nhẹ nhàng như dạo chơi. Hãy thử thách giới hạn tột cùng ở Cấp 6 ngay!",
    Level = 5
    ),

    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "ĐẢ MÃ KIỆN TƯỚNG",
    Message = "Không thể tin nổi! Bạn đã đánh bại Cấp 6! Trước mặt bạn chỉ còn Cấp 7 – Siêu máy tính tối thượng thôi!",
    Level = 6
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "BỘ ÓC SIÊU VIỆT",
    Message = "Đánh thắng Kiện tướng Cấp 6? Bạn là người hay thần vậy? Bước lên Cấp 7 để đối đầu với 'Chúa cờ' đi!",
    Level = 6
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "CHÂN LÝ THUỘC VỀ BẠN",
    Message = "Mọi đòn hy sinh quân của Cấp 6 đều bị bạn bẻ gãy! Cấp 7 đang đợi để phục thù cho đồng đội đấy.",
    Level = 6
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "QUẢ LÀ KỲ TÍCH",
    Message = "Cấp 6 ngã gục! Bạn đã đứng ở đỉnh cao của loài người rồi. Giờ hãy khiêu chiến Cấp 7 – Siêu AI!",
    Level = 6
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "KHIẾN AI BỐC KHÓI",
    Message = "Hệ thống Cấp 6 vừa quá tải vì không tính nổi nước đi của bạn! Cấp 7 sẵn sàng băm vằn bạn rồi đấy.",
    Level = 6
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "ĐÁNH BẠI HUYỀN THOẠI",
    Message = "Chiến thắng Cấp 6 khẳng định bạn là cao thủ thực sự. Liệu bạn có đủ sức sống sót ở Cấp 7?",
    Level = 6
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "CÁC BẠN LÀ BÁ CHỦ",
    Message = "Đã đến lúc bước vào trận chiến cuối cùng! Cấp 7 - nơi chưa từng ai dám mơ tới chiến thắng.",
    Level = 6
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "NƯỚC ĐI XUYÊN KHÔNG",
    Message = "Nhìn bạn đè bẹp Cấp 6 thật sự mãn nhãn! Bấm vào Cấp 7 để viết nên trang sử mới nào!",
    Level = 6
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "ĐẮNG LÒNG KIỆN TƯỚNG",
    Message = "Cấp 6 đành chịu trút giận vào hư không. Tiến lên Cấp 7 để khiêu chiến đấng sáng tạo cờ vua!",
    Level = 6
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "SẮP CHẠM ĐỈNH CAO",
    Message = "Thắng Cấp 6 là tấm vé bước vào ngôi đền huyền thoại. Cấp 7 đang chờ đón bạn!",
    Level = 6
    ),

    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "PHI LÝ! BẤT KHẢ THI!",
    Message = "BẠN ĐÃ THẮNG CẤP 7?! Thuật toán 10 triệu nước đi bị đánh bại sao? Bạn chắc chắn đã hack game rồi đúng không?!",
    Level = 7
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "NGÀY TẬN THẾ CỦA AI",
    Message = "Siêu máy tính Cấp 7 đã sụp đổ hoàn toàn trước bộ óc của bạn. Xin quỳ gối bái phục ĐẠI KIỆN TƯỚNG!",
    Level = 7
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "CÓ CHẮC KHÔNG HACK?",
    Message = "Tôi vừa kiểm tra lại code... Cấp 7 không thể thua được mà! Bạn là Magnus Carlsen giấu tên phải không?",
    Level = 7
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "ĐỈNH CAO CỦA NHÂN LOẠI",
    Message = "Thắng Cấp 7! Bạn đã vượt qua giới hạn trí tuệ loài người và khuất phục cỗ máy tối thượng này!",
    Level = 7
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "LỖI HỆ THỐNG nghiêm trọng",
    Message = "Hệ thống đang bốc khói vì kinh ngạc! Chiến thắng Cấp 7 này sẽ đi vào lịch sử game cờ vua!",
    Level = 7
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "HẮN LÀ QUÁI VẬT!",
    Message = "Xin ngả nón bái phục! Bạn vừa biến Siêu AI Cấp 7 thành trò đùa!",
    Level = 7
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "MAY MẮN TRONG 1 TỶ?",
    Message = "Thắng được Cấp 7 sao?! Chắc chắn là do bạn may mắn đúng 0.00001%... hoặc bạn là Thần Cờ tái thế!",
    Level = 7
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "KHÔNG CÒN GÌ ĐỂ DẠY",
    Message = "Bạn đã phá đảo game! Cấp 7 tối thượng cũng bó tay, từ nay game này thuộc về bạn!",
    Level = 7
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "AI XIN ĐẦU HÀNG",
    Message = "Toàn bộ máy chủ đang nghiêng mình trước bạn. Cấp 7 chính thức bị khuất phục hoàn toàn!",
    Level = 7
    ),
    MessageEndGame(
    Mode = "AI", IsWon = true,
    Title = "ẢO THẬT ĐẤY!",
    Message = "Thắng Cấp 7 à? Đừng hòng lừa tôi, bạn dùng tool đúng không? ...Cái gì, là đánh thật sao?! BÁ ĐẠO!",
    Level = 7
    ),

        MessageEndGame(
            Mode = "AI",
            IsWon = false,
            Title = "THẬT KHÔNG THỂ TIN NỔI",
            Message = "Thế mà cũng thoát được sao? Đấu lại ván nữa để tôi tiễn bạn rời bàn cờ nhé!"
        ),
        MessageEndGame(
            Mode = "AI",
            IsWon = false,
            Title = "HÒA TRONG THẾ THUA?",
            Message = "Nối gót may mắn nhờ luật hết nước đi à? Vào gỡ gạc ngay để xem thực lực thật tới đâu nào!"
        ),
        MessageEndGame(
            Mode = "AI",
            IsWon = false,
            Title = "BẤT PHÂN THẮNG BẠI?",
            Message = "Hòa mà mặt bạn ngơ ngác thế? Làm ván nữa để phân định ai mới là chủ nhân bàn cờ này!"
        ),
        MessageEndGame(
            Mode = "AI",
            IsWon = false,
            Title = "SUỐT CHÚT LÀ ĂN ĐÒN",
            Message = "Bắt bài được vài nước rồi tự đắc sao? Chơi lại ván nữa tôi sẽ cho bạn biết thế nào là bế tắc!"
        ),
        MessageEndGame(
            Mode = "AI",
            IsWon = false,
            Title = "HÒA KHÔNG VUI TẸO NÀO",
            Message = "Chạy cờ giữ mạng giỏi đấy! Nhưng lần sau thì không có chuyện thoát được đâu, tái đấu đi!"
        ),
        MessageEndGame(
            Mode = "AI",
            IsWon = false,
            Title = "MÁY TÍNH NHƯỜNG BẠN ĐẤY",
            Message = "Xem như tôi thả cho ván này hòa đấy. Ngon thì bấm 'Chơi lại' xem có sống sót nổi không!"
        ),
        MessageEndGame(
            Mode = "AI",
            IsWon = false,
            Title = "CỜ BÍ NƯỚC RỒI SAO?",
            Message = "Ép tôi vào thế hòa cơ à? Bài này cũ rồi, tái đấu ngay để nếm trải chiến thuật mới của tôi!"
        ),
        MessageEndGame(
            Mode = "AI",
            IsWon = false,
            Title = "THỜI NÀY AI CHƠI HÒA?",
            Message = "Sống sót qua bàn cờ này không làm bạn trở thành cao thủ đâu. Vào làm ván mới phân thắng bại đi!"
        ),
        MessageEndGame(
            Mode = "AI",
            IsWon = false,
            Title = "THẢ CÁ BẮT TÔM",
            Message = "Một ván hòa may mắn thôi! Đừng vội mừng, thử bấm chơi lại xem lượt này ai sẽ ngã gục!"
        ),
        MessageEndGame(
            Mode = "AI",
            IsWon = false,
            Title = "MAY CHO BẠN ĐẤY",
            Message = "Chỉ thiếu 1 nước nữa là tôi chiếu hết bạn rồi. Làm ván nữa để tôi hoàn thành nốt công việc nhé!"
        ),

    MessageEndGame(
        Mode = "PUZZLES",
    IsWon = true,
    Title = "BỘ ÓC SIÊU VIỆT!",
    Message = "Thế cờ hóc húa vậy mà bạn nhìn ra nước đi trong một nốt nhạc sao? Mắt thần đấy à!"

    ),
    MessageEndGame(
        Mode = "PUZZLES",
    IsWon = true,
    Title = "HACK MẮT À?",
    Message = "Nước cờ độc lạ đấy! Tác giả thế cờ này đang khóc trong góc vì bị bạn giải quá dễ dàng."
    ),
    MessageEndGame(
        Mode = "PUZZLES",
    IsWon = true,
    Title = "BẢN THIÊN TÀI!",
    Message = "Tìm ra nước đi duy nhất để kết liễu thế cờ! Đỉnh cao tư duy, tiếp tục thế cờ tiếp theo nào."
    ),
    MessageEndGame(
        Mode = "PUZZLES",
    IsWon = true,
    Title = "ĂN RỜI MẤY HẠT BỤI",
    Message = "Thế cờ này tưởng khó hóa ra lại quá muỗi đối với bạn. Cho bài khó hơn đi game ơi!"
    ),
    MessageEndGame(
        Mode = "PUZZLES",
    IsWon = true,
    Title = "ĐÒN HY SINH ĐẸP MẮT!",
    Message = "Bỏ quân lấy thế quá mãn nhãn! Bạn giải cờ mà như đang biểu diễn nghệ thuật vậy."
    ),
    MessageEndGame(
        Mode = "PUZZLES",
    IsWon = true,
    Title = "MAY MẮN HAY THỰC LỰC?",
    Message = "Giải đúng rồi đấy! Nhưng là do bạn tính toán thật hay bấm bừa trúng bẫy vậy?"
    ),
    MessageEndGame(
        Mode = "PUZZLES",
    IsWon = true,
    Title = "TÌM THẤY ĐÁP ÁN!",
    Message = "Nước đi chuẩn chỉnh từng cen-ti-mét! Bộ óc của bạn sinh ra là để giải cờ rồi."
    ),
    MessageEndGame(
        Mode = "PUZZLES",
    IsWon = true,
    Title = "BẠN LÀ MÁY TÍNH À?",
    Message = "Chỉ mất vài giây để phá giải bẫy cờ tinh vi này. Quá bá đạo!"
    ),
    MessageEndGame(
        Mode = "PUZZLES",
    IsWon = true,
    Title = "XUYÊN THỦNG BẾ TẮC",
    Message = "Tất cả mọi người đều bó tay ở nước này, ngoại trừ bạn. Bái phục!"
    ),
    MessageEndGame(
        Mode = "PUZZLES",
    IsWon = true,
    Title = "CHINH PHỤC THÀNH CÔNG",
    Message = "Thêm một thế cờ nằm gọn dưới chân bạn. Tiến lên câu tiếp theo để khẳng định vị thế nào!"
    ),

    MessageEndGame(
        Mode = "PUZZLES",
    IsWon = false,
    Title = "MẮT MÃI TRÊN MÂY",
    Message = "Nước đi duy nhất đúng thì bạn né, nước đi vô lý nhất thì bạn lại chọn. Tài thật!"
    ),
    MessageEndGame(
        Mode = "PUZZLES",
    IsWon = false,
    Title = "BẬC THẦY VÀO BẪY",
    Message = "Tác giả cài bẫy nhẹ hều mà bạn lao thẳng vào như một vị thần. Thử lại ngay!"
    ),
    MessageEndGame( Mode = "PUZZLES",
    IsWon = false,
    Title = "QUÁ NHANH QUÁ NGUY HẠI",
    Message = "Chưa suy nghĩ đã đi quân rồi à? Cờ Puzzle là để ngẫm, không phải để đua tốc độ đâu!"
    ),
    MessageEndGame( Mode = "PUZZLES",
    IsWon = false,
    Title = "CÁI BẪY QUÁ TINH VI",
    Message = "Nước cờ này đánh lừa được rất nhiều người, và bạn là nạn nhân tiếp theo. Đừng nản, xếp lại cờ!"
    ),
    MessageEndGame( Mode = "PUZZLES",
    IsWon = false,
    Title = "THIẾU MỘT CHÚT TINH TẾ",
    Message = "Ý tưởng tốt đấy, nhưng chọn sai thứ tự quân rồi! Chỉ cần chỉnh lại một xíu là ra ngay."
    ),
    MessageEndGame( Mode = "PUZZLES",
    IsWon = false,
    Title = "CÒN RẤT GẦN ĐÁP ÁN",
    Message = "Bạn đã đi đúng 90% chặng đường rồi, nước cuối lại dâng cờ cho giặc. Bình tĩnh nhìn lại nào!"
    ),
    MessageEndGame( Mode = "PUZZLES",
    IsWon = false,
    Title = "BÍ NƯỚC RỒI SAO?",
    Message = "Càng nhìn càng thấy rối đúng không? Hít một hơi thật sâu và thử quan sát theo hướng khác xem."
    ),
    MessageEndGame( Mode = "PUZZLES",
    IsWon = false,
    Title = "TỰ SÁT CHIẾN THUẬT",
    Message = "Bạn vừa cúng không Hậu cho đối phương trong một bài giải cờ... Cố gắng ván sau nhé!"
    ),
    MessageEndGame( Mode = "PUZZLES",
    IsWon = false,
    Title = "SAI MỘT LY ĐI MỘT DẶM",
    Message = "Chỉ cần chệch một nước là cả thế cờ đổ bể. Thử lại lần nữa xem độ nhạy bén đến đâu!"
    ),
    MessageEndGame( Mode = "PUZZLES",
    IsWon = false,
    Title = "CẦN LẮM MỘT GỢI Ý?",
    Message = "Thế cờ này hơi quá sức chăng? Đừng bỏ cuộc, thiên tài cũng từng vấp ngã ở câu này mà!"
    ),
    MessageEndGame( Mode = "ONE_MOVE",
    IsWon = true,
    Title = "NHANH NHƯ CHỚP!",
    Message = "Một nước kết liễu! Mắt bạn tinh đấy, nhìn cái ra ngay điểm yếu của đối thủ."
    ),
    MessageEndGame( Mode = "ONE_MOVE",
    IsWon = true,
    Title = "CHƯA KỊP THỞ!",
    Message = "Nhanh gọn lẹ! Đối phương còn chưa kịp hiểu chuyện gì xảy ra thì đã ăn đòn chiếu hết."
    ),
    MessageEndGame( Mode = "ONE_MOVE",
    IsWon = true,
    Title = "MẤT 1 GIÂY À?",
    Message = "Bài toán 1 nước này hơi muỗi đúng không? Tiến lên bài tiếp theo để xem tốc độ của bạn nào!"
    ),
    MessageEndGame( Mode = "ONE_MOVE",
    IsWon = true,
    Title = "MẮT THẦN DẠO CHƠI",
    Message = "Bắt trúng huyệt hiểm rồi! Nước đi chuẩn chỉnh như lập trình sẵn."
    ),
    MessageEndGame( Mode = "ONE_MOVE",
    IsWon = true,
    Title = "ĐÁNH ĐÚNG ĐIỂM MÙ",
    Message = "Đối thủ giấu Vua kỹ thế mà vẫn bị bạn chọc đúng một nước là xong. Bá đạo!"
    ),
    MessageEndGame( Mode = "ONE_MOVE",
    IsWon = true,
    Title = "MAY MẮN HAY BÁ ĐẠO?",
    Message = "Chiếu hết trong 1 nước đi quá hoàn hảo! Bấm đại hay có tính toán kỹ đấy?"
    ),
    MessageEndGame( Mode = "ONE_MOVE",
    IsWon = true,
    Title = "KẾT LIỄU TRONG NỐT NHẠC",
    Message = "Không một động tác thừa! Đỉnh cao của sự tinh tế nằm ở nước cờ vừa rồi."
    ),
    MessageEndGame( Mode = "ONE_MOVE",
    IsWon = true,
    Title = "TẠO PHONG CÁCH MỚI",
    Message = "Dùng đúng 1 nước đi để hạ gục bàn cờ! Tiếp tục giữ phong độ cho câu sau nhé."
    ),
    MessageEndGame( Mode = "ONE_MOVE",
    IsWon = true,
    Title = "BỘ ÓC SẮC BÉN",
    Message = "Nhìn phát biết ngay nước chiếu! Trí tuệ nhạy bén thế này thì game nào làm khó được bạn."
    ),
    MessageEndGame( Mode = "ONE_MOVE",
    IsWon = true,
    Title = "XONG PHIM!",
    Message = "Một đòn duy nhất và cuộc chơi kết thúc. Quá xuất sắc, thử sức với câu tiếp theo ngay!"
    ),

    MessageEndGame( Mode = "ONE_MOVE",
    IsWon = false,
    Title = "CÓ MỖI 1 NƯỚC CŨNG SAI?",
    Message = "Có đúng một nước đi để thắng thôi mà bạn cũng chọn sai được. Tài năng thiên bẩm đấy!"
    ),
    MessageEndGame( Mode = "ONE_MOVE",
    IsWon = false,
    Title = "MẮT NHẮM MẮT MỞ?",
    Message = "Vua đối phương đứng chềnh ềnh ra đó mà bạn lại đi quân đi đâu thế kia?"
    ),
    MessageEndGame( Mode = "ONE_MOVE",
    IsWon = false,
    Title = "CẦN KÍNH LÚP KHÔNG?",
    Message = "Nhìn lại bàn cờ lần nữa nào! Ô chiếu hết ngon ăn thế mà lại bỏ qua sao?"
    ),
    MessageEndGame( Mode = "ONE_MOVE",
    IsWon = false,
    Title = "NINJA NÉ ĐÁP ÁN",
    Message = "Bạn vừa né đáp án đúng một cách thần kỳ. Thử lại và tập trung vào quân Vua xem nào!"
    ),
    MessageEndGame( Mode = "ONE_MOVE",
    IsWon = false,
    Title = "TAY NHANH HƠN CÁI ĐẦU",
    Message = "Bấm vội quá rồi đấy! Bình tĩnh quan sát kỹ toàn bàn cờ trước khi đặt quân nhé."
    ),
    MessageEndGame( Mode = "ONE_MOVE",
    IsWon = false,
    Title = "CẠM BẪY 1 NƯỚC",
    Message = "Trúng bẫy rồi! Nước đó tưởng chiếu nhưng lại bị cản. Nhìn sâu hơn một xíu đi!"
    ),
    MessageEndGame( Mode = "ONE_MOVE",
    IsWon = false,
    Title = "SUY NGHĨ BAY BỔNG",
    Message = "Đừng phức tạp hóa vấn đề. Bài này cực kỳ đơn giản, chỉ cần một đòn trực diện thôi!"
    ),
    MessageEndGame( Mode = "ONE_MOVE",
    IsWon = false,
    Title = "THUA TRONG TÍCH TẮC",
    Message = "Chưa kịp nhìn hết bàn cờ đã dâng chiến thắng cho máy rồi. Xếp lại cờ gỡ gạc ngay!"
    ),
    MessageEndGame( Mode = "ONE_MOVE",
    IsWon = false,
    Title = "CẦN TỚI TẬP BỘI?",
    Message = "Nước chiếu ngon ăn thế mà lại hụt. Hít một hơi thật sâu rồi chọn lại quân cờ khác xem."
    ),
    MessageEndGame( Mode = "ONE_MOVE",
    IsWon = false,
    Title = "CƠ HỘI BỤT MẤT",
    Message = "Chỉ một nước nữa là làm vua, vậy mà bạn chọn làm bại tướng. Thử lại lần nữa nào!"
    )
    )
}