package com.example.chess.ui

import android.graphics.Matrix
import androidx.compose.ui.geometry.Offset

class PerspectiveBoardMapper(
    private val boardSizePx: Float
) {
    private val matrix = Matrix()
    private val scaleX = boardSizePx / RoyalBoardData.IMAGE_WIDTH
    private val scaleY = boardSizePx / RoyalBoardData.IMAGE_HEIGHT

    init {
        // Ánh xạ 8x8 logic sang hình thang thực tế
        val src = floatArrayOf(
            0f, 0f, 8f, 0f, 8f, 8f, 0f, 8f
        )
        val dst = floatArrayOf(
            RoyalBoardData.TOP_LEFT_X, RoyalBoardData.TOP_LEFT_Y,
            RoyalBoardData.TOP_RIGHT_X, RoyalBoardData.TOP_RIGHT_Y,
            RoyalBoardData.BOTTOM_RIGHT_X, RoyalBoardData.BOTTOM_RIGHT_Y,
            RoyalBoardData.BOTTOM_LEFT_X, RoyalBoardData.BOTTOM_LEFT_Y
        )
        matrix.setPolyToPoly(src, 0, dst, 0, 4)
    }

    fun getCellCenter(vRow: Int, vCol: Int): Offset {
        val pts = floatArrayOf(vCol + 0.5f, vRow + 0.5f)
        matrix.mapPoints(pts)
        return Offset(pts[0] * scaleX, pts[1] * scaleY)
    }

    fun getCellCorners(vRow: Int, vCol: Int): List<Offset> {
        val r = vRow.toFloat()
        val c = vCol.toFloat()
        val pts = floatArrayOf(
            c, r, c + 1f, r, c + 1f, r + 1f, c, r + 1f
        )
        matrix.mapPoints(pts)
        return listOf(
            Offset(pts[0] * scaleX, pts[1] * scaleY),
            Offset(pts[2] * scaleX, pts[3] * scaleY),
            Offset(pts[4] * scaleX, pts[5] * scaleY),
            Offset(pts[6] * scaleX, pts[7] * scaleY)
        )
    }

    fun getPieceScale(vRow: Int): Float {
        val pts = floatArrayOf(0f, vRow.toFloat(), 8f, vRow.toFloat())
        matrix.mapPoints(pts)
        val rowWidth = pts[2] - pts[0]
        val baseWidth = RoyalBoardData.TOP_RIGHT_X - RoyalBoardData.TOP_LEFT_X
        return (rowWidth / baseWidth) * 1.0f
    }
}
