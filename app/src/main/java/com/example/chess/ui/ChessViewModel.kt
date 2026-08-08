package com.example.chess.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chess.audio.SoundManager
import com.example.chess.data.GameHistoryItem
import com.example.chess.data.GameHistoryManager
import com.example.chess.engine.ChessAI
import com.example.chess.engine.ChessBoard
import com.example.chess.engine.StockfishEngine
import com.example.chess.data.ChessThemeManager
import com.example.chess.model.AppScreen
import com.example.chess.model.ChessTheme
import com.example.chess.model.DifficultyLevel
import com.example.chess.model.GameMode
import com.example.chess.model.GameStatus
import com.example.chess.model.Move
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.model.Position
import com.example.chess.model.SideOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChessUiState(
    val currentScreen: AppScreen = AppScreen.SETUP,
    val selectedSideOption: SideOption = SideOption.WHITE,
    val gameMode: GameMode = GameMode.VS_AI,
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
    val playerLastMove: Move? = null,
    val aiLastMove: Move? = null,
    val difficulty: DifficultyLevel = DifficultyLevel.LEVEL_2,
    val isAiThinking: Boolean = false,
    val showSideSelectionModal: Boolean = false,
    val showResignConfirmationModal: Boolean = false,
    val showCapturedPiecesModal: Boolean = false,
    val showHistoryModal: Boolean = false,
    val showThemeModal: Boolean = false,
    val showGameOverModal: Boolean = false,
    val selectedTheme: ChessTheme = ChessTheme.CLASSIC,
    val pendingPromotionMove: Move? = null,
    val hintMove: Move? = null,
    val tutorialPiece: PieceType? = null,
    val boardViewMode: com.example.chess.model.BoardViewMode = com.example.chess.model.BoardViewMode.VIEW_2D,
    val checkingPieces: List<Position> = emptyList(),
    val halfMoveClock: Int = 0,
    val boardSignatures: Map<String, Int> = emptyMap(),
)

class ChessViewModel(application: Application) : AndroidViewModel(application) {

    private val historyManager = GameHistoryManager(application)
    private val themeManager = ChessThemeManager(application)
    private var hasSavedHistoryForMatch = false

    private val _uiState = MutableStateFlow(ChessUiState(
        selectedTheme = themeManager.getSelectedTheme(),
        boardViewMode = themeManager.getSelectedViewMode(),
        gameMode = themeManager.getSelectedGameMode(),
        difficulty = themeManager.getSelectedDifficulty(),
        selectedSideOption = themeManager.getSelectedSideOption()
    ))
    val uiState: StateFlow<ChessUiState> = _uiState.asStateFlow()

    private fun recordMatchHistory(
        isQuitOrAppClosed: Boolean = false,
        forcedGameMode: GameMode? = null,
        forcedUserColor: PieceColor? = null,
        forcedStatus: GameStatus? = null,
        forcedWinner: PieceColor? = null
    ) {
        val state = _uiState.value
        val mode = forcedGameMode ?: state.gameMode

        if (mode == GameMode.TUTORIAL) return
        if (hasSavedHistoryForMatch) return
        if (state.moveHistory.isEmpty()) return

        val userCol = forcedUserColor ?: state.userColor
        val status = forcedStatus ?: state.gameStatus
        val win = forcedWinner ?: state.winner

        val displayText = GameHistoryManager.generateHistoryText(
            gameMode = mode,
            userColor = userCol,
            gameStatus = status,
            winner = win,
            isQuitOrAppClosed = isQuitOrAppClosed,
            difficulty = state.difficulty
        )

        val historyItem = GameHistoryItem(
            dateFormatted = GameHistoryManager.formatDate(),
            gameMode = mode,
            text = displayText
        )

        historyManager.addHistoryItem(historyItem)
        hasSavedHistoryForMatch = true
    }

    fun handleAppQuitOrPause() {
        val state = _uiState.value
        if (state.gameStatus == GameStatus.IN_PROGRESS &&
            state.moveHistory.isNotEmpty() &&
            !hasSavedHistoryForMatch &&
            state.gameMode != GameMode.TUTORIAL
        ) {
            // For 2 players, if quitting it's a draw.
            // For AI, if quitting it's a \"bỏ cuộc\" (quit) for the user.
            recordMatchHistory(isQuitOrAppClosed = true)
        }
    }

