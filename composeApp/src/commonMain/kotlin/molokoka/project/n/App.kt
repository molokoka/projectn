package molokoka.project.n

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import molokoka.project.n.chess.ChessBoardState
import molokoka.project.n.di.appModule
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import kotlin.time.Clock.System.now
import kotlin.time.ExperimentalTime

sealed class GameState {
    object Init : GameState()
    data class Game(val boardSize: Int) : GameState()
    data class Win(val boardSize: Int, val timeInSeconds: Long) : GameState()
    data class Leaderboard(val boardSize: Int) : GameState()
}

data class BoardConfig(
    val minBoardSize: Int = 4,
    val maxBoardSize: Int = 28,
    val defaultBoardSize: Int = 8
)

@OptIn(ExperimentalTime::class)
@Composable
@Preview
fun App() {
    KoinApplication(application = { modules(appModule) }) {
        AppContent()
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun AppContent() {
    val boardConfig = BoardConfig()
    var gameState by remember { mutableStateOf<GameState>(GameState.Init) }
    var boardSize by remember { mutableIntStateOf(boardConfig.defaultBoardSize) }
    var boardState by remember(boardSize) { mutableStateOf(ChessBoardState(boardSize = boardSize)) }

    Column(
        modifier = Modifier
            .background(Color.White)
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (val currentState = gameState) {
            is GameState.Init -> {
                InitScreen(
                    boardSize = boardSize,
                    onBoardSizeChange = { boardSize = it },
                    boardConfig = boardConfig,
                    onStartGame = {
                        boardState = ChessBoardState(boardSize = boardSize)
                        gameState = GameState.Game(boardSize)
                    },
                    onShowLeaderboard = {
                        gameState = GameState.Leaderboard(boardSize)
                    }
                )
            }
            is GameState.Game -> {
                GameScreen(
                    boardState = boardState,
                    onSquareClicked = { coordinate, startTime ->
                        val newBoardState = boardState.toggleQueen(coordinate)
                        boardState = newBoardState
                        
                        // Check for win condition
                        if (GameLogic.isWinCondition(newBoardState.queensPositions, currentState.boardSize)) {
                            val elapsedTime = (now().toEpochMilliseconds() - startTime) / 1000
                            gameState = GameState.Win(currentState.boardSize, elapsedTime)
                        }
                    },
                    onBackToInit = {
                        gameState = GameState.Init
                    },
                    onRestart = {
                        boardState = ChessBoardState(boardSize = currentState.boardSize)
                    }
                )
            }
            is GameState.Win -> {
                WinScreen(
                    boardSize = currentState.boardSize,
                    timeInSeconds = currentState.timeInSeconds,
                    onPlayAgain = {
                        boardState = ChessBoardState(boardSize = currentState.boardSize)
                        gameState = GameState.Game(currentState.boardSize)
                    },
                    onBackToInit = {
                        gameState = GameState.Init
                    }
                )
            }
            is GameState.Leaderboard -> {
                LeaderboardScreen(
                    boardSize = currentState.boardSize,
                    onBackToInit = {
                        gameState = GameState.Init
                    }
                )
            }
        }
    }
}