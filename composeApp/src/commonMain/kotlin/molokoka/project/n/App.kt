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
import projectn.composeapp.generated.resources.next
import projectn.composeapp.generated.resources.prev
import projectn.composeapp.generated.resources.size_format

data class AppConfig(
    val minBoardSize: Int = 4,
    val maxBoardSize: Int = 28,
    val defaultBoardSize: Int = 8
)

@Composable
@Preview
fun App() {
    val appConfig = AppConfig()

    Column(
        modifier = Modifier
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var boardSize by remember { mutableIntStateOf(appConfig.defaultBoardSize) }
        SizeControls(
            boardSize = boardSize,
            onBoardSizeChange = { boardSize = it },
            appConfig = appConfig
        )

        Spacer(modifier = Modifier.height(8.dp))

        var boardState by remember(boardSize) { mutableStateOf(ChessBoardState(boardSize = boardSize)) }
        ChessBoard(
            config = ChessBoardConfig(),
            boardState = boardState,
            onSquareClicked = { row, col ->
                boardState = boardState.toggleQueen(row, col)
            },
            modifier = Modifier
                .weight(1f)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState())
        )
    }
}

@Composable
private fun SizeControls(
    boardSize: Int,
    onBoardSizeChange: (Int) -> Unit,
    appConfig: AppConfig
) {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicText(
            text = stringResource(Res.string.prev),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (boardSize > appConfig.minBoardSize) Color.Blue else Color.Gray
            ),
            modifier = Modifier
                .clickable(enabled = boardSize > appConfig.minBoardSize) {
                    onBoardSizeChange((boardSize - 1).coerceAtLeast(appConfig.minBoardSize))
                }
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.padding(horizontal = 16.dp))

        BasicText(
            text = stringResource(Res.string.size_format, boardSize, boardSize),
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        )

        Spacer(modifier = Modifier.padding(horizontal = 16.dp))

        BasicText(
            text = stringResource(Res.string.next),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (boardSize < appConfig.maxBoardSize) Color.Blue else Color.Gray
            ),
            modifier = Modifier
                .clickable(enabled = boardSize < appConfig.maxBoardSize) {
                    onBoardSizeChange((boardSize + 1).coerceAtMost(appConfig.maxBoardSize))
                }
                .padding(8.dp)
        )
    }
}