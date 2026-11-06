package hu.nexio.hud.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.nexio.hud.ui.theme.NexioColors

/** Áttetsző, finom keretes kártya. */
@Composable
fun GlassCard(modifier: Modifier = Modifier, highlight: Boolean = false, content: @Composable () -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NexioColors.Surface),
        border = BorderStroke(1.dp, if (highlight) NexioColors.Purple else NexioColors.SurfaceStroke),
    ) { content() }
}

/** "Címke ........ ÁLLAPOT" sor színes állapotjelzővel. */
@Composable
fun StatusRow(label: String, status: String, color: Color) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = NexioColors.TextPrimary, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text(status, color = color, fontSize = 12.sp)
    }
}

/**
 * LOAD gomb: töltés közben a háttérben animáltan kitöltődik (progress 0..1),
 * különben sima gomb. Nagy érintési felület telefonra.
 */
@Composable
fun LoadButton(
    label: String,
    progress: Float?,
    modifier: Modifier = Modifier,
    height: Dp = 56.dp,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    val p by animateFloatAsState(progress ?: 0f, label = "loadProgress")
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(Color(0xFF1A1730))
            .border(1.dp, NexioColors.Purple.copy(alpha = 0.7f), shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (progress != null) {
            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxHeight()
                    .fillMaxWidth(p.coerceIn(0f, 1f))
                    .background(Brush.horizontalGradient(listOf(NexioColors.Purple.copy(alpha = 0.6f), NexioColors.Blue.copy(alpha = 0.6f)))),
            )
        }
        Text(label, color = NexioColors.TextPrimary, fontSize = 18.sp)
    }
}
