package com.example.chess.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chess.audio.SoundManager
import com.example.chess.data.*
import com.example.chess.engine.*
import com.example.chess.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

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
    val showRestartConfirmationModal: Boolean = false,
    val showCapturedPiecesModal: Boolean = false,
    val showHistoryModal: Boolean = false,
    val showThemeModal: Boolean = false,
    val showGameOverModal: Boolean = false,
    val showSpecialMoveResult: Boolean = false,
    val isSpecialMoveSuccess: Boolean = false,
    val specialMoveResultMessage: String = "",
    val selectedTheme: ChessTheme = ChessTheme.CLASSIC,
    val pendingPromotionMove: Move? = null,
    val hintMove: Move? = null,
    val tutorialPiece: PieceType? = null,
    val specialTutorialType: SpecialTutorialType? = null,
    val boardViewMode: com.example.chess.model.BoardViewMode = com.example.chess.model.BoardViewMode.VIEW_2D,
    val checkingPieces: List<Position> = emptyList(),
    val halfMoveClock: Int = 0,
    val boardSignatures: Map<String, Int> = emptyMap(),
    val isGameEndControlsEnabled: Boolean = false,
    val timerOption: GameTimerOption = GameTimerOption.NONE,
    val whiteTimeMillis: Long = 0,
    val blackTimeMillis: Long = 0,
    val isTimerActive: Boolean = false,
    val isSoundEnabled: Boolean = true,
    val isMoveHintsEnabled: Boolean = true,
    val isSaveGameEnabled: Boolean = false,
    val showGeneralSettingsModal: Boolean = false,
    val hasPersistedGame: Boolean = false,
    val initialPuzzleFen: String? = null,
    val puzzleCategory: String? = null,
    val puzzleLevel: Int? = null,
    val isLastPuzzleInCategory: Boolean = false,
    val completedPuzzles: Set<String> = emptySet(),
    val matchEndTimestamp: Long = 0,
    val showSaveGameConfirmationModal: Boolean = false,
    val pendingNavigationTarget: NavigationTarget? = null,
    val navigateToMenuTrigger: Boolean = false,
    val scoringScore: Int = 0,
    val selectedScoringMode: ChessScoreMode = ChessScoreMode.Score30s
)

class ChessViewModel(application: Application) : AndroidViewModel(application) {

    private val historyManager = GameHistoryManager(application)
    private val themeManager = ChessThemeManager(application)
    private var hasSavedHistoryForMatch = false
    private var timerJob: kotlinx.coroutines.Job? = null

    private val _uiState = MutableStateFlow(run {
        val mode = themeManager.getSelectedGameMode()
        ChessUiState(
            selectedTheme = themeManager.getSelectedTheme(),
            boardViewMode = themeManager.getSelectedViewMode(),
            gameMode = mode,
            difficulty = themeManager.getSelectedDifficulty(),
            selectedSideOption = if (mode == GameMode.SCORING) themeManager.getScoringSideOption() else themeManager.getSelectedSideOption(),
            timerOption = themeManager.getSelectedTimerOption(),
            isSoundEnabled = themeManager.isSoundEnabled(),
            isMoveHintsEnabled = themeManager.isMoveHintsEnabled(),
            isSaveGameEnabled = themeManager.isGamePersistenceEnabled(),
            hasPersistedGame = themeManager.getPersistedGameState() != null,
            completedPuzzles = themeManager.getCompletedPuzzles(themeManager.getSelectedGameMode()),
            tutorialPiece = if (mode == GameMode.SCORING) themeManager.getScoringPieceType() else null,
            selectedScoringMode = if (mode == GameMode.SCORING) ChessScoreMode.fromTime(themeManager.getScoringSeconds()) else ChessScoreMode.Score30s
        )
    })
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

