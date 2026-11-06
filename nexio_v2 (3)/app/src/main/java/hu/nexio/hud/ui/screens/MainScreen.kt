package hu.nexio.hud.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import hu.nexio.hud.HudSettings
import hu.nexio.hud.OverlayController
import hu.nexio.hud.cheats.CheatRegistry
import hu.nexio.hud.ui.components.LoadButton
import hu.nexio.hud.ui.components.RgbText
import hu.nexio.hud.ui.theme.NexioColors
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val BRAWL_STARS_PACKAGE = "com.supercell.brawlstars"

/**
 * Telefonra optimalizált cheat panel: RGB fejléc, lapozható fülek (húzással vagy koppintással),
 * alul fix dokk: HUD kapcsoló, játékindítás, beállítások.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen() {
    val ctx = LocalContext.current
    val activity = ctx as ComponentActivity
    val scope = rememberCoroutineScope()

    var canDraw by remember { mutableStateOf(OverlayController.canDraw(ctx)) }
    DisposableEffect(activity) {
        val obs = LifecycleEventObserver { _, e -> if (e == Lifecycle.Event.ON_RESUME) canDraw = OverlayController.canDraw(ctx) }
        activity.lifecycle.addObserver(obs)
        onDispose { activity.lifecycle.removeObserver(obs) }
    }

    val tabs = CheatRegistry.tabs
    val pager = rememberPagerState(initialPage = CheatRegistry.selected) { tabs.size }
    LaunchedEffect(pager.currentPage) { CheatRegistry.selected = pager.currentPage }

    var showSettings by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(NexioColors.Background, NexioColors.Panel))),
    ) {
        // --- Fejléc ---
        Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp)) {
            RgbText("NEXIO", fontSize = 40.sp, letterSpacing = 4.sp)
            Text("CHEAT PANEL  •  v2.1", color = NexioColors.TextSecondary, fontSize = 11.sp, letterSpacing = 2.sp)
        }

        // --- Fülek ---
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            tabs.forEachIndexed { i, t ->
                val sel = pager.currentPage == i
                val shape = RoundedCornerShape(12.dp)
                Box(
                    Modifier
                        .clip(shape)
                        .background(if (sel) NexioColors.Purple.copy(alpha = 0.25f) else NexioColors.Surface)
                        .border(1.dp, if (sel) NexioColors.Purple else NexioColors.SurfaceStroke, shape)
                        .clickable { scope.launch { pager.animateScrollToPage(i) } }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    Text(t.title, color = if (sel) NexioColors.TextPrimary else NexioColors.TextSecondary, fontSize = 14.sp)
                }
            }
        }

        // --- Engedély-figyelmeztetés ---
        if (!canDraw) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NexioColors.Warn.copy(alpha = 0.15f))
                    .border(1.dp, NexioColors.Warn.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .clickable { openOverlaySettings(ctx) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("A HUD-hoz engedély kell: koppints ide", color = NexioColors.Warn, fontSize = 13.sp)
            }
        }

        // --- Lapozható tartalom ---
        HorizontalPager(state = pager, modifier = Modifier.weight(1f).fillMaxWidth()) { page ->
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                tabs[page].Page()
            }
        }

        // --- Alsó dokk ---
        Row(
            Modifier
                .fillMaxWidth()
                .background(NexioColors.Panel)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, if (HudSettings.enabled) NexioColors.Purple else NexioColors.SurfaceStroke, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("HUD", color = NexioColors.TextPrimary, fontSize = 16.sp)
                Switch(
                    checked = HudSettings.enabled,
                    onCheckedChange = { on ->
                        if (on) {
                            if (!canDraw) openOverlaySettings(ctx)
                            else { HudSettings.setEnabled(ctx, true); OverlayController.start(ctx) }
                        } else {
                            HudSettings.setEnabled(ctx, false); OverlayController.stop(ctx)
                        }
                    },
                    colors = SwitchDefaults.colors(checkedTrackColor = NexioColors.Purple),
                )
            }
            LoadButton(
                label = "▶ BRAWL STARS",
                progress = null,
                modifier = Modifier.weight(1.3f),
                height = 52.dp,
                onClick = {
                    val i = ctx.packageManager.getLaunchIntentForPackage(BRAWL_STARS_PACKAGE)
                    if (i != null) ctx.startActivity(i)
                    else Toast.makeText(ctx, "A Brawl Stars nincs telepítve.", Toast.LENGTH_LONG).show()
                },
            )
            Box(
                Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, NexioColors.SurfaceStroke, RoundedCornerShape(14.dp))
                    .clickable { showSettings = true },
                contentAlignment = Alignment.Center,
            ) { Text("⚙", color = NexioColors.TextPrimary, fontSize = 22.sp) }
        }
    }

    if (showSettings) SettingsDialog(canDraw = canDraw, onDismiss = { showSettings = false })
}

@Composable
private fun SettingsDialog(canDraw: Boolean, onDismiss: () -> Unit) {
    val ctx = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NexioColors.Panel,
        title = { RgbText("BEÁLLÍTÁSOK", fontSize = 22.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    if (canDraw) "Megjelenítés más alkalmazások fölött: engedélyezve ✓" else "Megjelenítés más alkalmazások fölött: kikapcsolva",
                    color = if (canDraw) NexioColors.Ok else NexioColors.Warn, fontSize = 12.sp,
                )
                if (!canDraw) TextButton(onClick = { openOverlaySettings(ctx) }) { Text("Engedély megadása") }

                Text("Átlátszóság: ${(HudSettings.opacity * 100).roundToInt()}%", color = NexioColors.TextSecondary, fontSize = 13.sp)
                Slider(value = HudSettings.opacity, onValueChange = { HudSettings.setOpacity(ctx, it) }, valueRange = 0.3f..1f)
                Text("Méret: ${(HudSettings.scale * 100).roundToInt()}%", color = NexioColors.TextSecondary, fontSize = 13.sp)
                Slider(value = HudSettings.scale, onValueChange = { HudSettings.setScale(ctx, it) }, valueRange = 0.6f..1.6f)
                Text("A HUD-ot a fejlécében lévő ⠿ fogóval húzhatod, a ▾ gombbal összecsukhatod.", color = NexioColors.TextSecondary, fontSize = 11.sp)
                TextButton(onClick = {
                    HudSettings.resetPosition(ctx)
                    if (HudSettings.enabled) { OverlayController.stop(ctx); OverlayController.start(ctx) }
                }) { Text("Pozíció visszaállítása") }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Bezár") } },
    )
}

private fun openOverlaySettings(ctx: Context) {
    ctx.startActivity(
        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${ctx.packageName}"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
    )
}
