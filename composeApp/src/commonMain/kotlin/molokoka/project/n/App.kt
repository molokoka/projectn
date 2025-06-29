package molokoka.project.n

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import projectn.composeapp.generated.resources.Res
import projectn.composeapp.generated.resources.back
import projectn.composeapp.generated.resources.choose_different_size
import projectn.composeapp.generated.resources.choose_size
import projectn.composeapp.generated.resources.congratulations
import projectn.composeapp.generated.resources.n_queens
import projectn.composeapp.generated.resources.next
import projectn.composeapp.generated.resources.play_again
import projectn.composeapp.generated.resources.prev
import projectn.composeapp.generated.resources.size_format
import projectn.composeapp.generated.resources.start_game
import projectn.composeapp.generated.resources.win_message

sealed class GameState {
    object SetBoardSize : GameState()
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
    var gameState by remember { mutableStateOf<GameState>(GameState.SetBoardSize) }
    var boardSize by remember { mutableIntStateOf(boardConfig.defaultBoardSize) }
    var boardState by remember(boardSize) { mutableStateOf(ChessBoardState(boardSize = boardSize)) }

    Column(
        modifier = Modifier
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (val currentState = gameState) {
            is GameState.SetBoardSize -> {
                SetBoardSizeScreen(
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
                    onBackToSizeSelection = {
                        gameState = GameState.SetBoardSize
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
                    onBackToSizeSelection = {
                        gameState = GameState.SetBoardSize
                    }
                )
            }
        }
    }
}

@Composable
private fun SizeControls(
    boardSize: Int,
    onBoardSizeChange: (Int) -> Unit,
    boardConfig: BoardConfig
) {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PixelatedText(
            text = stringResource(Res.string.prev),
            pixelSize = 2.dp,
            color = if (boardSize > boardConfig.minBoardSize) Color.Blue else Color.Gray,
            modifier = Modifier
                .clickable(enabled = boardSize > boardConfig.minBoardSize) {
                    onBoardSizeChange((boardSize - 1).coerceAtLeast(boardConfig.minBoardSize))
                }
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.padding(horizontal = 16.dp))

        PixelatedText(
            text = stringResource(Res.string.size_format, boardSize, boardSize),
            pixelSize = 3.dp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.padding(horizontal = 16.dp))

        PixelatedText(
            text = stringResource(Res.string.next),
            pixelSize = 2.dp,
            color = if (boardSize < boardConfig.maxBoardSize) Color.Blue else Color.Gray,
            modifier = Modifier
                .clickable(enabled = boardSize < boardConfig.maxBoardSize) {
                    onBoardSizeChange((boardSize + 1).coerceAtMost(boardConfig.maxBoardSize))
                }
                .padding(8.dp)
        )
    }
}

@Composable
fun SetBoardSizeScreen(
    boardSize: Int,
    onBoardSizeChange: (Int) -> Unit,
    boardConfig: BoardConfig,
    onStartGame: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        PixelatedText(
            text = stringResource(Res.string.n_queens),
            pixelSize = 4.dp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        PixelatedText(
            text = stringResource(Res.string.choose_size),
            pixelSize = 2.dp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        SizeControls(
            boardSize = boardSize,
            onBoardSizeChange = onBoardSizeChange,
            boardConfig = boardConfig
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        PixelatedText(
            text = stringResource(Res.string.start_game),
            pixelSize = 3.dp,
            color = Color.Blue,
            modifier = Modifier
                .clickable { onStartGame() }
                .padding(16.dp)
        )
    }
}

@Composable
fun GameScreen(
    boardState: ChessBoardState,
    onSquareClicked: (ChessCoordinate) -> Unit,
    onBackToSizeSelection: () -> Unit
) {
    Column {
        BasicText(
            text = stringResource(Res.string.back),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Blue
            ),
            modifier = Modifier
                .clickable { onBackToSizeSelection() }
                .padding(16.dp)
        )
        
        ChessBoard(
            config = ChessBoardConfig(),
            boardState = boardState,
            onSquareClicked = onSquareClicked,
            modifier = Modifier
                .weight(1f)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState())
        )
    }
}

@Composable
fun WinScreen(
    boardSize: Int,
    onPlayAgain: () -> Unit,
    onBackToSizeSelection: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        BasicText(
            text = stringResource(Res.string.congratulations),
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        BasicText(
            text = stringResource(Res.string.win_message, boardSize, boardSize),
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            ),
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        BasicText(
            text = stringResource(Res.string.play_again),
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Blue
            ),
            modifier = Modifier
                .clickable { onPlayAgain() }
                .padding(16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        BasicText(
            text = stringResource(Res.string.choose_different_size),
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Blue
            ),
            modifier = Modifier
                .clickable { onBackToSizeSelection() }
                .padding(16.dp)
        )
    }
}