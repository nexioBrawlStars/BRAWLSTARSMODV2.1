package hu.nexio.hud.cheats

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import hu.nexio.hud.cheats.ranked.RankedAssistantTab

/**
 * Egy lapozható fül a panelen. Új fül hozzáadása: implementáld ezt, majd vedd fel a CheatRegistry.tabs listába.
 * Page() = a teljes képernyős tartalom az appban, Hud() = a lebegő panelen megjelenő kompakt változat.
 */
interface CheatTab {
    val id: String
    val title: String

    @Composable
    fun Page()

    @Composable
    fun Hud()
}

object CheatRegistry {
    /** Egyelőre csak a Ranked Assistant. */
    val tabs: List<CheatTab> = listOf(RankedAssistantTab)
    var selected by mutableIntStateOf(0)
}
