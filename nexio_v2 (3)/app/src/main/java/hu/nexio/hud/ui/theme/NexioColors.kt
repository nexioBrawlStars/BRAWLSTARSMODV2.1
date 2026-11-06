package hu.nexio.hud.ui.theme

import androidx.compose.ui.graphics.Color

/** NEXIO színpaletta: sötét alap, lila/kék/ciánkék neon akcentusok. */
object NexioColors {
    val Background = Color(0xFF07060D)
    val Panel = Color(0xFF110F1E)
    val Surface = Color(0xCC12101F)
    val SurfaceStroke = Color(0x449B5CFF)

    val Purple = Color(0xFF9B5CFF)
    val Blue = Color(0xFF4D7CFF)
    val Cyan = Color(0xFF3FD0FF)
    val Pink = Color(0xFFFF5CD0)

    val Ok = Color(0xFF4DFF9B)
    val Warn = Color(0xFFFFC04D)
    val Bad = Color(0xFFFF4D6D)

    val TextPrimary = Color(0xFFEDE9FF)
    val TextSecondary = Color(0xFF8C86A8)

    /** Az RGB betűstílus színei. */
    val RgbCycle = listOf(Purple, Blue, Cyan, Blue, Purple, Pink, Purple)
}
