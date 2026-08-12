package com.example.chess.data

data class Puzzles(
    val level: Int,
    val fen: String,
    val firstMove: String,
    val isWhite: Boolean
)