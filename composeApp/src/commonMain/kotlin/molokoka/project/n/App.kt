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
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

sealed class AppState {
    data object Setup : AppState()
    data class Game(val chessBoardState: ChessBoardState) : AppState()
    data object Win : AppState()
}

@Composable
@Preview
fun App() {
    KoinApplication(application = { modules(appModule) }) {
        ChessBoardUiConfigProvider {
            AppContent()
        }
    }
}

@Composable
fun AppContent() {
    var appState by remember { mutableStateOf<AppState>(AppState.Setup) }

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
                    onStartGame = {
                        appState = AppState.Game(ChessBoardState())
                    }
                )
            }
            is AppState.Game -> {
                GameScreen(
                    boardState = currentState.chessBoardState,
                    onSquareClicked = { coordinate ->
                        val newBoardState = currentState.chessBoardState.toggleQueen(coordinate)
                        appState = AppState.Game(newBoardState)

                        if (isWinCondition(newBoardState.queensPositions)) {
                            appState = AppState.Win
                        }
                    },
                    onBackToInit = {
                        appState = AppState.Setup
                    },
                    onRestart = {
                        appState = AppState.Game(ChessBoardState())
                    }
                )
            }
            is AppState.Win -> {
                WinScreen(
                    onPlayAgain = {
                        appState = AppState.Game(ChessBoardState())
                    },
                    onBackToInit = {
                        appState = AppState.Setup
                    }
                )
            }
        }
    }
}