package molokoka.project.n.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import org.jetbrains.compose.resources.Font
import projectn.composeapp.generated.resources.Res
import projectn.composeapp.generated.resources.byte_bounce
import projectn.composeapp.generated.resources.karmatic_arcade

@Composable
fun karmaticArcade() = FontFamily(Font(Res.font.karmatic_arcade))

@Composable
fun byteBounce() = FontFamily(Font(Res.font.byte_bounce))