    fun startNewGame(
        sideOption: SideOption = SideOption.WHITE,
        chosenDifficulty: DifficultyLevel = DifficultyLevel.LEVEL_2,
        gameMode: GameMode = GameMode.VS_AI
    ) {
        val previousState = _uiState.value
        if (previousState.gameStatus == GameStatus.IN_PROGRESS &&
            previousState.moveHistory.isNotEmpty() &&
            !hasSavedHistoryForMatch &&
            previousState.gameMode != GameMode.TUTORIAL
        ) {
            recordMatchHistory(isQuitOrAppClosed = true)
        }

        hasSavedHistoryForMatch = false

        // Save settings if not tutorial
        if (gameMode != GameMode.TUTORIAL) {
            themeManager.saveGameMode(gameMode)
            themeManager.saveDifficulty(chosenDifficulty)
            themeManager.saveSideOption(sideOption)
        }

        val actualUserColor = if (gameMode == GameMode.TWO_PLAYERS) {
            PieceColor.WHITE
        } else {
            when (sideOption) {
                SideOption.WHITE -> PieceColor.WHITE
                SideOption.BLACK -> PieceColor.BLACK
                SideOption.RANDOM -> if (kotlin.random.Random.nextBoolean()) PieceColor.WHITE else PieceColor.BLACK
            }
        }

        val newBoard = ChessBoard()
        val savedTheme = themeManager.getSelectedTheme()
        val savedViewMode = themeManager.getSelectedViewMode()

        _uiState.value = ChessUiState(
            currentScreen = AppScreen.GAME,
            selectedSideOption = sideOption,
            gameMode = gameMode,
            board = newBoard,
            userColor = actualUserColor,
            currentTurn = PieceColor.WHITE,
            difficulty = chosenDifficulty,
            gameStatus = GameStatus.IN_PROGRESS,
            showSideSelectionModal = false,
            playerLastMove = null,
            aiLastMove = null,
            lastMove = null,
            hintMove = null,
            selectedTheme = savedTheme,
            boardViewMode = savedViewMode,
            halfMoveClock = 0,
            boardSignatures = mapOf(newBoard.getBoardSignature() to 1)
        )

        // If VS_AI mode and user is Black, AI plays White first!
        if (gameMode == GameMode.VS_AI && actualUserColor == PieceColor.BLACK) {
            triggerAiMove(PieceColor.WHITE)
        }
    }

    fun navigateToSetup() {
        _uiState.value = _uiState.value.copy(currentScreen = AppScreen.SETUP)
    }

    fun returnToCurrentGame() {
        if (_uiState.value.gameStatus == GameStatus.IN_PROGRESS) {
            syncTheme()
            _uiState.value = _uiState.value.copy(currentScreen = AppScreen.GAME)
        }
    }

    fun syncTheme() {
        val savedTheme = themeManager.getSelectedTheme()
        val savedViewMode = themeManager.getSelectedViewMode()
        if (_uiState.value.selectedTheme != savedTheme || _uiState.value.boardViewMode != savedViewMode) {
            _uiState.value = _uiState.value.copy(
                selectedTheme = savedTheme,
                boardViewMode = savedViewMode
            )
        }
    }

    fun restartGame() {
        val currentState = _uiState.value
        if (currentState.isAiThinking) return
        
        // Record current game as finished before restarting if it had moves
        if (currentState.moveHistory.isNotEmpty() && currentState.gameStatus == GameStatus.IN_PROGRESS) {
            recordMatchHistory(isQuitOrAppClosed = true)
        }
        
        startNewGame(
            gameMode = currentState.gameMode,
            chosenDifficulty = currentState.difficulty,
            sideOption = currentState.selectedSideOption
        )
    }

    fun selectSide(chosenColor: PieceColor, chosenDifficulty: DifficultyLevel = _uiState.value.difficulty) {
        val option = if (chosenColor == PieceColor.WHITE) SideOption.WHITE else SideOption.BLACK
        startNewGame(option, chosenDifficulty)
    }

    fun setDifficulty(difficulty: DifficultyLevel) {
        _uiState.value = _uiState.value.copy(difficulty = difficulty)
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
            currentState.isAiThinking ||
            currentState.showSideSelectionModal
        ) {
            return
        }

