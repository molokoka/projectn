package molokoka.project.n.ui.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import kotlinx.coroutines.launch

private const val PressedAlpha = 1f
private const val HoveredAlpha = 0.35f

private const val FadeInMillis = 90
private const val FadeOutMillis = 220

/**
 * Draws [overlay] over a pressed, hovered or focused node, fading it in and out.
 *
 * The fade is what makes a tap visible. Interactions are collected one at a time, so a release
 * waits for the press to finish fading in before it starts fading out, and a tap held for a
 * single frame still shows the whole animation.
 */
data class AppIndication(private val overlay: Color) : IndicationNodeFactory {

    override fun create(interactionSource: InteractionSource): DelegatableNode =
        AppIndicationNode(interactionSource, overlay)
}

private class AppIndicationNode(
    private val interactionSource: InteractionSource,
    private val overlay: Color
) : Modifier.Node(), DrawModifierNode {

    private val shown = Animatable(0f)

    private var presses = 0
    private var hovers = 0
    private var focuses = 0

    override fun onAttach() {
        coroutineScope.launch {
            presses = 0
            hovers = 0
            focuses = 0
            shown.snapTo(0f)

            interactionSource.interactions.collect { interaction ->
                count(interaction)

                val target = target()
                val duration = if (target > shown.value) FadeInMillis else FadeOutMillis

                shown.animateTo(target, tween(duration))
            }
        }
    }

    override fun ContentDrawScope.draw() {
        drawContent()

        if (shown.value > 0f) {
            drawRect(
                color = overlay.copy(alpha = overlay.alpha * shown.value),
                size = size
            )
        }
    }

    private fun count(interaction: Interaction) {
        when (interaction) {
            is PressInteraction.Press -> presses++
            is PressInteraction.Release -> presses--
            is PressInteraction.Cancel -> presses--
            is HoverInteraction.Enter -> hovers++
            is HoverInteraction.Exit -> hovers--
            is FocusInteraction.Focus -> focuses++
            is FocusInteraction.Unfocus -> focuses--
        }
    }

    private fun target(): Float =
        when {
            presses > 0 -> PressedAlpha
            hovers > 0 || focuses > 0 -> HoveredAlpha
            else -> 0f
        }
}
