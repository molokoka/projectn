package molokoka.project.n

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import molokoka.project.n.ui.theme.AppTheme
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
@Preview
fun App() {
    KoinApplication(configuration = koinConfiguration { modules(appModule) }) {
        AppTheme {
            AppContent()
        }
    }
}

@Serializable
data object AnalysisRoute : NavKey

val navSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        include(SavedStateConfiguration.DEFAULT.serializersModule)
        polymorphic(NavKey::class) {
            subclass(AnalysisRoute::class)
        }
    }
}

@Composable
fun AppContent() {
    val backStack = rememberNavBackStack(navSavedStateConfiguration, AnalysisRoute)

    NavDisplay(
        modifier = Modifier
            .background(AppTheme.colors.background)
            .safeContentPadding()
            .fillMaxSize(),
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<AnalysisRoute> {
                AnalysisScreen()
            }
        }
    )
}