        // Dedicated Tutorial Mode handling
        if (currentState.gameMode == GameMode.TUTORIAL) {
            val board = currentState.board
            val activeTurnColor = currentState.userColor
            val selected = currentState.selectedPosition

            if (selected != null) {
                val matchingMove = currentState.legalMovesForSelected.find { it.to == pos }
                if (matchingMove != null) {
                    executeUserMove(matchingMove)
                    return
                }
            }

            // Clicked on a piece
            val clickedPiece = board.getPiece(pos)
            if (clickedPiece != null && clickedPiece.color == activeTurnColor) {
                val legalMoves = board.getLegalMovesForPosition(pos)
                _uiState.value = currentState.copy(
                    selectedPosition = pos,
                    legalMovesForSelected = legalMoves,
                    hintMove = null
                )
            }
            return
        }

        // In VS_AI mode, only allow clicks when it's user's turn
        if (currentState.gameMode == GameMode.VS_AI && currentState.currentTurn != currentState.userColor) {
            return
        }

        val activeTurnColor = currentState.currentTurn
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

            // Clicked on another piece of active turn's color
            val clickedPiece = board.getPiece(pos)
            if (clickedPiece != null && clickedPiece.color == activeTurnColor) {
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
            if (clickedPiece != null && clickedPiece.color == activeTurnColor) {
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

        if (currentState.gameMode == GameMode.TUTORIAL) {
            val board = currentState.board.copy()
            board.applyMove(move)

            SoundManager.playMoveSound(
                pieceType = move.piece.type,
                isCapture = move.capturedPiece != null,
                isCheck = false
            )

            val newPos = move.to
            val movedPiece = board.getPiece(newPos)
            val newLegalMoves = if (movedPiece != null && movedPiece.color == currentState.userColor) {
                board.getLegalMovesForPosition(newPos)
            } else emptyList()

            _uiState.value = currentState.copy(
                board = board,
                selectedPosition = if (newLegalMoves.isNotEmpty()) newPos else null,
                legalMovesForSelected = newLegalMoves,
                lastMove = move,
                playerLastMove = move,
                hintMove = null
            )
            return
        }

        viewModelScope.launch {
            val boardAfter = currentState.board.copy()
            boardAfter.applyMove(move)

            val updatedHistory = currentState.moveHistory + move
            val (capWhite, capBlack) = updateCapturedPieces(currentState, move)

            val currentTurnColor = move.piece.color
            val opponent = currentTurnColor.opposite
            val opponentLegalMoves = boardAfter.getLegalMoves(opponent)
            val opponentInCheck = boardAfter.isKingInCheck(opponent)
            val checkingPos = if (opponentInCheck) boardAfter.getCheckingPieces(opponent) else emptyList()

            var newStatus = GameStatus.IN_PROGRESS
            var winner: PieceColor? = null

            if (opponentLegalMoves.isEmpty()) {
                if (opponentInCheck) {
                    newStatus = GameStatus.CHECKMATE
                    winner = currentTurnColor
                } else {
                    newStatus = GameStatus.STALEMATE
                }
            }

            // Evaluate draw conditions
            val signature = boardAfter.getBoardSignature()
            val updatedSignatures = currentState.boardSignatures.toMutableMap()
            updatedSignatures[signature] = (updatedSignatures[signature] ?: 0) + 1

            val newHalfMoveClock = if (move.piece.type == PieceType.PAWN || move.capturedPiece != null) {
                0
            } else {
                currentState.halfMoveClock + 1
            }

            var finalStatus = newStatus
            if (finalStatus == GameStatus.IN_PROGRESS) {
                when {
                    boardAfter.hasInsufficientMaterial() -> finalStatus = GameStatus.DRAW
                    newHalfMoveClock >= 100 -> finalStatus = GameStatus.DRAW
                    (updatedSignatures[signature] ?: 0) >= 3 -> finalStatus = GameStatus.DRAW
                }
            }

            // Play end game sound if applicable
            if (finalStatus != GameStatus.IN_PROGRESS) {
                SoundManager.playVictorySound()
            } else {
                // Play regular move sound
                val isCapture = move.capturedPiece != null
                SoundManager.playMoveSound(
                    pieceType = move.piece.type,
                    isCapture = isCapture,
                    isCheck = opponentInCheck
                )
            }

            // Phase 1: Trigger Animation WITHOUT updating board state yet
            _uiState.value = currentState.copy(
                selectedPosition = null,
                legalMovesForSelected = emptyList(),
                lastMove = move,
                playerLastMove = if (currentState.gameMode == GameMode.TWO_PLAYERS) {
                    if (move.piece.color == PieceColor.WHITE) move else null
                } else {
                    move
                },
                aiLastMove = if (currentState.gameMode == GameMode.TWO_PLAYERS) {
                    if (move.piece.color == PieceColor.BLACK) move else null
                } else {
                    null
                },
                hintMove = null
            )

            // Wait for animation (0.5s - small margin)
            delay(480)

            // Phase 2: Update final board state
            _uiState.value = _uiState.value.copy(
                board = boardAfter,
                moveHistory = updatedHistory,
                capturedWhitePieces = capWhite,
                capturedBlackPieces = capBlack,
                currentTurn = opponent,
                gameStatus = finalStatus,
                winner = winner,
                isCheck = opponentInCheck,
                showCheckPopup = (opponentInCheck && finalStatus == GameStatus.IN_PROGRESS),
                checkingPieces = checkingPos,
                halfMoveClock = newHalfMoveClock,
                boardSignatures = updatedSignatures
            )

            if (finalStatus != GameStatus.IN_PROGRESS) {
                recordMatchHistory(
                    isQuitOrAppClosed = false,
                    forcedStatus = finalStatus,
                    forcedWinner = winner
                )
                
                // Delay 5 seconds before showing game over popup
                launch {
                    delay(5000)
                    _uiState.value = _uiState.value.copy(showGameOverModal = true)
                }
            } else if (updatedHistory.size == 1) {
                hasSavedHistoryForMatch = false
            }

            if (finalStatus == GameStatus.IN_PROGRESS && currentState.gameMode == GameMode.VS_AI) {
                triggerAiMove(opponent)
            }
        }
    }

    private fun triggerAiMove(aiColor: PieceColor) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAiThinking = true)
            delay(500)

