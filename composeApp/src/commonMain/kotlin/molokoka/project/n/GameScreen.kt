@file:OptIn(ExperimentalTime::class)

package molokoka.project.n

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import molokoka.project.n.ui.ChessBoard
import molokoka.project.n.ui.ChessBoardState
import molokoka.project.n.domain.chess.ChessCoordinates
import molokoka.project.n.ui.PixelatedText
import molokoka.project.n.utils.formatTimeInMillis
import org.jetbrains.compose.resources.stringResource
import projectn.composeapp.generated.resources.*
import kotlin.time.Clock.System.now
import kotlin.time.ExperimentalTime

data class GameScreenState(
    val chessBoardSize: Int,
    val startTime: Long = now().toEpochMilliseconds(),
    val triggerForTimerRecomposition: Long = now().toEpochMilliseconds()
) {
    fun getElapsedTimeFormatted(): String {
        val elapsedMillis = now().toEpochMilliseconds() - startTime
        return formatTimeInMillis(elapsedMillis)
    }
    
    fun getQueensPlaced(boardState: ChessBoardState): Int = boardState.queensPositions.size
    
    fun restart(): GameScreenState = GameScreenState(chessBoardSize = this.chessBoardSize)
}

@Composable
fun GameScreen(
    boardState: ChessBoardState,
    onSquareClicked: (ChessCoordinates, Long) -> Unit,
    onBackToInit: () -> Unit,
    onRestart: () -> Unit
) {
    var gameScreenState by remember(boardState.chessBoardSize) {
        mutableStateOf(GameScreenState(chessBoardSize = boardState.chessBoardSize))
    }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            gameScreenState = gameScreenState.copy(triggerForTimerRecomposition = now().toEpochMilliseconds())
        }
    }
    
    Column {
        GameInfoHeader(
            gameScreenState = gameScreenState,
            boardState = boardState
        )
        Spacer(modifier = Modifier.height(8.dp))
        ChessBoard(
            boardState = boardState,
            onSquareClicked = { coordinate ->
                onSquareClicked(coordinate, gameScreenState.startTime)
            },
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterHorizontally)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState())
        )
        
        BottomBar(
            onRestart = {
                onRestart()
                gameScreenState = gameScreenState.restart()
            },
            onBackToInit = onBackToInit
        )
    }
}

@Composable
private fun GameInfoHeader(
    gameScreenState: GameScreenState,
    boardState: ChessBoardState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PixelatedText(
            text = stringResource(Res.string.timer, gameScreenState.getElapsedTimeFormatted()),
            pixelSize = 2.dp,
            color = Color.Black,
            modifier = Modifier
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        PixelatedText(
            text = stringResource(
                Res.string.queens_left,
                gameScreenState.getQueensPlaced(boardState),
                gameScreenState.chessBoardSize
            ),
            pixelSize = 2.dp,
            color = Color.Black,
            modifier = Modifier
                .padding(8.dp)
        )
    }
}

@Composable
private fun BottomBar(onRestart: () -> Unit, onBackToInit: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PixelatedText(
            text = stringResource(Res.string.restart),
            pixelSize = 2.dp,
            color = Color.Blue,
            modifier = Modifier
                .clickable { onRestart() }
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        PixelatedText(
            text = stringResource(Res.string.exit_game),
            pixelSize = 2.dp,
            color = Color.Blue,
            modifier = Modifier
                .clickable { onBackToInit() }
                .padding(8.dp)
        )
    }
}
