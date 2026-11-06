package hu.nexio.hud.cheats.ranked

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.nexio.hud.cheats.CheatTab
import hu.nexio.hud.ui.components.GlassCard
import hu.nexio.hud.ui.components.LoadButton
import hu.nexio.hud.ui.components.RgbText
import hu.nexio.hud.ui.components.StatusRow
import hu.nexio.hud.ui.theme.NexioColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object RankedAssistantTab : CheatTab {
    override val id = "ranked_assistant"
    override val title = "RANKED ASSISTANT"

    private fun loadLabel(): String = when (RankedAssistant.status) {
        LoadStatus.IDLE -> "LOAD"
        LoadStatus.LOADING -> "LOADING  ${(RankedAssistant.progress * 100).toInt()}%"
        LoadStatus.LOADED -> "LOADED ✓  (újratöltés)"
        LoadStatus.ERROR -> "HIBA – ÚJRA"
    }

    private fun time(ms: Long) = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(ms))

    @Composable
    override fun Page() {
        val ra = RankedAssistant
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            GlassCard(highlight = ra.status == LoadStatus.LOADED) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    RgbText("RANKED ASSISTANT", fontSize = 22.sp, letterSpacing = 1.sp)
                    Text(
                        "Draft coach valós adatokra építve. Csak információt mutat, a játékot nem módosítja és nem automatizálja.",
                        color = NexioColors.TextSecondary, fontSize = 12.sp,
                    )
                    LoadButton(
                        label = loadLabel(),
                        progress = if (ra.status == LoadStatus.LOADING) ra.progress else if (ra.status == LoadStatus.LOADED) 1f else null,
                        onClick = { ra.load() },
                    )
                    if (ra.status == LoadStatus.ERROR) {
                        Text("Hiba: ${ra.error}", color = NexioColors.Bad, fontSize = 12.sp)
                    }
                    if (ra.status == LoadStatus.LOADED) {
                        Text("Forrás: brawlapi.com  •  lekérve: ${time(ra.fetchedAt)}", color = NexioColors.TextSecondary, fontSize = 11.sp)
                    }
                }
            }

            GlassCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Betöltési lépések", color = NexioColors.TextPrimary, fontSize = 15.sp)
                    ra.steps.forEach { s ->
                        val (txt, col) = when (s.state) {
                            StepState.WAIT -> "–" to NexioColors.TextSecondary
                            StepState.RUN -> "…" to NexioColors.Cyan
                            StepState.OK -> "✓ ${s.detail}" to NexioColors.Ok
                            StepState.FAIL -> "✕ ${s.detail}" to NexioColors.Bad
                        }
                        StatusRow(s.label, txt, col)
                    }
                }
            }

            GlassCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Képességek állapota", color = NexioColors.TextPrimary, fontSize = 15.sp)
                    val loaded = ra.status == LoadStatus.LOADED
                    StatusRow("Brawler / pálya / mód adatbázis", if (loaded) "KÉSZ" else "NINCS BETÖLTVE", if (loaded) NexioColors.Ok else NexioColors.TextSecondary)
                    StatusRow("Aktív események", if (loaded && ra.events.isNotEmpty()) "KÉSZ" else "NINCS BETÖLTVE", if (loaded && ra.events.isNotEmpty()) NexioColors.Ok else NexioColors.TextSecondary)
                    StatusRow("Win rate · pick rate · matchup", "NINCS FORRÁS", NexioColors.Warn)
                    StatusRow("Rang / ELO (Player Tag)", "NINCS FORRÁS", NexioColors.Warn)
                    StatusRow("Draft felismerés (képernyő)", "KÖVETKEZŐ LÉPÉS", NexioColors.Warn)
                    StatusRow("Ban · Pick · Lane ajánlás", "ADATRA VÁR", NexioColors.Warn)
                    Text(
                        "Amíg nincs ellenőrzött statisztikai forrás, az asszisztens nem ad ajánlást: nem találunk ki adatot.",
                        color = NexioColors.TextSecondary, fontSize = 11.sp,
                    )
                }
            }

            if (ra.status == LoadStatus.LOADED && ra.events.isNotEmpty()) {
                GlassCard {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Aktív események (Brawlify)", color = NexioColors.TextPrimary, fontSize = 15.sp)
                        ra.events.take(8).forEach { e ->
                            StatusRow(e.map.ifBlank { "?" }, e.mode.ifBlank { e.slot }, NexioColors.Cyan)
                        }
                    }
                }
            }
        }
    }

    @Composable
    override fun Hud() {
        val ra = RankedAssistant
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("RANKED ASSISTANT", color = NexioColors.Cyan, fontSize = 11.sp)
            when (ra.status) {
                LoadStatus.IDLE, LoadStatus.ERROR -> {
                    LoadButton(label = if (ra.status == LoadStatus.ERROR) "ÚJRA" else "LOAD", progress = null, height = 40.dp, onClick = { ra.load() })
                    ra.error?.let { Text(it, color = NexioColors.Bad, fontSize = 10.sp) }
                }
                LoadStatus.LOADING -> LoadButton(label = "LOADING ${(ra.progress * 100).toInt()}%", progress = ra.progress, height = 40.dp, onClick = {})
                LoadStatus.LOADED -> {
                    Text("ADAT ✓  ${ra.brawlerCount} brawler · ${ra.mapCount} pálya", color = NexioColors.Ok, fontSize = 11.sp)
                    ra.events.take(3).forEach { Text("${it.map} – ${it.mode}", color = NexioColors.TextPrimary, fontSize = 11.sp) }
                    Text("Draft felismerés: nem aktív", color = NexioColors.Warn, fontSize = 10.sp)
                }
            }
        }
    }
}
