package molokoka.project.n

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import molokoka.project.n.analysis.AnalysisScreen
import molokoka.project.n.di.appModule
import molokoka.project.n.setup.Setup
import molokoka.project.n.ui.ChessBoardUiConfigProvider
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
@Preview
fun App() {
    KoinApplication(configuration = koinConfiguration { modules(appModule) }) {
        ChessBoardUiConfigProvider {
            AppContent()
        }
    }
}

@Serializable
data object SetupRoute : NavKey

@Serializable
data object AnalysisRoute : NavKey

val navSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        include(SavedStateConfiguration.DEFAULT.serializersModule)
        polymorphic(NavKey::class) {
            subclass(SetupRoute::class)
            subclass(AnalysisRoute::class)
        }
    }
}

@Composable
fun AppContent() {
    val backStack = rememberNavBackStack(navSavedStateConfiguration, SetupRoute)

    NavDisplay(
        modifier = Modifier
            .background(Color.White)
            .safeContentPadding()
            .fillMaxSize(),
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<SetupRoute> {
                Setup(onStart = { backStack.add(AnalysisRoute) })
            }
            entry<AnalysisRoute> {
                AnalysisScreen(onBackToInit = { backStack.removeLastOrNull() })
            }
        }
    )
}