        // If puzzle/one-move/scoring mode, don't add to standard history list
        if (mode == GameMode.PUZZLE || mode == GameMode.ONE_MOVE || mode == GameMode.SCORING) {
            val status = forcedStatus ?: state.gameStatus
            val win = forcedWinner ?: state.winner
            if ((mode == GameMode.PUZZLE || mode == GameMode.ONE_MOVE) && status == GameStatus.CHECKMATE && win == state.userColor) {
                state.puzzleCategory?.let { cat ->
                    state.puzzleLevel?.let { lvl ->
                        themeManager.savePuzzleCompleted(cat, lvl, mode)
                        _uiState.value = _uiState.value.copy(completedPuzzles = themeManager.getCompletedPuzzles(mode))
                    }
                }
            }
            return
        }

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
        persistCurrentGame()
        val state = _uiState.value
        if (state.gameStatus == GameStatus.IN_PROGRESS &&
            state.moveHistory.isNotEmpty() &&
            !hasSavedHistoryForMatch &&
            state.gameMode != GameMode.TUTORIAL &&
            state.gameMode != GameMode.PUZZLE &&
            state.gameMode != GameMode.ONE_MOVE
        ) {
            // For 2 players, if quitting it's a draw.
            // For AI, if quitting it's a \"bỏ cuộc\" (quit) for the user.
            recordMatchHistory(isQuitOrAppClosed = true)
        }
    }

    fun startNewGame(
        sideOption: SideOption = SideOption.WHITE,
        chosenDifficulty: DifficultyLevel = DifficultyLevel.LEVEL_2,
        gameMode: GameMode = GameMode.VS_AI,
        timerOption: GameTimerOption = GameTimerOption.NONE,
        customMinutes: Int? = null
    ) {
        val previousState = _uiState.value
        stopTimer()

        if (previousState.gameStatus == GameStatus.IN_PROGRESS &&
            previousState.moveHistory.isNotEmpty() &&
            !hasSavedHistoryForMatch &&
            previousState.gameMode != GameMode.TUTORIAL
        ) {
            recordMatchHistory(isQuitOrAppClosed = true)
        }

        hasSavedHistoryForMatch = false

        // Clear any old saved game when starting a fresh one
        if (gameMode != GameMode.TUTORIAL) {
            themeManager.clearPersistedGameState()
        }

        // Save settings if not tutorial
        if (gameMode != GameMode.TUTORIAL) {
            themeManager.saveGameMode(gameMode)
            themeManager.saveDifficulty(chosenDifficulty)
            themeManager.saveSideOption(sideOption)
        }

        val actualUserColor = when (sideOption) {
            SideOption.WHITE -> PieceColor.WHITE
            SideOption.BLACK -> PieceColor.BLACK
            SideOption.RANDOM -> if (kotlin.random.Random.nextBoolean()) PieceColor.WHITE else PieceColor.BLACK
        }

        val initialMillis = when (timerOption) {
            GameTimerOption.NONE -> 0L
            GameTimerOption.CUSTOM -> (customMinutes ?: 10).toLong() * 60 * 1000
            else -> (timerOption.minutes ?: 0).toLong() * 60 * 1000
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
            boardSignatures = mapOf(newBoard.getBoardSignature() to 1),
            timerOption = timerOption,
            whiteTimeMillis = initialMillis,
            blackTimeMillis = initialMillis,
            isTimerActive = timerOption != GameTimerOption.NONE,
            isSoundEnabled = themeManager.isSoundEnabled(),
            isMoveHintsEnabled = themeManager.isMoveHintsEnabled(),
            isSaveGameEnabled = themeManager.isGamePersistenceEnabled()
        )

        if (timerOption != GameTimerOption.NONE) {
            startTimer()
        }

        // If VS_AI mode and user is Black, AI plays White first!
            if (gameMode == GameMode.VS_AI && actualUserColor == PieceColor.BLACK) {
            triggerAiMove(PieceColor.WHITE)
        }
    }

    fun startPuzzleMode(fen: String, category: String? = null, level: Int? = null, forcedMode: GameMode? = null) {
        stopTimer()
        hasSavedHistoryForMatch = false
        
        val board = ChessBoard(initialize = false)
        board.loadFromFen(fen)
        
        val mode = forcedMode ?: _uiState.value.gameMode
        val currentPuzzle = findPuzzleData(category, level, mode)
        val list = if (mode == GameMode.ONE_MOVE) {
            when (category) {
                "Nhập môn" -> OneMoveBeginner.list
                "Dễ" -> OneMoveEasy.list
                "Trung bình" -> OneMoveMedium.list
                "Khó" -> OneMoveHard.list
                "Cao thủ" -> OneMoveExpert.list
                else -> emptyList()
            }
        } else {
            when (category) {
                "Nhập môn" -> PuzzlesBeginner.list
                "Dễ" -> PuzzlesEasy.list
                "Trung bình" -> PuzzlesMedium.list
                "Khó" -> PuzzlesHard.list
                "Cao thủ" -> PuzzlesExpert.list
                else -> emptyList()
            }
        }
        val isLast = level == list.maxByOrNull { it.level }?.level

        val userColor = if (currentPuzzle?.isWhite == true) PieceColor.BLACK else PieceColor.WHITE
        val initialTurn = if (currentPuzzle?.isWhite == true) PieceColor.WHITE else PieceColor.BLACK
        
        _uiState.value = ChessUiState(
            currentScreen = AppScreen.PUZZLE,
            gameMode = mode,
            board = board,
            userColor = userColor,
            currentTurn = initialTurn,
            gameStatus = GameStatus.IN_PROGRESS,
            selectedTheme = themeManager.getSelectedTheme(),
            boardViewMode = themeManager.getSelectedViewMode(),
            isSoundEnabled = themeManager.isSoundEnabled(),
            isMoveHintsEnabled = themeManager.isMoveHintsEnabled(),
            isSaveGameEnabled = false,
            hasPersistedGame = themeManager.getPersistedGameState() != null,
            capturedWhitePieces = emptyList(),
            capturedBlackPieces = emptyList(),
            playerLastMove = null,
            aiLastMove = null,
            lastMove = null,
            hintMove = null,
            initialPuzzleFen = fen,
            puzzleCategory = category,
            puzzleLevel = level,
            isLastPuzzleInCategory = isLast,
            completedPuzzles = themeManager.getCompletedPuzzles(mode)
        )

        // Find and execute the firstMove
        if (currentPuzzle != null) {
            viewModelScope.launch {
                delay(800) 
                executePuzzleFirstMove(currentPuzzle.firstMove, userColor)
            }
        }
    }

    fun startScoringMode(sideOption: SideOption, pieceType: PieceType, seconds: Int) {
        stopTimer()
        hasSavedHistoryForMatch = false
        
        val scoringMode = ChessScoreMode.fromTime(seconds)
        val board = ChessBoard(initialize = false)
        val userColor = when (sideOption) {
            SideOption.WHITE -> PieceColor.WHITE
            SideOption.BLACK -> PieceColor.BLACK
            SideOption.RANDOM -> if (kotlin.random.Random.nextBoolean()) PieceColor.WHITE else PieceColor.BLACK
        }
        val opponentColor = userColor.opposite

        val allPositions = (0..7).flatMap { r -> (0..7).map { c -> Position(r, c) } }.shuffled().toMutableList()

        // Place User Selected Piece
        val userPiecePos = if (pieceType == PieceType.PAWN) {
            val validPos = allPositions.first { it.row in 1..6 }
            allPositions.remove(validPos)
            validPos
        } else {
            allPositions.removeAt(0)
        }
        board.setPiece(userPiecePos, Piece(pieceType, userColor))

        // Place 2 Random Enemy Pieces (not King)
        val enemyPieceTypes = listOf(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT, PieceType.PAWN)
        repeat(2) {
            if (allPositions.isNotEmpty()) {
                val type = enemyPieceTypes.random()
                val pos = if (type == PieceType.PAWN) {
                    val validPos = allPositions.first { it.row in 1..6 }
                    allPositions.remove(validPos)
                    validPos
                } else {
                    allPositions.removeAt(0)
                }
                board.setPiece(pos, Piece(type, opponentColor))
            }
        }
        
        val initialTimeMillis = scoringMode.time * 1000L

        // Persist scoring settings
        themeManager.saveScoringSideOption(sideOption)
        themeManager.saveScoringPieceType(pieceType)
        themeManager.saveScoringSeconds(seconds)

        _uiState.value = ChessUiState(
            currentScreen = AppScreen.SCORING,
            gameMode = GameMode.SCORING,
            board = board,
            userColor = userColor,
            selectedSideOption = sideOption,
            currentTurn = userColor, // Player always goes first
            tutorialPiece = pieceType,
            gameStatus = GameStatus.IN_PROGRESS,
            selectedTheme = themeManager.getSelectedTheme(),
            boardViewMode = themeManager.getSelectedViewMode(),
            isSoundEnabled = themeManager.isSoundEnabled(),
            isMoveHintsEnabled = themeManager.isMoveHintsEnabled(),
            isSaveGameEnabled = false,
            hasPersistedGame = themeManager.getPersistedGameState() != null,
            capturedWhitePieces = emptyList(),
            capturedBlackPieces = emptyList(),
            playerLastMove = null,
            aiLastMove = null,
            lastMove = null,
            hintMove = null,
            initialPuzzleFen = board.toFen(PieceColor.WHITE),
            scoringScore = 0,
            selectedScoringMode = scoringMode,
            whiteTimeMillis = initialTimeMillis,
            blackTimeMillis = initialTimeMillis,
            isTimerActive = true
        )
        
        startTimer()
    }

    private fun findPuzzleData(category: String?, level: Int?, mode: GameMode): Puzzles? {
        if (category == null || level == null) return null
        val list = if (mode == GameMode.ONE_MOVE) {
            when (category) {
                "Nhập môn" -> OneMoveBeginner.list
                "Dễ" -> OneMoveEasy.list
                "Trung bình" -> OneMoveMedium.list
                "Khó" -> OneMoveHard.list
                "Cao thủ" -> OneMoveExpert.list
                else -> return null
            }
        } else {
            when (category) {
                "Nhập môn" -> PuzzlesBeginner.list
                "Dễ" -> PuzzlesEasy.list
                "Trung bình" -> PuzzlesMedium.list
                "Khó" -> PuzzlesHard.list
                "Cao thủ" -> PuzzlesExpert.list
                else -> return null
            }
        }
        return list.find { it.level == level }
    }

    private fun executePuzzleFirstMove(moveStr: String, userColor: PieceColor) {
        val board = _uiState.value.board
        if (moveStr.length < 4) return
        
        val fromPos = Position.fromAlgebraic(moveStr.substring(0, 2)) ?: return
        val toPos = Position.fromAlgebraic(moveStr.substring(2, 4)) ?: return
        val piece = board.getPiece(fromPos) ?: return
        
        val move = Move(fromPos, toPos, piece, board.getPiece(toPos))
        board.applyMove(move)

        // Kiểm tra xem sau nước đi này người chơi có bị chiếu không
        val isCheck = board.isKingInCheck(userColor)
        val checkingPieces = if (isCheck) board.getCheckingPieces(userColor) else emptyList()

        // After firstMove, it must be User's turn
        _uiState.value = _uiState.value.copy(
            board = board,
            currentTurn = userColor, 
            aiLastMove = move,
            lastMove = move,
            isCheck = isCheck,
            checkingPieces = checkingPieces
        )
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val currentState = _uiState.value
                if (currentState.gameStatus != GameStatus.IN_PROGRESS || !currentState.isTimerActive) break

                if (currentState.gameMode == GameMode.SCORING) {
                    val isWhite = currentState.userColor == PieceColor.WHITE
                    val currentTime = if (isWhite) currentState.whiteTimeMillis else currentState.blackTimeMillis
                    val newTime = (currentTime - 1000).coerceAtLeast(0)
                    _uiState.value = if (isWhite) currentState.copy(whiteTimeMillis = newTime) else currentState.copy(blackTimeMillis = newTime)
                    if (newTime <= 0) {
                        handleTimeout(currentState.userColor)
                        break
                    }
                } else if (currentState.currentTurn == PieceColor.WHITE) {
                    val newTime = (currentState.whiteTimeMillis - 1000).coerceAtLeast(0)
                    _uiState.value = currentState.copy(whiteTimeMillis = newTime)
                    if (newTime <= 0) {
                        handleTimeout(PieceColor.WHITE)
                        break
                    }
                } else {
                    val newTime = (currentState.blackTimeMillis - 1000).coerceAtLeast(0)
                    _uiState.value = currentState.copy(blackTimeMillis = newTime)
                    if (newTime <= 0) {
                        handleTimeout(PieceColor.BLACK)
                        break
                    }
                }
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun handleTimeout(loserColor: PieceColor) {
        val currentState = _uiState.value

        if (currentState.gameMode == GameMode.SCORING) {
            _uiState.value = currentState.copy(
                gameStatus = GameStatus.CHECKMATE, // Reusing status for challenge finish
                winner = null,
                isTimerActive = false,
                isGameEndControlsEnabled = true,
                showGameOverModal = true,
                isAiThinking = false
            )
            if (currentState.isSoundEnabled) {
                SoundManager.playVictorySound()
            }
            return
        }

        val winnerColor = loserColor.opposite
        
        // If the side with time remaining has insufficient material to mate, it's a draw
        val isInsufficientMaterial = currentState.board.hasInsufficientMatingMaterial(winnerColor)
        
        if (isInsufficientMaterial) {
            _uiState.value = currentState.copy(
                gameStatus = GameStatus.DRAW,
                winner = null,
                isTimerActive = false,
                isGameEndControlsEnabled = true,
                showGameOverModal = true
            )
            recordMatchHistory(forcedStatus = GameStatus.DRAW, forcedWinner = null)
        } else {
            _uiState.value = currentState.copy(
                gameStatus = GameStatus.CHECKMATE, // Reusing status for simplicity, or could add TIMEOUT
                winner = winnerColor,
                isTimerActive = false,
                isGameEndControlsEnabled = true,
                showGameOverModal = true
            )
            recordMatchHistory(forcedStatus = GameStatus.CHECKMATE, forcedWinner = winnerColor)
        }

        if (currentState.isSoundEnabled) {
            SoundManager.playVictorySound()
        }
    }

    fun navigateToSetup() {
        stopTimer()
        _uiState.value = _uiState.value.copy(
            currentScreen = AppScreen.SETUP,
            isTimerActive = false,
            showGameOverModal = false,
            showCheckPopup = false,
            pendingPromotionMove = null
        )
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }

    fun requestNavigation(target: NavigationTarget) {
        executeNavigation(target)
    }

    fun confirmSaveGame(save: Boolean) {
        val state = _uiState.value
        val target = state.pendingNavigationTarget ?: return
        
        if (save) {
            val json = serializeGameState(state)
            themeManager.saveCurrentGameState(json)
            _uiState.value = _uiState.value.copy(hasPersistedGame = true)
        } else {
            themeManager.clearPersistedGameState()
            // Mark game as not started so resume button disappears
            _uiState.value = _uiState.value.copy(
                hasPersistedGame = false,
                gameStatus = GameStatus.NOT_STARTED
            )
        }
        
        _uiState.value = _uiState.value.copy(showSaveGameConfirmationModal = false, pendingNavigationTarget = null)
        executeNavigation(target)
    }

    fun cancelSaveGameDialog() {
        _uiState.value = _uiState.value.copy(showSaveGameConfirmationModal = false, pendingNavigationTarget = null)
    }

    private fun executeNavigation(target: NavigationTarget) {
        when (target) {
            NavigationTarget.SETUP -> navigateToSetup()
            NavigationTarget.MENU -> {
                stopTimer()
                _uiState.value = _uiState.value.copy(
                    navigateToMenuTrigger = true,
                    isTimerActive = false,
                    showGameOverModal = false,
                    showCheckPopup = false,
                    pendingPromotionMove = null
                )
            }
        }
    }

    fun onMenuNavigationHandled() {
        _uiState.value = _uiState.value.copy(navigateToMenuTrigger = false)
    }

    fun returnToCurrentGame() {
        val mode = _uiState.value.gameMode
        if (_uiState.value.gameStatus == GameStatus.IN_PROGRESS && (mode == GameMode.VS_AI || mode == GameMode.TWO_PLAYERS)) {
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
        val state = _uiState.value

        // In SCORING mode or if game over modal is already showing, restart immediately
        if (state.gameMode == GameMode.SCORING || state.showGameOverModal) {
            executeRestart()
            return
        }

        if (state.isAiThinking) return

        // If game is in progress and has moves, show confirmation dialog
        if (state.gameStatus == GameStatus.IN_PROGRESS && state.moveHistory.isNotEmpty()) {
            _uiState.value = state.copy(showRestartConfirmationModal = true)
            return
        }

        // Otherwise, restart immediately (applies to finished games too)
        executeRestart()
    }

    fun confirmRestart() {
        _uiState.value = _uiState.value.copy(showRestartConfirmationModal = false)
        executeRestart()
    }

    fun cancelRestart() {
        _uiState.value = _uiState.value.copy(showRestartConfirmationModal = false)
    }

    private fun executeRestart() {
        val currentState = _uiState.value
        stopTimer()
        
        // Record current game as finished before restarting if it had moves and was in progress
        if (currentState.moveHistory.isNotEmpty() && currentState.gameStatus == GameStatus.IN_PROGRESS && currentState.gameMode != GameMode.PUZZLE && currentState.gameMode != GameMode.ONE_MOVE && currentState.gameMode != GameMode.SCORING) {
            recordMatchHistory(isQuitOrAppClosed = true)
        }

        if ((currentState.gameMode == GameMode.PUZZLE || currentState.gameMode == GameMode.ONE_MOVE) && currentState.initialPuzzleFen != null) {
            startPuzzleMode(currentState.initialPuzzleFen, currentState.puzzleCategory, currentState.puzzleLevel, currentState.gameMode)
            return
        }

        if (currentState.gameMode == GameMode.SCORING) {
            startScoringMode(currentState.selectedSideOption, currentState.tutorialPiece ?: PieceType.QUEEN, currentState.selectedScoringMode.time.toInt())
            return
        }

        if (currentState.gameMode == GameMode.SPECIAL_MOVE && currentState.specialTutorialType != null) {
            startSpecialMoveTutorial(currentState.specialTutorialType)
            return
        }
        
        val initialMillis = when (currentState.timerOption) {
            GameTimerOption.NONE -> 0L
            GameTimerOption.CUSTOM -> (currentState.whiteTimeMillis.coerceAtLeast(currentState.blackTimeMillis)).coerceAtLeast(60000)
            else -> (currentState.timerOption.minutes ?: 0).toLong() * 60 * 1000
        }

        startNewGame(
            gameMode = currentState.gameMode,
            chosenDifficulty = currentState.difficulty,
            sideOption = currentState.selectedSideOption,
            timerOption = currentState.timerOption,
            customMinutes = (initialMillis / 60000).toInt()
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
                    // Check if promotion choice is needed in Tutorial
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

        // In VS_AI or SCORING mode, only allow clicks when it's user's turn
        if ((currentState.gameMode == GameMode.VS_AI || currentState.gameMode == GameMode.SCORING) && currentState.currentTurn != currentState.userColor) {
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

            if (currentState.isSoundEnabled) {
                SoundManager.playMoveSound(
                    pieceType = move.piece.type,
                    isCapture = move.capturedPiece != null,
                    isCheck = false
                )
            }

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
                // Check if Special Move tutorial is completed
                if (currentState.gameMode == GameMode.SPECIAL_MOVE) {
                    val completed = when (currentState.specialTutorialType) {
                        SpecialTutorialType.CASTLING_KINGSIDE, SpecialTutorialType.CASTLING_QUEENSIDE -> move.isCastling
                        SpecialTutorialType.PAWN_PROMOTION -> move.promotion != null
                        SpecialTutorialType.EN_PASSANT -> move.isEnPassant
                        else -> false
                    }
                    val typeName = currentState.specialTutorialType?.displayNameVi ?: "đặc biệt"
                    if (completed) {
                        finalStatus = GameStatus.CHECKMATE
                        winner = currentState.userColor
                        _uiState.value = _uiState.value.copy(
                            showSpecialMoveResult = true,
                            isSpecialMoveSuccess = true,
                            specialMoveResultMessage = "Bạn đã hoàn thành nước đi \"$typeName\"!"
                        )
                    } else {
                        finalStatus = GameStatus.CHECKMATE
                        winner = currentState.userColor.opposite
                        _uiState.value = _uiState.value.copy(
                            showSpecialMoveResult = true,
                            isSpecialMoveSuccess = false,
                            specialMoveResultMessage = "Nước đi không hợp lệ! Bạn cần thực hiện đúng \"$typeName\"."
                        )
                    }
                }
                
                if (finalStatus == GameStatus.IN_PROGRESS) {
                    when {
                        boardAfter.hasInsufficientMaterial() -> finalStatus = GameStatus.DRAW
                        newHalfMoveClock >= 100 -> finalStatus = GameStatus.DRAW
                        (updatedSignatures[signature] ?: 0) >= 3 -> finalStatus = GameStatus.DRAW
                    }
                }
            }

            // SCORING mode never ends by move conditions, only by timer
            if (currentState.gameMode == GameMode.SCORING) {
                finalStatus = GameStatus.IN_PROGRESS
            }

            // ONE_MOVE mode: if the user's move is not checkmate, they lose immediately.
            if (currentState.gameMode == GameMode.ONE_MOVE && finalStatus == GameStatus.IN_PROGRESS) {
                finalStatus = GameStatus.CHECKMATE
                winner = currentState.userColor.opposite
            }

            var updatedScoringScore = currentState.scoringScore
            if (currentState.gameMode == GameMode.SCORING && move.capturedPiece != null) {
                updatedScoringScore += move.capturedPiece.type.pointValue
            }
            
            // Give 30 points for completing Special Move
            if (currentState.gameMode == GameMode.SPECIAL_MOVE && finalStatus == GameStatus.CHECKMATE && winner == currentState.userColor) {
                updatedScoringScore = 30
            }

            if (finalStatus != GameStatus.IN_PROGRESS) {
            if (currentState.isSoundEnabled) {
                SoundManager.playVictorySound()
            }
        } else {
            // Play regular move sound
            val isCapture = move.capturedPiece != null
            if (currentState.isSoundEnabled) {
                SoundManager.playMoveSound(
                    pieceType = move.piece.type,
                    isCapture = isCapture,
                    isCheck = opponentInCheck
                )
            }
        }

            // Phase 1: Trigger Animation WITHOUT updating board state yet
            _uiState.value = _uiState.value.copy(
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
                boardSignatures = updatedSignatures,
                scoringScore = updatedScoringScore
            )
            
            if (finalStatus == GameStatus.IN_PROGRESS) {
                persistCurrentGame()
            } else {
                onGameFinished()
            }

            if (finalStatus != GameStatus.IN_PROGRESS) {
                recordMatchHistory(
                    isQuitOrAppClosed = false,
                    forcedStatus = finalStatus,
                    forcedWinner = winner
                )
                
                // Delay 5 seconds before showing game over popup and enabling controls
                launch {
                    if (currentState.gameMode != GameMode.SPECIAL_MOVE) {
                        val gameOverDelay = if (currentState.gameMode == GameMode.ONE_MOVE || currentState.gameMode == GameMode.SCORING) 0L else 5000L
                        delay(gameOverDelay)
                        _uiState.value = _uiState.value.copy(
                            showGameOverModal = true,
                            isGameEndControlsEnabled = true
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(isGameEndControlsEnabled = true)
                    }
                }
            } else if (updatedHistory.size == 1) {
                hasSavedHistoryForMatch = false
            }

            if (finalStatus == GameStatus.IN_PROGRESS && (currentState.gameMode == GameMode.VS_AI || currentState.gameMode == GameMode.PUZZLE || currentState.gameMode == GameMode.SCORING)) {
                triggerAiMove(opponent)
            }
        }
    }

    private fun triggerAiMove(aiColor: PieceColor) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAiThinking = true)
            delay(500)

            val currentState = _uiState.value
            if (currentState.gameStatus != GameStatus.IN_PROGRESS) {
                _uiState.value = currentState.copy(isAiThinking = false)
                return@launch
            }

            val board = currentState.board.copy()
            val aiMove = withContext(Dispatchers.Default) {
                if (currentState.gameMode == GameMode.PUZZLE) {
                    val stockfish = com.example.chess.engine.StockfishEngine(getApplication())
                    val fen = currentState.board.toFen(aiColor)
                    val bestUci = stockfish.getBestMove(fen, depth = 14)
                    if (bestUci != null) {
                        stockfish.parseUciMove(currentState.board, bestUci)
                    } else {
                        val ai = ChessAI(aiColor, getApplication())
                        ai.chooseMove(board, currentState.difficulty)
                    }
                } else if (currentState.gameMode == GameMode.SCORING) {
                    val ai = ChessAI(aiColor, getApplication())
                    // Use the new scoring mode algorithm
                    ai.chooseMove(board, isScoringMode = true)
                } else {
                    val ai = ChessAI(aiColor, getApplication())
                    ai.chooseMove(board, currentState.difficulty)
                }
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

                // In Puzzle mode, Draw or Stalemate is treated as a loss for the user
                if ((currentState.gameMode == GameMode.PUZZLE || currentState.gameMode == GameMode.ONE_MOVE) && (finalStatus == GameStatus.DRAW || finalStatus == GameStatus.STALEMATE)) {
                    finalStatus = GameStatus.CHECKMATE 
                    winner = aiColor // User lost
                }

                // SCORING mode never ends by move conditions, only by timer
                if (currentState.gameMode == GameMode.SCORING) {
                    finalStatus = GameStatus.IN_PROGRESS
                }

                var updatedScoringScore = currentState.scoringScore
                val finalBoard = board.copy()
                
                // Logic for maximum AI pieces based on score
                if (currentState.gameMode == GameMode.SCORING) {
                    // Update score if AI captured player's piece
                    if (aiMove.capturedPiece != null) {
                        updatedScoringScore = (updatedScoringScore - aiMove.capturedPiece.type.pointValue).coerceAtLeast(0)
                    }

                    refillAiPiecesForScoring(
                        board = finalBoard,
                        aiColor = aiColor,
                        targetScore = updatedScoringScore,
                        scoringMode = currentState.selectedScoringMode
                    )

                    // Respawn player piece if it was captured
                    if (aiMove.capturedPiece != null) {
                        val emptySquares = mutableListOf<Position>()
                        for (r in 0..7) {
                            for (c in 0..7) {
                                if (finalBoard.getPiece(r, c) == null) {
                                    emptySquares.add(Position(r, c))
                                }
                            }
                        }
                        if (emptySquares.isNotEmpty()) {
                            var spawnPos = emptySquares.random()
                            
                            // Constraint: Respawning Pawn cannot be on rank 1 or 8
                            if (aiMove.capturedPiece.type == PieceType.PAWN && (spawnPos.row == 0 || spawnPos.row == 7)) {
                                val validSquares = emptySquares.filter { it.row != 0 && it.row != 7 }
                                if (validSquares.isNotEmpty()) {
                                    spawnPos = validSquares.random()
                                } else {
                                    // If no valid middle squares (unlikely), promote it to a Knight for respawn
                                    finalBoard.setPiece(spawnPos, Piece(PieceType.KNIGHT, aiMove.capturedPiece.color))
                                    return@launch // Skip normal setPiece
                                }
                            }

                            finalBoard.setPiece(spawnPos, Piece(aiMove.capturedPiece.type, aiMove.capturedPiece.color))
                        }
                    }
                }

                // Play end game sound if applicable
                if (finalStatus != GameStatus.IN_PROGRESS) {
                    if (currentState.isSoundEnabled) {
                        SoundManager.playVictorySound()
                    }
                } else {
                    // Play AI move sound
                    val isCapture = aiMove.capturedPiece != null
                    if (currentState.isSoundEnabled) {
                        SoundManager.playMoveSound(
                            pieceType = aiMove.piece.type,
                            isCapture = isCapture,
                            isCheck = userInCheck
                        )
                    }
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
                val latestState = _uiState.value
                val statusToKeep = if (latestState.gameStatus != GameStatus.IN_PROGRESS) {
                    latestState.gameStatus
                } else {
                    finalStatus
                }

                _uiState.value = _uiState.value.copy(
                    board = finalBoard,
                    moveHistory = updatedHistory,
                    capturedWhitePieces = capWhite,
                    capturedBlackPieces = capBlack,
                    currentTurn = userColor,
                    gameStatus = statusToKeep,
                    winner = if (statusToKeep != GameStatus.IN_PROGRESS) latestState.winner else winner,
                    isCheck = userInCheck,
                    showCheckPopup = (userInCheck && statusToKeep == GameStatus.IN_PROGRESS),
                    checkingPieces = checkingPos,
                    halfMoveClock = newHalfMoveClock,
                    boardSignatures = updatedSignatures,
                    scoringScore = updatedScoringScore,
                    isAiThinking = false
                )

                if (finalStatus == GameStatus.IN_PROGRESS) {
                    persistCurrentGame()
                } else {
                    onGameFinished()
                }

                if (finalStatus != GameStatus.IN_PROGRESS) {
                    recordMatchHistory(
                        isQuitOrAppClosed = false,
                        forcedStatus = finalStatus,
                        forcedWinner = winner
                    )

                    // Delay 5 seconds before showing game over popup and enabling controls
                    launch {
                        val gameOverDelay = if (currentState.gameMode == GameMode.ONE_MOVE) 0L else 5000L
                        delay(gameOverDelay)
                        _uiState.value = _uiState.value.copy(
                            showGameOverModal = true,
                            isGameEndControlsEnabled = true
                        )
                    }
                } else if (updatedHistory.size == 1) {
                    hasSavedHistoryForMatch = false
                }
            } else {
                if (currentState.gameMode == GameMode.SCORING) {
                    val finalBoard = board.copy()
                    refillAiPiecesForScoring(
                        board = finalBoard,
                        aiColor = aiColor,
                        targetScore = currentState.scoringScore,
                        scoringMode = currentState.selectedScoringMode
                    )
                    val latestState = _uiState.value
                    val statusToKeep = if (latestState.gameStatus != GameStatus.IN_PROGRESS) {
                        latestState.gameStatus
                    } else {
                        GameStatus.IN_PROGRESS
                    }
                    _uiState.value = latestState.copy(
                        board = finalBoard,
                        currentTurn = currentState.userColor,
                        isAiThinking = false,
                        gameStatus = statusToKeep
                    )
                } else {
                    // AI has no moves -> Stalemate or user checkmated AI
                    _uiState.value = currentState.copy(isAiThinking = false)
                }
            }
        }
    }

    private fun refillAiPiecesForScoring(
        board: ChessBoard,
        aiColor: PieceColor,
        targetScore: Int,
        scoringMode: ChessScoreMode
    ) {
        val milestonesReached = scoringMode.progressLevel.count { targetScore >= it }
        val maxAiPieces = 2 + milestonesReached
        var currentAiPieces = board.getPieces(aiColor).size

        // If more pieces than maxAiPieces, remove the weakest pieces
        if (currentAiPieces > maxAiPieces) {
            val aiPieces = mutableListOf<Pair<Position, Piece>>()
            for (r in 0..7) {
                for (c in 0..7) {
                    val p = board.getPiece(r, c)
                    if (p != null && p.color == aiColor) {
                        aiPieces.add(Position(r, c) to p)
                    }
                }
            }
            aiPieces.sortBy { it.second.type.pointValue }
            val toRemove = currentAiPieces - maxAiPieces
            repeat(toRemove) {
                val (pos, _) = aiPieces.removeAt(0)
                board.setPiece(pos, null)
            }
            currentAiPieces = maxAiPieces
        }

        // Always try to refill AI pieces up to maxAiPieces
        while (currentAiPieces < maxAiPieces) {
            val emptySquares = mutableListOf<Position>()
            for (r in 0..7) {
                for (c in 0..7) {
                    if (board.getPiece(r, c) == null) {
                        emptySquares.add(Position(r, c))
                    }
                }
            }
            if (emptySquares.isNotEmpty()) {
                val spawnPos = emptySquares.random()
                val rand = (0 until 100).random()
                var spawnedType = when {
                    rand < 45 -> PieceType.PAWN
                    rand < 60 -> PieceType.KNIGHT
                    rand < 75 -> PieceType.BISHOP
                    rand < 90 -> PieceType.ROOK
                    else -> PieceType.QUEEN
                }
                if (spawnedType == PieceType.PAWN && (spawnPos.row == 0 || spawnPos.row == 7)) {
                    spawnedType = listOf(PieceType.KNIGHT, PieceType.BISHOP, PieceType.ROOK).random()
                }
                board.setPiece(spawnPos, Piece(spawnedType, aiColor))
                currentAiPieces++
            } else {
                break
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

        // Undo 1 move in TWO_PLAYERS mode, 2 moves in VS_AI/PUZZLE/SCORING mode (AI + User)
        val movesToPop = if (currentState.gameMode == GameMode.TWO_PLAYERS) {
            1
        } else {
            if (currentState.moveHistory.size >= 2) 2 else 1
        }
        val newHistory = currentState.moveHistory.dropLast(movesToPop)

        // Replay board from scratch or FEN
        val isPuzzleOrScoring = currentState.gameMode == GameMode.PUZZLE || 
                               currentState.gameMode == GameMode.ONE_MOVE || 
                               currentState.gameMode == GameMode.SCORING
        
        val newBoard = ChessBoard(initialize = !isPuzzleOrScoring)
        
        if (isPuzzleOrScoring && currentState.initialPuzzleFen != null) {
            newBoard.loadFromFen(currentState.initialPuzzleFen)
            
            // REDO firstMove ONLY for puzzles
            if (currentState.gameMode == GameMode.PUZZLE || currentState.gameMode == GameMode.ONE_MOVE) {
                val currentPuzzle = findPuzzleData(currentState.puzzleCategory, currentState.puzzleLevel, currentState.gameMode)
                currentPuzzle?.let {
                    val moveStr = it.firstMove
                    if (moveStr.length >= 4) {
                        val fromPos = Position.fromAlgebraic(moveStr.substring(0, 2))
                        val toPos = Position.fromAlgebraic(moveStr.substring(2, 4))
                        if (fromPos != null && toPos != null) {
                            newBoard.getPiece(fromPos)?.let { p ->
                                newBoard.applyMove(Move(fromPos, toPos, p, newBoard.getPiece(toPos)))
                            }
                        }
                    }
                }
            }
        }
        val capWhite = mutableListOf<PieceType>()
        val capBlack = mutableListOf<PieceType>()
        var lastM: Move? = null
        
        // For Puzzles, if history becomes empty, we should still show the firstMove as the last move made
        if ((currentState.gameMode == GameMode.PUZZLE || currentState.gameMode == GameMode.ONE_MOVE) && newHistory.isEmpty()) {
            val currentPuzzle = findPuzzleData(currentState.puzzleCategory, currentState.puzzleLevel, currentState.gameMode)
            currentPuzzle?.let {
                val fromPos = Position.fromAlgebraic(it.firstMove.substring(0, 2))
                val toPos = Position.fromAlgebraic(it.firstMove.substring(2, 4))
                if (fromPos != null && toPos != null) {
                    newBoard.getPiece(toPos)?.let { p ->
                        lastM = Move(fromPos, toPos, p, null) // Note: simplified capture for UI highlight
                    }
                }
            }
        }
        
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
            if (currentState.gameMode == GameMode.PUZZLE || currentState.gameMode == GameMode.ONE_MOVE) {
                currentState.userColor
            } else {
                PieceColor.WHITE
            }
        } else {
            newHistory.last().piece.color.opposite
        }

        val inCheck = newBoard.isKingInCheck(newCurrentTurn)
        val checkingPos = if (inCheck) newBoard.getCheckingPieces(newCurrentTurn) else emptyList()
        
        // Highlight only the absolute last move in history
        val pLast = if (lastM != null && lastM.piece.color == currentState.userColor) lastM else null
        val aLast = if (lastM != null && lastM.piece.color != currentState.userColor) lastM else null

        // Recalculate score for SCORING mode
        var newScoringScore = 0
        if (currentState.gameMode == GameMode.SCORING) {
            for (m in newHistory) {
                if (m.piece.color == currentState.userColor && m.capturedPiece != null) {
                    newScoringScore += m.capturedPiece.type.pointValue
                } else if (m.piece.color != currentState.userColor && m.capturedPiece != null) {
                    newScoringScore -= m.capturedPiece.type.pointValue
                }
            }
        } else {
            newScoringScore = currentState.scoringScore
        }

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
            boardSignatures = newBoardSignatures,
            scoringScore = newScoringScore
        )
        persistCurrentGame()
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
            showGameOverModal = true,
            isGameEndControlsEnabled = true
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

    fun closeSpecialMoveResult() {
        _uiState.value = _uiState.value.copy(
            showSpecialMoveResult = false,
            isSpecialMoveSuccess = false,
            specialMoveResultMessage = ""
        )
    }

    fun setBoardViewMode(mode: com.example.chess.model.BoardViewMode) {
        if (_uiState.value.gameMode != com.example.chess.model.GameMode.TWO_PLAYERS) {
            themeManager.saveViewMode(mode)
        }
        _uiState.value = _uiState.value.copy(boardViewMode = mode)
    }

    fun selectTheme(theme: ChessTheme) {
        themeManager.saveTheme(theme.name)
        _uiState.value = _uiState.value.copy(
            selectedTheme = theme,
            showThemeModal = false
        )
    }

    fun openGeneralSettingsModal() {
        _uiState.value = _uiState.value.copy(showGeneralSettingsModal = true)
    }

    fun closeGeneralSettingsModal() {
        _uiState.value = _uiState.value.copy(showGeneralSettingsModal = false)
    }

    fun setSoundEnabled(enabled: Boolean) {
        themeManager.saveSoundEnabled(enabled)
        _uiState.value = _uiState.value.copy(isSoundEnabled = enabled)
    }

    fun setMoveHintsEnabled(enabled: Boolean) {
        themeManager.saveMoveHintsEnabled(enabled)
        _uiState.value = _uiState.value.copy(isMoveHintsEnabled = enabled)
    }

    fun onGameFinished() {
        val state = _uiState.value
        if (state.isSaveGameEnabled) {
            themeManager.clearPersistedGameState()
            _uiState.value = state.copy(
                hasPersistedGame = false,
                matchEndTimestamp = System.currentTimeMillis()
            )
        } else {
            _uiState.value = state.copy(matchEndTimestamp = System.currentTimeMillis())
        }
    }

    fun setSaveGameEnabled(enabled: Boolean) {
        themeManager.saveGamePersistenceEnabled(enabled)
        if (!enabled) {
            themeManager.clearPersistedGameState()
        }
        _uiState.value = _uiState.value.copy(
            isSaveGameEnabled = enabled,
            hasPersistedGame = if (!enabled) false else _uiState.value.hasPersistedGame
        )
    }

    fun persistCurrentGame() {
        val state = _uiState.value
        if (state.isSaveGameEnabled && (state.gameMode == GameMode.VS_AI || state.gameMode == GameMode.TWO_PLAYERS)) {
            if (state.gameStatus == GameStatus.IN_PROGRESS && state.moveHistory.isNotEmpty()) {
                val json = serializeGameState(state)
                themeManager.saveCurrentGameState(json)
                _uiState.value = _uiState.value.copy(hasPersistedGame = true)
            } else if (state.moveHistory.isEmpty()) {
                themeManager.clearPersistedGameState()
                _uiState.value = _uiState.value.copy(hasPersistedGame = false)
            }
        }
    }

    fun loadPersistedGame() {
        val json = themeManager.getPersistedGameState() ?: return
        val restoredState = deserializeGameState(json) ?: return
        
        stopTimer()
        
        _uiState.value = restoredState.copy(
            currentScreen = AppScreen.GAME,
            showGameOverModal = false,
            isGameEndControlsEnabled = false,
            isAiThinking = false,
            isSaveGameEnabled = themeManager.isGamePersistenceEnabled()
        )
        
        hasSavedHistoryForMatch = false
        
        // Trigger AI if it's their turn
        if (restoredState.gameMode == GameMode.VS_AI && restoredState.currentTurn != restoredState.userColor) {
            triggerAiMove(restoredState.currentTurn)
        }

        if (restoredState.timerOption != GameTimerOption.NONE) {
            startTimer()
        }
    }

    private fun serializeGameState(state: ChessUiState): String {
        val obj = JSONObject()
        obj.put("gameMode", state.gameMode.name)
        obj.put("userColor", state.userColor.name)
        obj.put("currentTurn", state.currentTurn.name)
        obj.put("difficulty", state.difficulty.name)
        obj.put("timerOption", state.timerOption.name)
        obj.put("whiteTime", state.whiteTimeMillis)
        obj.put("blackTime", state.blackTimeMillis)
        
        // Serialize board
        val boardArray = JSONArray()
        for (r in 0..7) {
            for (c in 0..7) {
                val p = state.board.getPiece(r, c)
                if (p != null) {
                    val pObj = JSONObject()
                    pObj.put("r", r)
                    pObj.put("c", c)
                    pObj.put("t", p.type.name)
                    pObj.put("col", p.color.name)
                    boardArray.put(pObj)
                }
            }
        }
        obj.put("board", boardArray)
        
        // History
        val historyArray = JSONArray()
        state.moveHistory.forEach { m ->
            val mObj = JSONObject()
            mObj.put("fr", m.from.row); mObj.put("fc", m.from.col)
            mObj.put("tr", m.to.row); mObj.put("tc", m.to.col)
            mObj.put("pt", m.piece.type.name); mObj.put("pc", m.piece.color.name)
            m.capturedPiece?.let { 
                mObj.put("ct", it.type.name); mObj.put("cc", it.color.name) 
            }
            m.promotion?.let { mObj.put("promo", it.name) }
            mObj.put("cas", m.isCastling)
            mObj.put("ep", m.isEnPassant)
            historyArray.put(mObj)
        }
        obj.put("history", historyArray)
        
        // Board flags
        obj.put("wkm", state.board.whiteKingMoved)
        obj.put("wrk", state.board.whiteRookKingsideMoved)
        obj.put("wrq", state.board.whiteRookQueensideMoved)
        obj.put("bkm", state.board.blackKingMoved)
        obj.put("brk", state.board.blackRookKingsideMoved)
        obj.put("brq", state.board.blackRookQueensideMoved)
        
        // En Passant
        state.board.enPassantTarget?.let {
            obj.put("epr", it.row)
            obj.put("epc", it.col)
        }
        
        // Half move clock
        obj.put("hmc", state.halfMoveClock)
        
        return obj.toString()
    }

    private fun deserializeGameState(json: String): ChessUiState? {
        try {
            val obj = JSONObject(json)
            val mode = GameMode.valueOf(obj.getString("gameMode"))
            val userCol = PieceColor.valueOf(obj.getString("userColor"))
            val turn = PieceColor.valueOf(obj.getString("currentTurn"))
            val diff = DifficultyLevel.valueOf(obj.getString("difficulty"))
            val timer = GameTimerOption.valueOf(obj.getString("timerOption"))
            
            val board = ChessBoard(initialize = false)
            val bArray = obj.getJSONArray("board")
            for (i in 0 until bArray.length()) {
                val p = bArray.getJSONObject(i)
                board.setPiece(p.getInt("r"), p.getInt("c"), Piece(PieceType.valueOf(p.getString("t")), PieceColor.valueOf(p.getString("col"))))
            }
            board.whiteKingMoved = obj.optBoolean("wkm")
            board.whiteRookKingsideMoved = obj.optBoolean("wrk")
            board.whiteRookQueensideMoved = obj.optBoolean("wrq")
            board.blackKingMoved = obj.optBoolean("bkm")
            board.blackRookKingsideMoved = obj.optBoolean("brk")
            board.blackRookQueensideMoved = obj.optBoolean("brq")
            
            if (obj.has("epr") && obj.has("epc")) {
                board.enPassantTarget = Position(obj.getInt("epr"), obj.getInt("epc"))
            }

            val history = mutableListOf<Move>()
            val hArray = obj.getJSONArray("history")
            for (i in 0 until hArray.length()) {
                val m = hArray.getJSONObject(i)
                history.add(Move(
                    from = Position(m.getInt("fr"), m.getInt("fc")),
                    to = Position(m.getInt("tr"), m.getInt("tc")),
                    piece = Piece(PieceType.valueOf(m.getString("pt")), PieceColor.valueOf(m.getString("pc"))),
                    capturedPiece = if (m.has("ct")) Piece(PieceType.valueOf(m.getString("ct")), PieceColor.valueOf(m.getString("cc"))) else null,
                    promotion = if (m.has("promo")) PieceType.valueOf(m.getString("promo")) else null,
                    isCastling = m.optBoolean("cas"),
                    isEnPassant = m.optBoolean("ep")
                ))
            }

            val (capW, capB) = calculateCapturedFromHistory(history)
            
            // Reconstruct board signatures for threefold repetition
            val signatures = mutableMapOf<String, Int>()
            signatures[board.getBoardSignature()] = 1

            return ChessUiState(
                currentScreen = AppScreen.GAME,
                gameMode = mode,
                userColor = userCol,
                currentTurn = turn,
                difficulty = diff,
                timerOption = timer,
                board = board,
                moveHistory = history,
                capturedWhitePieces = capW,
                capturedBlackPieces = capB,
                whiteTimeMillis = obj.getLong("whiteTime"),
                blackTimeMillis = obj.getLong("blackTime"),
                selectedTheme = themeManager.getSelectedTheme(),
                boardViewMode = themeManager.getSelectedViewMode(),
                isSoundEnabled = themeManager.isSoundEnabled(),
                isMoveHintsEnabled = themeManager.isMoveHintsEnabled(),
                isSaveGameEnabled = true,
                hasPersistedGame = true,
                halfMoveClock = obj.optInt("hmc", 0),
                boardSignatures = signatures,
                gameStatus = GameStatus.IN_PROGRESS
            )
        } catch (e: Exception) {
            return null
        }
    }

    private fun calculateCapturedFromHistory(history: List<Move>): Pair<List<PieceType>, List<PieceType>> {
        val w = mutableListOf<PieceType>()
        val b = mutableListOf<PieceType>()
        history.forEach { m ->
            m.capturedPiece?.let { if (it.color == PieceColor.WHITE) w.add(it.type) else b.add(it.type) }
        }
        return Pair(w, b)
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
            tutorialPiece = pieceType,
            selectedTheme = themeManager.getSelectedTheme(),
            boardViewMode = themeManager.getSelectedViewMode(),
            isSoundEnabled = themeManager.isSoundEnabled(),
            isMoveHintsEnabled = themeManager.isMoveHintsEnabled(),
            capturedWhitePieces = emptyList(),
            capturedBlackPieces = emptyList(),
            playerLastMove = null,
            aiLastMove = null,
            lastMove = null,
            hintMove = null
        )
    }

    fun startSpecialMoveTutorial(type: SpecialTutorialType) {
        val board = ChessBoard(initialize = false)
        val userColor = PieceColor.WHITE
        val opponentColor = PieceColor.BLACK

        var primaryPos = Position(7, 4) // Default to King position for castling

        when (type) {
            SpecialTutorialType.CASTLING_KINGSIDE -> {
                primaryPos = Position(7, 4)
                board.setPiece(primaryPos, Piece(PieceType.KING, userColor))
                board.setPiece(Position(7, 7), Piece(PieceType.ROOK, userColor))
                board.setPiece(Position(0, 4), Piece(PieceType.KING, opponentColor))
            }
            SpecialTutorialType.CASTLING_QUEENSIDE -> {
                primaryPos = Position(7, 4)
                board.setPiece(primaryPos, Piece(PieceType.KING, userColor))
                board.setPiece(Position(7, 0), Piece(PieceType.ROOK, userColor))
                board.setPiece(Position(0, 4), Piece(PieceType.KING, opponentColor))
            }
            SpecialTutorialType.PAWN_PROMOTION -> {
                primaryPos = Position(1, 3)
                board.setPiece(primaryPos, Piece(PieceType.PAWN, userColor))
                board.setPiece(Position(7, 4), Piece(PieceType.KING, userColor))
                board.setPiece(Position(0, 7), Piece(PieceType.KING, opponentColor))
            }
            SpecialTutorialType.EN_PASSANT -> {
                primaryPos = Position(3, 3)
                board.setPiece(primaryPos, Piece(PieceType.PAWN, userColor))
                // Place black pawn at its starting square
                board.setPiece(Position(1, 4), Piece(PieceType.PAWN, opponentColor))
                board.setPiece(Position(7, 4), Piece(PieceType.KING, userColor))
                board.setPiece(Position(0, 4), Piece(PieceType.KING, opponentColor))
            }
        }

        val initialTurn = if (type == SpecialTutorialType.EN_PASSANT) opponentColor else userColor
        val initialSelectedPos = if (type == SpecialTutorialType.EN_PASSANT) null else primaryPos
        val initialLegalMoves = initialSelectedPos?.let { board.getLegalMovesForPosition(it) } ?: emptyList()

        _uiState.value = ChessUiState(
            currentScreen = AppScreen.GAME,
            gameMode = GameMode.SPECIAL_MOVE,
            board = board,
            userColor = userColor,
            currentTurn = initialTurn,
            selectedPosition = initialSelectedPos,
            legalMovesForSelected = initialLegalMoves,
            specialTutorialType = type,
            gameStatus = GameStatus.IN_PROGRESS,
            selectedTheme = themeManager.getSelectedTheme(),
            boardViewMode = themeManager.getSelectedViewMode(),
            isSoundEnabled = themeManager.isSoundEnabled(),
            isMoveHintsEnabled = themeManager.isMoveHintsEnabled()
        )

        // If it's En Passant, trigger the black pawn move automatically
        if (type == SpecialTutorialType.EN_PASSANT) {
            viewModelScope.launch {
                delay(800) // Slightly longer delay for clarity
                val move = Move(
                    from = Position(1, 4),
                    to = Position(3, 4),
                    piece = Piece(PieceType.PAWN, opponentColor)
                )
                // Execute move directly on current state
                val updatedBoard = _uiState.value.board.copy()
                updatedBoard.applyMove(move)
                
                // After black moves, select the white pawn (primaryPos)
                val legalMovesForWhitePawn = updatedBoard.getLegalMovesForPosition(primaryPos)

                _uiState.value = _uiState.value.copy(
                    board = updatedBoard,
                    currentTurn = userColor,
                    selectedPosition = primaryPos,
                    legalMovesForSelected = legalMovesForWhitePawn,
                    aiLastMove = move,
                    lastMove = move
                )
                SoundManager.playMoveSound(PieceType.PAWN, false, false)
            }
        }
    }

    fun resetTutorialBoard() {
        val currentPiece = _uiState.value.tutorialPiece ?: PieceType.ROOK
        startTutorialMode(currentPiece)
    }

    fun startNextPuzzle() {
        val state = _uiState.value
        val currentCategory = state.puzzleCategory ?: return
        val currentLevel = state.puzzleLevel ?: return
        val mode = state.gameMode
        
        val list = if (mode == GameMode.ONE_MOVE) {
            when (currentCategory) {
                "Nhập môn" -> OneMoveBeginner.list
                "Dễ" -> OneMoveEasy.list
                "Trung bình" -> OneMoveMedium.list
                "Khó" -> OneMoveHard.list
                "Cao thủ" -> OneMoveExpert.list
                else -> emptyList()
            }
        } else {
            when (currentCategory) {
                "Nhập môn" -> PuzzlesBeginner.list
                "Dễ" -> PuzzlesEasy.list
                "Trung bình" -> PuzzlesMedium.list
                "Khó" -> PuzzlesHard.list
                "Cao thủ" -> PuzzlesExpert.list
                else -> emptyList()
            }
        }

        val nextLevel = currentLevel + 1
        val nextPuzzle = list.find { it.level == nextLevel }
        
        if (nextPuzzle != null) {
            startPuzzleMode(nextPuzzle.fen, currentCategory, nextLevel, mode)
        } else {
            // No more puzzles in this category, return to setup
            navigateToSetup()
        }
    }
}
