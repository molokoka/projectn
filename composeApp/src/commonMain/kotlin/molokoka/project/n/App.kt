package molokoka.project.n

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import molokoka.project.n.domain.nqueen.isWinCondition
import molokoka.project.n.ui.ChessBoardUiConfigProvider
import molokoka.project.n.ui.ChessBoardState
import molokoka.project.n.di.appModule
import molokoka.project.n.domain.chess.DEFAULT_BOARD_SIZE
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import kotlin.time.Clock.System.now
import kotlin.time.ExperimentalTime

sealed class AppState {
    data class Setup(val chessBoardSize: Int) : AppState()
    // TODO: investigate if
    data class Game(val chessBoardSize: Int, val chessBoardState: ChessBoardState) : AppState()
    data class Win(val chessBoardSize: Int, val timeInMillis: Long) : AppState()
    data class Leaderboard(val chessBoardSize: Int) : AppState()
}

@OptIn(ExperimentalTime::class)
@Composable
@Preview
fun App() {
    KoinApplication(application = { modules(appModule) }) {
        ChessBoardUiConfigProvider {
            AppContent()
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun AppContent() {
    var appState by remember { mutableStateOf<AppState>(AppState.Setup(DEFAULT_BOARD_SIZE)) }

    Column(
        modifier = Modifier
            .background(Color.White)
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (val currentState = appState) {
            is AppState.Setup -> {
                Setup(
                    chessBoardSize = currentState.chessBoardSize,
                    onBoardSizeChange = { newSize ->
                        appState = AppState.Setup(newSize)
                    },
                    onStartGame = {
                        val newBoardState = ChessBoardState(chessBoardSize = currentState.chessBoardSize)
                        appState = AppState.Game(currentState.chessBoardSize, newBoardState)
                    },
                    onShowLeaderboard = {
                        appState = AppState.Leaderboard(currentState.chessBoardSize)
                    }
                )
            }
            is AppState.Game -> {
                GameScreen(
                    boardState = currentState.chessBoardState,
                    onSquareClicked = { coordinate, startTimeInMillis ->
                        val newBoardState = currentState.chessBoardState.toggleQueen(coordinate)
                        appState = currentState.copy(chessBoardState = newBoardState)

                        if (isWinCondition(newBoardState.queensPositions, currentState.chessBoardSize)) {
                            val elapsedTimeInMillis = (now().toEpochMilliseconds() - startTimeInMillis)
                            appState = AppState.Win(currentState.chessBoardSize, elapsedTimeInMillis)
                        }
                    },
                    onBackToInit = {
                        appState = AppState.Setup(currentState.chessBoardSize)
                    },
                    onRestart = {
                        appState = currentState.copy(chessBoardState = ChessBoardState(chessBoardSize = currentState.chessBoardSize))
                    }
                )
            }
            is AppState.Win -> {
                WinScreen(
                    chessBoardSize = currentState.chessBoardSize,
                    timeInMillis = currentState.timeInMillis,
                    onPlayAgain = {
                        val newBoardState = ChessBoardState(chessBoardSize = currentState.chessBoardSize)
                        appState = AppState.Game(currentState.chessBoardSize, newBoardState)
                    },
                    onBackToInit = {
                        appState = AppState.Setup(currentState.chessBoardSize)
                    }
                )
            }
            is AppState.Leaderboard -> {
                LeaderBoardScreen(
                    chessBoardSize = currentState.chessBoardSize,
                    onBackToInit = {
                        appState = AppState.Setup(currentState.chessBoardSize)
                    }
                )
            }
        }
    }
}