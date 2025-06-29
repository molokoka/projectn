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
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    var currentBoardSize by remember { mutableStateOf(8) }
    val testSizes = (4..28).toList()
    val currentIndex = testSizes.indexOf(currentBoardSize)
    
    Column(
        modifier = Modifier
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Size controls
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicText(
                text = "Prev",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (currentIndex > 0) Color.Blue else Color.Gray
                ),
                modifier = Modifier
                    .clickable(enabled = currentIndex > 0) {
                        if (currentIndex > 0) {
                            currentBoardSize = testSizes[currentIndex - 1]
                        }
                    }
                    .padding(8.dp)
            )
            
            Spacer(modifier = Modifier.padding(horizontal = 16.dp))
            
            BasicText(
                text = "Size: ${currentBoardSize}x${currentBoardSize}",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
            
            Spacer(modifier = Modifier.padding(horizontal = 16.dp))
            
            BasicText(
                text = "Next",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (currentIndex < testSizes.size - 1) Color.Blue else Color.Gray
                ),
                modifier = Modifier
                    .clickable(enabled = currentIndex < testSizes.size - 1) {
                        if (currentIndex < testSizes.size - 1) {
                            currentBoardSize = testSizes[currentIndex + 1]
                        }
                    }
                    .padding(8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Chess board
        ChessBoard(
            boardSize = currentBoardSize,
            modifier = Modifier
                .weight(1f)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState())
        )
    }
}