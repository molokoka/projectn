package molokoka.project.n.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PixelatedText(
    text: String,
    modifier: Modifier = Modifier,
    pixelSize: Dp = 3.dp,
    color: Color = Color.Black,
    charSpacing: Dp = 1.dp
) {
    val charWidth = pixelSize * 6
    val charHeight = pixelSize * 8
    val totalWidth = (charWidth + charSpacing) * text.length
    
    Canvas(
        modifier = modifier.size(totalWidth, charHeight)
    ) {
        val pixelSizePx = pixelSize.toPx()
        val charSpacingPx = charSpacing.toPx()
        
        text.forEachIndexed { index, char ->
            val x = index * (charWidth.toPx() + charSpacingPx)
            drawPixelChar(char, x, 0f, pixelSizePx, color)
        }
    }
}

private fun DrawScope.drawPixelChar(
    char: Char,
    x: Float,
    y: Float,
    pixelSize: Float,
    color: Color
) {
    val patterns = getCharacterPatterns()
    val pattern = patterns[char.uppercaseChar()] ?: patterns[' ']!!
    
    pattern.forEachIndexed { row, line ->
        line.forEachIndexed { col, pixel ->
            if (pixel == '█') {
                drawRect(
                    color = color,
                    topLeft = Offset(x + col * pixelSize, y + row * pixelSize),
                    size = Size(pixelSize, pixelSize)
                )
            }
        }
    }
}

