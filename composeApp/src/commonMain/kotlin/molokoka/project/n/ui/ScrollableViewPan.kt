package molokoka.project.n.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import kotlin.math.roundToInt

/**
 *
 * A quick solution rather than a general one. It exists because analysis rows must drag together and the rows
 * cannot share a `ScrollState`: each scrollable node writes its own `maxValue` while
 * measuring, so the shortest row would win and clamp the offset to zero for all of them.
 *
 */
@Stable
internal class ScrollableViewPan {
    var offset by mutableFloatStateOf(0f)
        private set

    private var limit by mutableFloatStateOf(0f)

    /** Consumes a horizontal scroll [delta], returning the part of it actually used. */
    fun drag(delta: Float): Float {
        val previous = offset

        offset = (offset - delta).coerceIn(0f, limit)

        return previous - offset
    }

    /** Widens the pan limit to cover a label overrunning its row by [overrun] pixels. */
    fun stretchTo(overrun: Float) {
        if (overrun > limit) limit = overrun
    }
}

/**
 * Draws the label at its natural width, shifted left by the shared [pan].
 */
internal fun Modifier.panned(pan: ScrollableViewPan) =
    layout { measurable, constraints ->
        val label = measurable.measure(constraints.copy(maxWidth = Constraints.Infinity))
        val visible = label.width.coerceAtMost(constraints.maxWidth)

        pan.stretchTo((label.width - visible).toFloat())

        layout(visible, label.height) {
            label.place(-pan.offset.roundToInt(), 0)
        }
    }