            val currentState = _uiState.value
            val board = currentState.board.copy()
            val aiMove = withContext(Dispatchers.Default) {
                val ai = ChessAI(aiColor, getApplication())
                ai.chooseMove(board, currentState.difficulty)
            }

            if (aiMove != null) {
                board.applyMove(aiMove)
                val updatedHistory = currentState.moveHistory + aiMove
                val (capWhite, capBlack) = updateCapturedPieces(currentState, aiMove)

                val userColor = currentState.userColor
                val userLegalMoves = board.getLegalMoves(userColor)
                val userInCheck = board.isKingInCheck(userColor)
                val checkingPos = if (userInCheck) board.getCheckingPieces(userColor) else emptyList()

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

                // Evaluate draw conditions
                val signature = board.getBoardSignature()
                val updatedSignatures = currentState.boardSignatures.toMutableMap()
                updatedSignatures[signature] = (updatedSignatures[signature] ?: 0) + 1

                val newHalfMoveClock = if (aiMove.piece.type == PieceType.PAWN || aiMove.capturedPiece != null) {
                    0
                } else {
                    currentState.halfMoveClock + 1
                }

                var finalStatus = newStatus
                if (finalStatus == GameStatus.IN_PROGRESS) {
                    when {
                        board.hasInsufficientMaterial() -> finalStatus = GameStatus.DRAW
                        newHalfMoveClock >= 100 -> finalStatus = GameStatus.DRAW
                        (updatedSignatures[signature] ?: 0) >= 3 -> finalStatus = GameStatus.DRAW
                    }
                }

                // Play end game sound if applicable
                if (finalStatus != GameStatus.IN_PROGRESS) {
                    SoundManager.playVictorySound()
                } else {
                    // Play AI move sound
                    val isCapture = aiMove.capturedPiece != null
                    SoundManager.playMoveSound(
                        pieceType = aiMove.piece.type,
                        isCapture = isCapture,
                        isCheck = userInCheck
                    )
                }

                // Phase 1: Trigger AI Animation without updating board state yet
                _uiState.value = currentState.copy(
                    lastMove = aiMove,
                    aiLastMove = aiMove,
                    playerLastMove = null,
                    isAiThinking = false
                )

                // Wait for animation (0.5s - small margin)
                delay(480)

                // Phase 2: Update final board state
                _uiState.value = _uiState.value.copy(
                    board = board,
                    moveHistory = updatedHistory,
                    capturedWhitePieces = capWhite,
                    capturedBlackPieces = capBlack,
                    currentTurn = userColor,
                    gameStatus = finalStatus,
                    winner = winner,
                    isCheck = userInCheck,
                    showCheckPopup = (userInCheck && finalStatus == GameStatus.IN_PROGRESS),
                    checkingPieces = checkingPos,
                    halfMoveClock = newHalfMoveClock,
                    boardSignatures = updatedSignatures
                )

                if (finalStatus != GameStatus.IN_PROGRESS) {
                    recordMatchHistory(
                        isQuitOrAppClosed = false,
                        forcedStatus = finalStatus,
                        forcedWinner = winner
                    )

                    // Delay 5 seconds before showing game over popup
                    launch {
                        delay(5000)
                        _uiState.value = _uiState.value.copy(showGameOverModal = true)
                    }
                } else if (updatedHistory.size == 1) {
                    hasSavedHistoryForMatch = false
                }
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

        // Undo 1 move in TWO_PLAYERS mode, 2 moves in VS_AI mode (AI + User)
        val movesToPop = if (currentState.gameMode == GameMode.TWO_PLAYERS) {
            1
        } else {
            if (currentState.moveHistory.size >= 2) 2 else 1
        }
        val newHistory = currentState.moveHistory.dropLast(movesToPop)

        // Replay board from scratch
        val newBoard = ChessBoard()
        val capWhite = mutableListOf<PieceType>()
        val capBlack = mutableListOf<PieceType>()
        var lastM: Move? = null
        
        var newHalfMoveClock = 0
        val newBoardSignatures = mutableMapOf<String, Int>()
        // Initial board signature
        newBoardSignatures[newBoard.getBoardSignature()] = 1

        for (m in newHistory) {
            m.capturedPiece?.let { c ->
                if (c.color == PieceColor.WHITE) capWhite.add(c.type) else capBlack.add(c.type)
            }
            newBoard.applyMove(m)
            lastM = m
            
            // Recalculate half-move clock
            if (m.piece.type == PieceType.PAWN || m.capturedPiece != null) {
                newHalfMoveClock = 0
            } else {
                newHalfMoveClock++
            }
            
            // Track signatures
            val sig = newBoard.getBoardSignature()
            newBoardSignatures[sig] = (newBoardSignatures[sig] ?: 0) + 1
        }

        val newCurrentTurn = if (newHistory.isEmpty()) {
            PieceColor.WHITE
        } else {
            newHistory.last().piece.color.opposite
        }

        val inCheck = newBoard.isKingInCheck(newCurrentTurn)
        val checkingPos = if (inCheck) newBoard.getCheckingPieces(newCurrentTurn) else emptyList()
        
        // Highlight only the absolute last move in history
        val pLast = if (lastM != null && lastM.piece.color == currentState.userColor) lastM else null
        val aLast = if (lastM != null && lastM.piece.color != currentState.userColor) lastM else null

        _uiState.value = currentState.copy(
            board = newBoard,
            selectedPosition = null,
            legalMovesForSelected = emptyList(),
            moveHistory = newHistory,
            capturedWhitePieces = capWhite,
            capturedBlackPieces = capBlack,
            currentTurn = newCurrentTurn,
            gameStatus = GameStatus.IN_PROGRESS,
            winner = null,
            isCheck = inCheck,
            checkingPieces = checkingPos,
            lastMove = lastM,
            playerLastMove = pLast,
            aiLastMove = aLast,
            isAiThinking = false,
            hintMove = null,
            halfMoveClock = newHalfMoveClock,
            boardSignatures = newBoardSignatures
        )
    }

