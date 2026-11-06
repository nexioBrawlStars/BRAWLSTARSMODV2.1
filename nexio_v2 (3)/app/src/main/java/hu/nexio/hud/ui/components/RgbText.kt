package hu.nexio.hud.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import hu.nexio.hud.ui.theme.NexioColors
import hu.nexio.hud.ui.theme.rememberNexioFont

/** Lilita One betűs, lassan végigfutó RGB színátmenetű szöveg finom fénnyel. */
@Composable
fun RgbText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 28.sp,
    letterSpacing: TextUnit = 2.sp,
    durationMs: Int = 4500,
) {
    val span = 600f
    val t = rememberInfiniteTransition(label = "rgbText")
    val shift by t.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMs, easing = LinearEasing), RepeatMode.Restart),
        label = "rgbShift",
    )
    val brush = Brush.linearGradient(
        colors = NexioColors.RgbCycle,
        start = Offset(shift * span * 2, 0f),
        end = Offset(shift * span * 2 + span, 0f),
        tileMode = TileMode.Mirror,
    )
    Text(
        text = text,
        modifier = modifier,
        style = TextStyle(
            brush = brush,
            fontSize = fontSize,
            fontWeight = FontWeight.Normal, // a Lilita One egyvastagságú, nem kell szintetikus bold
            letterSpacing = letterSpacing,
            fontFamily = rememberNexioFont(),
            shadow = Shadow(color = NexioColors.Purple.copy(alpha = 0.55f), offset = Offset.Zero, blurRadius = 18f),
        ),
    )
}
