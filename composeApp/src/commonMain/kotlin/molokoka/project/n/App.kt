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
import molokoka.project.n.di.appModule
import molokoka.project.n.ui.ChessBoardUiConfigProvider
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

sealed class AppState {
    data object Setup : AppState()
    data object Analysis : AppState()
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
        when (appState) {
            is AppState.Setup -> {
                Setup(
                    onStart = {
                        appState = AppState.Analysis
                    }
                )
            }
            is AppState.Analysis -> {
                AnalysisScreen(
                    onSquareClicked = { },
                    onReset = { },
                    onBackToInit = {
                        appState = AppState.Setup
                    }
                )
            }
        }
    }
}
