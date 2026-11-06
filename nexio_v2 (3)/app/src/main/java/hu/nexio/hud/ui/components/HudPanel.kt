package hu.nexio.hud.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.nexio.hud.HudSettings
import hu.nexio.hud.cheats.CheatRegistry
import hu.nexio.hud.ui.theme.NexioColors

/** A lebegő HUD-panel: fejléc (RGB NEXIO, összecsukás, húzófogó, bezárás) + a kiválasztott fül kompakt tartalma. */
@Composable
fun HudPanel(
    onDragBy: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
    onClose: () -> Unit,
) {
    val ctx = LocalContext.current
    val base = LocalDensity.current
    CompositionLocalProvider(LocalDensity provides Density(base.density * HudSettings.scale, base.fontScale)) {
        Surface(
            modifier = Modifier.alpha(HudSettings.opacity),
            shape = RoundedCornerShape(16.dp),
            color = NexioColors.Surface,
            border = BorderStroke(1.dp, NexioColors.SurfaceStroke),
        ) {
            Column(
                Modifier.padding(horizontal = 10.dp, vertical = 6.dp).widthIn(min = 170.dp, max = 260.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                var dragStart by remember { mutableStateOf(Offset.Zero) }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    RgbText("NEXIO", fontSize = 16.sp, letterSpacing = 2.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        HeaderIcon(if (HudSettings.collapsed) "▴" else "▾") { HudSettings.setCollapsed(ctx, !HudSettings.collapsed) }
                        Box(
                            Modifier.size(36.dp).pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { dragStart = it },
                                    onDragEnd = onDragEnd,
                                    onDragCancel = onDragEnd,
                                ) { change, _ ->
                                    change.consume()
                                    onDragBy(change.position.x - dragStart.x, change.position.y - dragStart.y)
                                }
                            },
                            contentAlignment = Alignment.Center,
                        ) { Text("⠿", color = NexioColors.TextSecondary, fontSize = 18.sp) }
                        HeaderIcon("✕", onClick = onClose)
                    }
                }
                if (!HudSettings.collapsed) {
                    CheatRegistry.tabs.getOrNull(CheatRegistry.selected)?.Hud()
                }
            }
        }
    }
}

@Composable
private fun HeaderIcon(glyph: String, onClick: () -> Unit) {
    Box(Modifier.size(36.dp).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(glyph, color = NexioColors.TextSecondary, fontSize = 16.sp)
    }
}
