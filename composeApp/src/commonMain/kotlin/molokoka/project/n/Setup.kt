package molokoka.project.n

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import molokoka.project.n.ui.karmaticArcade
import org.jetbrains.compose.resources.stringResource
import projectn.composeapp.generated.resources.Res
import projectn.composeapp.generated.resources.analysis_board
import projectn.composeapp.generated.resources.start

@Composable
fun Setup(onStart: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        BasicText(
            text = stringResource(Res.string.analysis_board),
            style = TextStyle(
                fontFamily = karmaticArcade(),
                fontSize = 32.sp,
                color = Color.Black
            ),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        BasicText(
            text = stringResource(Res.string.start),
            style = TextStyle(
                fontFamily = karmaticArcade(),
                fontSize = 24.sp,
                color = Color.Blue
            ),
            modifier = Modifier
                .clickable { onStart() }
                .padding(16.dp)
        )
    }
}