    fun showHint() {
        val state = _uiState.value
        if (state.gameStatus != GameStatus.IN_PROGRESS || state.isAiThinking || state.gameMode == GameMode.TWO_PLAYERS) return

        viewModelScope.launch {
            // Hiển thị trạng thái đang suy nghĩ cho gợi ý
            _uiState.value = _uiState.value.copy(isAiThinking = true)
            
            val hint = withContext(Dispatchers.Default) {
                // Ưu tiên dùng Stockfish Engine cho gợi ý chất lượng cao
                val stockfish = StockfishEngine(getApplication<Application>())
                val fen = state.board.toFen(state.currentTurn)
                val bestUci = stockfish.getBestMove(fen, depth = 14)
                
                if (bestUci != null) {
                    stockfish.parseUciMove(state.board, bestUci)
                } else {
                    // Fallback sang AI tích hợp nếu Stockfish không sẵn sàng
                    val ai = ChessAI(state.currentTurn, getApplication())
                    ai.chooseMove(state.board, DifficultyLevel.LEVEL_6)
                }
            }
            
            if (hint != null) {
                val legalMoves = state.board.getLegalMovesForPosition(hint.from)
                _uiState.value = _uiState.value.copy(
                    selectedPosition = hint.from,
                    legalMovesForSelected = legalMoves,
                    hintMove = hint,
                    isAiThinking = false
                )
            } else {
                _uiState.value = _uiState.value.copy(isAiThinking = false)
            }
        }
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
        val winnerColor = state.currentTurn.opposite
        _uiState.value = state.copy(
            gameStatus = GameStatus.RESIGNED,
            winner = winnerColor,
            selectedPosition = null,
            legalMovesForSelected = emptyList(),
            showResignConfirmationModal = false,
            showGameOverModal = true
        )
        recordMatchHistory(
            isQuitOrAppClosed = false,
            forcedStatus = GameStatus.RESIGNED,
            forcedWinner = winnerColor
        )
    }

