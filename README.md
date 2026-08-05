# ⚔️ Dự Án Cờ Vua Trung Cổ (Jetpack Compose & Kotlin)

Chào mừng bạn! File này hướng dẫn cách đọc hiểu mã nguồn dành cho lập trình viên đã biết **C#** hoặc **Java**.

## 1. Cấu trúc Dự án
Dự án tuân theo kiến trúc **MVVM (Model-View-ViewModel)**:

- `model/`: Định nghĩa dữ liệu (Data Classes).
- `engine/`: Logic xử lý bàn cờ (Luật chơi, AI).
- `ui/`: Giao diện người dùng (Jetpack Compose).
- `data/`: Quản lý lưu trữ (Lịch sử đấu, Chủ đề).

## 2. Jetpack Compose dành cho người mới (C#/Java Dev)

### `@Composable` là gì?
Trong Java/C#, bạn tạo giao diện bằng Class. Trong Compose, giao diện là **Hàm**.
Hàm nào có đánh dấu `@Composable` nghĩa là nó có thể vẽ ra màn hình.

### Luồng Dữ Liệu (Unidirectional Data Flow)
1. **ViewModel** giữ dữ liệu (ví dụ: danh sách quân cờ).
2. **UI (Composable)** đọc dữ liệu này và vẽ ra.
3. Khi người dùng click, UI gọi một hàm trong **ViewModel**.
4. **ViewModel** cập nhật dữ liệu -> **UI** tự động chạy lại (Recomposition) để hiển thị thay đổi.

## 3. Các File Quan Trọng Cần Đọc

### 🧠 ChessViewModel.kt (Bộ não)
- Sử dụng `MutableStateFlow`: Giống như `ObservableCollection` hoặc `Property` có thông báo trong C#.
- Hàm `onSquareClick`: Xử lý khi người dùng chạm vào bàn cờ.

### 🎨 ChessScreen.kt (Màn hình chính)
- Sử dụng `Scaffold`: Một khung chuẩn của Android (gồm TopBar và nội dung).
- Chia layout 3 phần (Top, Middle, Bottom) bằng `Column` và `Arrangement.SpaceBetween`.

### 🏰 ChessBoard3D.kt & PerspectiveBoardMapper.kt (Xử lý 3D)
Đây là phần nâng cao nhất của dự án:
- **PerspectiveBoardMapper**: Sử dụng lớp `android.graphics.Matrix` để thực hiện phép toán **Homography**. Nó ánh xạ tọa độ 2D (0-7) sang hình thang 3D trên ảnh PNG dựa trên các mốc pixel chính xác.
- **Depth Scaling**: Tính toán để quân cờ ở xa trông nhỏ hơn quân cờ ở gần.
- **Base Anchoring**: Sử dụng `transformOrigin` để khi quân cờ xoay đứng (`rotationX`), chân cờ vẫn bám đúng tâm ô cờ.

## 4. So sánh khái niệm

| Khái niệm | Java/C# truyền thống | Jetpack Compose |
| :--- | :--- | :--- |
| Định nghĩa UI | XML hoặc Designer | Hàm Kotlin (`@Composable`) |
| Thay đổi UI | `view.setText("Hi")` | Cập nhật biến State -> UI tự vẽ lại |
| Chứa logic | Controller / Presenter | ViewModel |
| Container dọc | `LinearLayout (Vertical)` | `Column` |
| Container ngang | `LinearLayout (Horizontal)` | `Row` |
| Đè lên nhau | `FrameLayout` | `Box` |

## 5. Cách bắt đầu chỉnh sửa
1. Nếu muốn đổi màu sắc/giao diện: Xem `ui/theme/Color.kt`.
2. Nếu muốn đổi tọa độ bàn cờ 3D: Xem `PerspectiveBoardMapper.kt`.
3. Nếu muốn thêm luật chơi: Xem `engine/ChessBoard.kt`.

---
Chúc bạn có trải nghiệm lập trình thú vị với Kotlin!
