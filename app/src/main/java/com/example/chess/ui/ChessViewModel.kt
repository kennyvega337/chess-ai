package com.example.chess.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chess.audio.SoundManager
import com.example.chess.engine.ChessAI
import com.example.chess.engine.ChessBoard
import com.example.chess.model.GameStatus
import com.example.chess.model.Move
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.model.Position
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChessUiState(
    val board: ChessBoard = ChessBoard(),
    val userColor: PieceColor = PieceColor.WHITE,
    val currentTurn: PieceColor = PieceColor.WHITE,
    val selectedPosition: Position? = null,
    val legalMovesForSelected: List<Move> = emptyList(),
    val moveHistory: List<Move> = emptyList(),
    val capturedWhitePieces: List<PieceType> = emptyList(),
    val capturedBlackPieces: List<PieceType> = emptyList(),
    val gameStatus: GameStatus = GameStatus.NOT_STARTED,
    val winner: PieceColor? = null,
    val isCheck: Boolean = false,
    val showCheckPopup: Boolean = false,
    val lastMove: Move? = null,
    val isAiThinking: Boolean = false,
    val showSideSelectionModal: Boolean = true,
    val showResignConfirmationModal: Boolean = false,
    val pendingPromotionMove: Move? = null,
    val hintMove: Move? = null
)

class ChessViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChessUiState())
    val uiState: StateFlow<ChessUiState> = _uiState.asStateFlow()

    fun selectSide(chosenColor: PieceColor) {
        val newBoard = ChessBoard()
        _uiState.value = ChessUiState(
            board = newBoard,
            userColor = chosenColor,
            currentTurn = PieceColor.WHITE,
            gameStatus = GameStatus.IN_PROGRESS,
            showSideSelectionModal = false,
            hintMove = null
        )

        // If user chose Black, AI plays White first!
        if (chosenColor == PieceColor.BLACK) {
            triggerAiMove(PieceColor.WHITE)
        }
    }

    fun openSideSelectionModal() {
        _uiState.value = _uiState.value.copy(showSideSelectionModal = true)
    }

    fun closeSideSelectionModal() {
        if (_uiState.value.gameStatus == GameStatus.IN_PROGRESS) {
            _uiState.value = _uiState.value.copy(showSideSelectionModal = false)
        }
    }

    fun onSquareClick(pos: Position) {
        val currentState = _uiState.value
        if (currentState.gameStatus != GameStatus.IN_PROGRESS ||
            currentState.currentTurn != currentState.userColor ||
            currentState.isAiThinking ||
            currentState.showSideSelectionModal
        ) {
            return
        }

        val board = currentState.board
        val selected = currentState.selectedPosition

        if (selected != null) {
            // Check if clicking on a legal move destination
            val matchingMove = currentState.legalMovesForSelected.find { it.to == pos }
            if (matchingMove != null) {
                // Check if promotion choice is needed
                if (matchingMove.piece.type == PieceType.PAWN &&
                    ((matchingMove.piece.color == PieceColor.WHITE && pos.row == 0) ||
                     (matchingMove.piece.color == PieceColor.BLACK && pos.row == 7))
                ) {
                    _uiState.value = currentState.copy(pendingPromotionMove = matchingMove)
                    return
                }

                executeUserMove(matchingMove)
                return
            }

            // Clicked on another piece of user's color
            val clickedPiece = board.getPiece(pos)
            if (clickedPiece != null && clickedPiece.color == currentState.userColor) {
                val legalMoves = board.getLegalMovesForPosition(pos)
                _uiState.value = currentState.copy(
                    selectedPosition = pos,
                    legalMovesForSelected = legalMoves,
                    hintMove = null
                )
                return
            }

            // Clicked empty square or opponent piece (not a legal move) -> deselect
            _uiState.value = currentState.copy(
                selectedPosition = null,
                legalMovesForSelected = emptyList(),
                hintMove = null
            )
        } else {
            // Select piece
            val clickedPiece = board.getPiece(pos)
            if (clickedPiece != null && clickedPiece.color == currentState.userColor) {
                val legalMoves = board.getLegalMovesForPosition(pos)
                _uiState.value = currentState.copy(
                    selectedPosition = pos,
                    legalMovesForSelected = legalMoves,
                    hintMove = null
                )
            }
        }
    }

    fun completePromotion(promotionType: PieceType) {
        val pendingMove = _uiState.value.pendingPromotionMove ?: return
        val finalMove = pendingMove.copy(promotion = promotionType)
        _uiState.value = _uiState.value.copy(pendingPromotionMove = null)
        executeUserMove(finalMove)
    }

    private fun executeUserMove(move: Move) {
        val currentState = _uiState.value
        val board = currentState.board.copy()
        board.applyMove(move)

        val updatedHistory = currentState.moveHistory + move
        val (capWhite, capBlack) = updateCapturedPieces(currentState, move)

        val opponent = currentState.userColor.opposite
        val opponentLegalMoves = board.getLegalMoves(opponent)
        val opponentInCheck = board.isKingInCheck(opponent)

        var newStatus = GameStatus.IN_PROGRESS
        var winner: PieceColor? = null

        if (opponentLegalMoves.isEmpty()) {
            if (opponentInCheck) {
                newStatus = GameStatus.CHECKMATE
                winner = currentState.userColor
            } else {
                newStatus = GameStatus.STALEMATE
            }
        }

        // Play piece move sound
        val isCapture = move.capturedPiece != null
        SoundManager.playMoveSound(
            pieceType = move.piece.type,
            isCapture = isCapture,
            isCheck = opponentInCheck
        )

        if (newStatus == GameStatus.CHECKMATE && winner == currentState.userColor) {
            SoundManager.playVictorySound()
        }

        _uiState.value = currentState.copy(
            board = board,
            selectedPosition = null,
            legalMovesForSelected = emptyList(),
            moveHistory = updatedHistory,
            capturedWhitePieces = capWhite,
            capturedBlackPieces = capBlack,
            currentTurn = opponent,
            gameStatus = newStatus,
            winner = winner,
            isCheck = opponentInCheck,
            showCheckPopup = (opponentInCheck && newStatus == GameStatus.IN_PROGRESS),
            lastMove = move,
            hintMove = null
        )

        if (newStatus == GameStatus.IN_PROGRESS) {
            triggerAiMove(opponent)
        }
    }

    private fun triggerAiMove(aiColor: PieceColor) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAiThinking = true)
            delay(1000) // Delay 1 second before AI makes a move

            val currentState = _uiState.value
            val board = currentState.board.copy()
            val ai = ChessAI(aiColor)
            val aiMove = ai.chooseMove(board)

            if (aiMove != null) {
                board.applyMove(aiMove)
                val updatedHistory = currentState.moveHistory + aiMove
                val (capWhite, capBlack) = updateCapturedPieces(currentState, aiMove)

                val userColor = currentState.userColor
                val userLegalMoves = board.getLegalMoves(userColor)
                val userInCheck = board.isKingInCheck(userColor)

                var newStatus = GameStatus.IN_PROGRESS
                var winner: PieceColor? = null

                if (userLegalMoves.isEmpty()) {
                    if (userInCheck) {
                        newStatus = GameStatus.CHECKMATE
                        winner = aiColor // User loses
                    } else {
                        newStatus = GameStatus.STALEMATE
                    }
                }

                // Play AI move sound
                val isCapture = aiMove.capturedPiece != null
                SoundManager.playMoveSound(
                    pieceType = aiMove.piece.type,
                    isCapture = isCapture,
                    isCheck = userInCheck
                )

                if (newStatus == GameStatus.CHECKMATE && winner == currentState.userColor) {
                    SoundManager.playVictorySound()
                }

                _uiState.value = currentState.copy(
                    board = board,
                    moveHistory = updatedHistory,
                    capturedWhitePieces = capWhite,
                    capturedBlackPieces = capBlack,
                    currentTurn = userColor,
                    gameStatus = newStatus,
                    winner = winner,
                    isCheck = userInCheck,
                    showCheckPopup = (userInCheck && newStatus == GameStatus.IN_PROGRESS),
                    lastMove = aiMove,
                    isAiThinking = false
                )
            } else {
                // AI has no moves -> Stalemate or user checkmated AI
                _uiState.value = currentState.copy(isAiThinking = false)
            }
        }
    }

    private fun updateCapturedPieces(state: ChessUiState, move: Move): Pair<List<PieceType>, List<PieceType>> {
        val capWhite = state.capturedWhitePieces.toMutableList()
        val capBlack = state.capturedBlackPieces.toMutableList()

        move.capturedPiece?.let { captured ->
            if (captured.color == PieceColor.WHITE) {
                capWhite.add(captured.type)
            } else {
                capBlack.add(captured.type)
            }
        }
        return Pair(capWhite, capBlack)
    }

    fun undoMove() {
        val currentState = _uiState.value
        if (currentState.isAiThinking || currentState.moveHistory.isEmpty()) return

        // Undo 2 moves (AI move + User move) if user's turn, or 1 move if user just moved
        val movesToPop = if (currentState.moveHistory.size >= 2) 2 else 1
        val newHistory = currentState.moveHistory.dropLast(movesToPop)

        // Replay board from scratch
        val newBoard = ChessBoard()
        var capWhite = mutableListOf<PieceType>()
        var capBlack = mutableListOf<PieceType>()
        var lastM: Move? = null

        for (m in newHistory) {
            m.capturedPiece?.let { c ->
                if (c.color == PieceColor.WHITE) capWhite.add(c.type) else capBlack.add(c.type)
            }
            newBoard.applyMove(m)
            lastM = m
        }

        val userInCheck = newBoard.isKingInCheck(currentState.userColor)

        _uiState.value = currentState.copy(
            board = newBoard,
            selectedPosition = null,
            legalMovesForSelected = emptyList(),
            moveHistory = newHistory,
            capturedWhitePieces = capWhite,
            capturedBlackPieces = capBlack,
            currentTurn = currentState.userColor,
            gameStatus = GameStatus.IN_PROGRESS,
            winner = null,
            isCheck = userInCheck,
            lastMove = lastM,
            isAiThinking = false,
            hintMove = null
        )
    }

    fun showHint() {
        val state = _uiState.value
        if (state.gameStatus != GameStatus.IN_PROGRESS ||
            state.currentTurn != state.userColor ||
            state.isAiThinking
        ) return

        val ai = ChessAI(state.userColor)
        val hint = ai.chooseMove(state.board) ?: return
        val legalMoves = state.board.getLegalMovesForPosition(hint.from)

        _uiState.value = state.copy(
            selectedPosition = hint.from,
            legalMovesForSelected = legalMoves,
            hintMove = hint
        )
    }

    fun requestResign() {
        val state = _uiState.value
        if (state.gameStatus != GameStatus.IN_PROGRESS || state.isAiThinking) return
        _uiState.value = state.copy(showResignConfirmationModal = true)
    }

    fun cancelResign() {
        _uiState.value = _uiState.value.copy(showResignConfirmationModal = false)
    }

    fun confirmResign() {
        val state = _uiState.value
        if (state.gameStatus != GameStatus.IN_PROGRESS || state.isAiThinking) return
        val opponentColor = if (state.userColor == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE
        _uiState.value = state.copy(
            gameStatus = GameStatus.RESIGNED,
            winner = opponentColor,
            selectedPosition = null,
            legalMovesForSelected = emptyList(),
            showResignConfirmationModal = false
        )
    }

    fun dismissCheckPopup() {
        _uiState.value = _uiState.value.copy(showCheckPopup = false)
    }
}