    fun openHistoryModal() {
        _uiState.value = _uiState.value.copy(showHistoryModal = true)
    }

    fun closeHistoryModal() {
        _uiState.value = _uiState.value.copy(showHistoryModal = false)
    }

    fun dismissCheckPopup() {
        _uiState.value = _uiState.value.copy(showCheckPopup = false)
    }

    fun openCapturedPiecesModal() {
        _uiState.value = _uiState.value.copy(showCapturedPiecesModal = true)
    }

    fun closeCapturedPiecesModal() {
        _uiState.value = _uiState.value.copy(showCapturedPiecesModal = false)
    }

    fun openThemeModal() {
        _uiState.value = _uiState.value.copy(showThemeModal = true)
    }

    fun closeThemeModal() {
        _uiState.value = _uiState.value.copy(showThemeModal = false)
    }

    fun closeGameOverModal() {
        _uiState.value = _uiState.value.copy(showGameOverModal = false)
    }

    fun setBoardViewMode(mode: com.example.chess.model.BoardViewMode) {
        themeManager.saveViewMode(mode)
        _uiState.value = _uiState.value.copy(boardViewMode = mode)
    }

    fun selectTheme(theme: ChessTheme) {
        themeManager.saveTheme(theme.name)
        _uiState.value = _uiState.value.copy(
            selectedTheme = theme,
            showThemeModal = false
        )
    }