private fun getCharacterPatterns(): Map<Char, Array<String>> {
    return mapOf(
        ' ' to arrayOf(
            "      ",
            "      ",
            "      ",
            "      ",
            "      ",
            "      ",
            "      ",
            "      "
        ),
        'A' to arrayOf(
            "  ██  ",
            " █  █ ",
            "█    █",
            "█    █",
            "██████",
            "█    █",
            "█    █",
            "      "
        ),
        'B' to arrayOf(
            "█████ ",
            "█    █",
            "█    █",
            "█████ ",
            "█    █",
            "█    █",
            "█████ ",
            "      "
        ),
        'C' to arrayOf(
            " █████",
            "█     ",
            "█     ",
            "█     ",
            "█     ",
            "█     ",
            " █████",
            "      "
        ),
        'D' to arrayOf(
            "█████ ",
            "█    █",
            "█    █",
            "█    █",
            "█    █",
            "█    █",
            "█████ ",
            "      "
        ),
        'E' to arrayOf(
            "██████",
            "█     ",
            "█     ",
            "█████ ",
            "█     ",
            "█     ",
            "██████",
            "      "
        ),
        'F' to arrayOf(
            "██████",
            "█     ",
            "█     ",
            "█████ ",
            "█     ",
            "█     ",
            "█     ",
            "      "
        ),
        'G' to arrayOf(
            " █████",
            "█     ",
            "█     ",
            "█  ███",
            "█    █",
            "█    █",
            " █████",
            "      "
        ),
        'H' to arrayOf(
            "█    █",
            "█    █",
            "█    █",
            "██████",
            "█    █",
            "█    █",
            "█    █",
            "      "
        ),
        'I' to arrayOf(
            "██████",
            "  █   ",
            "  █   ",
            "  █   ",
            "  █   ",
            "  █   ",
            "██████",
            "      "
        ),
        'J' to arrayOf(
            "██████",
            "    █ ",
            "    █ ",
            "    █ ",
            "    █ ",
            "█   █ ",
            " ████ ",
            "      "
        ),
        'K' to arrayOf(
            "█    █",
            "█   █ ",
            "█ █   ",
            "██    ",
            "█ █   ",
            "█  █  ",
            "█   █ ",
            "      "
        ),
        'L' to arrayOf(
            "█     ",
            "█     ",
            "█     ",
            "█     ",
            "█     ",
            "█     ",
            "██████",
            "      "
        ),
        'M' to arrayOf(
            "█    █",
            "██  ██",
            "█ ██ █",
            "█    █",
            "█    █",
            "█    █",
            "█    █",
            "      "
        ),
        'N' to arrayOf(
            "█    █",
            "██   █",
            "█ █  █",
            "█  █ █",
            "█   ██",
            "█    █",
            "█    █",
            "      "
        ),
        'O' to arrayOf(
            " ████ ",
            "█    █",
            "█    █",
            "█    █",
            "█    █",
            "█    █",
            " ████ ",
            "      "
        ),
        'P' to arrayOf(
            "█████ ",
            "█    █",
            "█    █",
            "█████ ",
            "█     ",
            "█     ",
            "█     ",
            "      "
        ),
        'Q' to arrayOf(
            " █████",
            "█    █",
            "█    █",
            "█    █",
            "█  █ █",
            "█   ██",
            " ██████",
            "      "
        ),
        'R' to arrayOf(
            "█████ ",
            "█    █",
            "█    █",
            "█████ ",
            "█ █   ",
            "█  █  ",
            "█   █ ",
            "      "
        ),
        'S' to arrayOf(
            " █████",
            "█     ",
            "█     ",
            " ████ ",
            "     █",
            "     █",
            "█████ ",
            "      "
        ),
        'T' to arrayOf(
            "██████",
            "  █   ",
            "  █   ",
            "  █   ",
            "  █   ",
            "  █   ",
            "  █   ",
            "      "
        ),
        'U' to arrayOf(
            "█    █",
            "█    █",
            "█    █",
            "█    █",
            "█    █",
            "█    █",
            " █████",
            "      "
        ),
        'V' to arrayOf(
            "█    █",
            "█    █",
            "█    █",
            "█    █",
            " █  █ ",
            "  ██  ",
            "  ██  ",
            "      "
        ),
        'W' to arrayOf(
            "█    █",
            "█    █",
            "█    █",
            "█ ██ █",
            "██  ██",
            "█    █",
            "█    █",
            "      "
        ),
        'X' to arrayOf(
            "█    █",
            " █  █ ",
            "  ██  ",
            "  ██  ",
            "  ██  ",
            " █  █ ",
            "█    █",
            "      "
        ),
        'Y' to arrayOf(
            "█    █",
            "█    █",
            " █  █ ",
            "  ██  ",
            "  ██  ",
            "  ██  ",
            "  ██  ",
            "      "
        ),
        'Z' to arrayOf(
            "██████",
            "     █",
            "    █ ",
            "   █  ",
            "  █   ",
            " █    ",
            "██████",
            "      "
        ),
        '0' to arrayOf(
            " ████ ",
            "█    █",
            "█   ██",
            "█  █ █",
            "██   █",
            "█    █",
            " ████ ",
            "      "
        ),
        '1' to arrayOf(
            "  █   ",
            " ██   ",
            "  █   ",
            "  █   ",
            "  █   ",
            "  █   ",
            "██████",
            "      "
        ),
        '2' to arrayOf(
            " █████",
            "█    █",
            "     █",
            "  ████",
            " █    ",
            "█     ",
            "██████",
            "      "
        ),
        '3' to arrayOf(
            "██████",
            "     █",
            "     █",
            " █████",
            "     █",
            "     █",
            "██████",
            "      "
        ),
        '4' to arrayOf(
            "█    █",
            "█    █",
            "█    █",
            "██████",
            "     █",
            "     █",
            "     █",
            "      "
        ),
        '5' to arrayOf(
            "██████",
            "█     ",
            "█     ",
            "██████",
            "     █",
            "     █",
            "██████",
            "      "
        ),
        '6' to arrayOf(
            " █████",
            "█     ",
            "█     ",
            "██████",
            "█    █",
            "█    █",
            " █████",
            "      "
        ),
        '7' to arrayOf(
            "██████",
            "     █",
            "    █ ",
            "   █  ",
            "  █   ",
            " █    ",
            "█     ",
            "      "
        ),
        '8' to arrayOf(
            " ████ ",
            "█    █",
            "█    █",
            " ████ ",
            "█    █",
            "█    █",
            " ████ ",
            "      "
        ),
        '9' to arrayOf(
            " █████",
            "█    █",
            "█    █",
            " █████",
            "     █",
            "     █",
            " █████",
            "      "
        ),
        '-' to arrayOf(
            "      ",
            "      ",
            "      ",
            "██████",
            "      ",
            "      ",
            "      ",
            "      "
        ),
        ':' to arrayOf(
            "      ",
            "  ██  ",
            "  ██  ",
            "      ",
            "      ",
            "  ██  ",
            "  ██  ",
            "      "
        ),
        '/' to arrayOf(
            "     █",
            "    █ ",
            "   █  ",
            "  █   ",
            " █    ",
            "█     ",
            "      ",
            "      "
        ),
        '█' to arrayOf(
            "██████",
            "██████",
            "██████",
            "██████",
            "██████",
            "██████",
            "██████",
            "██████"
        ),
        '_' to arrayOf(
            "      ",
            "      ",
            "      ",
            "      ",
            "      ",
            "      ",
            "██████",
            "      "
        )
    )
}