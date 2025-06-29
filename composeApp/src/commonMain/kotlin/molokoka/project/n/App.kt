package molokoka.project.n

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
import molokoka.project.n.chess.ChessBoardState
import org.jetbrains.compose.ui.tooling.preview.Preview

sealed class GameState {
    object Init : GameState()
    data class Game(val boardSize: Int) : GameState()
    data class Win(val boardSize: Int) : GameState()
}

data class BoardConfig(
    val minBoardSize: Int = 4,
    val maxBoardSize: Int = 28,
    val defaultBoardSize: Int = 8
)

@Composable
@Preview
fun App() {
    val boardConfig = BoardConfig()
    var gameState by remember { mutableStateOf<GameState>(GameState.Init) }
    var boardSize by remember { mutableIntStateOf(boardConfig.defaultBoardSize) }
    var boardState by remember(boardSize) { mutableStateOf(ChessBoardState(boardSize = boardSize)) }

    Column(
        modifier = Modifier
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
                    }
                )
            }
            is GameState.Game -> {
                GameScreen(
                    boardState = boardState,
                    onSquareClicked = { coordinate ->
                        val newBoardState = boardState.toggleQueen(coordinate)
                        boardState = newBoardState
                        
                        // Check for win condition
                        if (GameLogic.isWinCondition(newBoardState.queensPositions, currentState.boardSize)) {
                            gameState = GameState.Win(currentState.boardSize)
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
                    onPlayAgain = {
                        boardState = ChessBoardState(boardSize = currentState.boardSize)
                        gameState = GameState.Game(currentState.boardSize)
                    },
                    onBackToInit = {
                        gameState = GameState.Init
                    }
                )
            }
        }
    }
}