    fun startTutorialMode(pieceType: PieceType = PieceType.ROOK) {
        val board = ChessBoard(initialize = false)
        val userColor = PieceColor.WHITE
        val opponentColor = PieceColor.BLACK

        var primaryPos = Position(4, 3) // d4

        when (pieceType) {
            PieceType.ROOK -> { // Xe: Đi thẳng, ngang không giới hạn ô
                primaryPos = Position(4, 3)
                board.setPiece(primaryPos, com.example.chess.model.Piece(PieceType.ROOK, userColor))
                board.setPiece(Position(7, 4), com.example.chess.model.Piece(PieceType.KING, userColor))
                board.setPiece(Position(0, 0), com.example.chess.model.Piece(PieceType.KING, opponentColor))
                board.setPiece(Position(2, 3), com.example.chess.model.Piece(PieceType.PAWN, opponentColor))
                board.setPiece(Position(4, 1), com.example.chess.model.Piece(PieceType.PAWN, userColor))
                board.setPiece(Position(4, 6), com.example.chess.model.Piece(PieceType.KNIGHT, opponentColor))
            }
            PieceType.KNIGHT -> { // Mã: Đi hình chữ L
                primaryPos = Position(4, 3)
                board.setPiece(primaryPos, com.example.chess.model.Piece(PieceType.KNIGHT, userColor))
                board.setPiece(Position(7, 4), com.example.chess.model.Piece(PieceType.KING, userColor))
                board.setPiece(Position(0, 0), com.example.chess.model.Piece(PieceType.KING, opponentColor))
                board.setPiece(Position(2, 4), com.example.chess.model.Piece(PieceType.PAWN, opponentColor))
                board.setPiece(Position(3, 1), com.example.chess.model.Piece(PieceType.BISHOP, opponentColor))
                board.setPiece(Position(3, 3), com.example.chess.model.Piece(PieceType.PAWN, userColor))
                board.setPiece(Position(4, 2), com.example.chess.model.Piece(PieceType.PAWN, userColor))
            }
            PieceType.BISHOP -> { // Tượng: Đi chéo không giới hạn ô
                primaryPos = Position(4, 3)
                board.setPiece(primaryPos, com.example.chess.model.Piece(PieceType.BISHOP, userColor))
                board.setPiece(Position(7, 4), com.example.chess.model.Piece(PieceType.KING, userColor))
                board.setPiece(Position(0, 0), com.example.chess.model.Piece(PieceType.KING, opponentColor))
                board.setPiece(Position(2, 5), com.example.chess.model.Piece(PieceType.KNIGHT, opponentColor))
                board.setPiece(Position(6, 1), com.example.chess.model.Piece(PieceType.PAWN, userColor))
                board.setPiece(Position(2, 1), com.example.chess.model.Piece(PieceType.PAWN, opponentColor))
            }
            PieceType.QUEEN -> { // Hậu: Đi thẳng, ngang, chéo tự do
                primaryPos = Position(4, 3)
                board.setPiece(primaryPos, com.example.chess.model.Piece(PieceType.QUEEN, userColor))
                board.setPiece(Position(7, 4), com.example.chess.model.Piece(PieceType.KING, userColor))
                board.setPiece(Position(0, 0), com.example.chess.model.Piece(PieceType.KING, opponentColor))
                board.setPiece(Position(2, 3), com.example.chess.model.Piece(PieceType.ROOK, opponentColor))
                board.setPiece(Position(2, 5), com.example.chess.model.Piece(PieceType.BISHOP, opponentColor))
                board.setPiece(Position(6, 3), com.example.chess.model.Piece(PieceType.PAWN, userColor))
                board.setPiece(Position(4, 6), com.example.chess.model.Piece(PieceType.KNIGHT, opponentColor))
            }
            PieceType.KING -> { // Vua: Đi 1 ô theo mọi hướng
                primaryPos = Position(4, 3)
                board.setPiece(primaryPos, com.example.chess.model.Piece(PieceType.KING, userColor))
                board.setPiece(Position(0, 0), com.example.chess.model.Piece(PieceType.KING, opponentColor))
                board.setPiece(Position(3, 3), com.example.chess.model.Piece(PieceType.PAWN, opponentColor))
                board.setPiece(Position(5, 2), com.example.chess.model.Piece(PieceType.PAWN, userColor))
                board.setPiece(Position(3, 4), com.example.chess.model.Piece(PieceType.KNIGHT, opponentColor))
            }
            PieceType.PAWN -> { // Tốt: Đi thẳng 1 ô (ô đầu đi 2 ô), ăn chéo
                primaryPos = Position(6, 4)
                board.setPiece(primaryPos, com.example.chess.model.Piece(PieceType.PAWN, userColor))
                board.setPiece(Position(4, 2), com.example.chess.model.Piece(PieceType.PAWN, userColor))
                board.setPiece(Position(7, 4), com.example.chess.model.Piece(PieceType.KING, userColor))
                board.setPiece(Position(0, 0), com.example.chess.model.Piece(PieceType.KING, opponentColor))
                board.setPiece(Position(5, 3), com.example.chess.model.Piece(PieceType.PAWN, opponentColor))
                board.setPiece(Position(3, 3), com.example.chess.model.Piece(PieceType.KNIGHT, opponentColor))
            }
        }

        val legalMoves = board.getLegalMovesForPosition(primaryPos)

        _uiState.value = ChessUiState(
            currentScreen = AppScreen.GAME,
            selectedSideOption = SideOption.WHITE,
            gameMode = GameMode.TUTORIAL,
            board = board,
            userColor = userColor,
            currentTurn = userColor,
            selectedPosition = primaryPos,
            legalMovesForSelected = legalMoves,
            gameStatus = GameStatus.IN_PROGRESS,
            tutorialPiece = pieceType
        )
    }

    fun resetTutorialBoard() {
        val currentPiece = _uiState.value.tutorialPiece ?: PieceType.ROOK
        startTutorialMode(currentPiece)
    }
}
