package hu.nexio.hud.cheats.ranked

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import hu.nexio.hud.data.BrawlifyClient
import hu.nexio.hud.data.BrawlifyClient.items
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

enum class StepState { WAIT, RUN, OK, FAIL }
enum class LoadStatus { IDLE, LOADING, LOADED, ERROR }

class LoadStep(val label: String) {
    var state by mutableStateOf(StepState.WAIT)
    var detail by mutableStateOf("")
}

data class ActiveEvent(val slot: String, val map: String, val mode: String)

/**
 * A Ranked Assistant adatállapota. A "betöltés" valódi hálózati lekérés: nem szimulált folyamat.
 * Phase 1: játékadatbázis (brawlerek, pályák, módok, aktív események).
 * Statisztikai adat (win rate, matchup) és draftfelismerés még nincs bekötve – ezeket a felület is jelzi.
 */
object RankedAssistant {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    var status by mutableStateOf(LoadStatus.IDLE); private set
    var error by mutableStateOf<String?>(null); private set
    var brawlerCount by mutableStateOf(0); private set
    var mapCount by mutableStateOf(0); private set
    var modeCount by mutableStateOf(0); private set
    var events by mutableStateOf<List<ActiveEvent>>(emptyList()); private set
    var fetchedAt by mutableStateOf(0L); private set

    val steps = listOf(LoadStep("Brawlerek"), LoadStep("Pályák"), LoadStep("Játékmódok"), LoadStep("Aktív események"))

    val progress: Float
        get() = steps.count { it.state == StepState.OK } / steps.size.toFloat()

    fun load() {
        if (status == LoadStatus.LOADING) return
        status = LoadStatus.LOADING
        error = null
        steps.forEach { it.state = StepState.WAIT; it.detail = "" }
        scope.launch {
            try {
                run(steps[0]) {
                    val n = BrawlifyClient.get("brawlers").items().length()
                    brawlerCount = n
                    "$n db"
                }
                run(steps[1]) {
                    val n = BrawlifyClient.get("maps").items().length()
                    mapCount = n
                    "$n db"
                }
                run(steps[2]) {
                    val n = BrawlifyClient.get("gamemodes").items().length()
                    modeCount = n
                    "$n db"
                }
                run(steps[3]) {
                    val arr = BrawlifyClient.get("events").items("active")
                    val list = ArrayList<ActiveEvent>()
                    for (i in 0 until arr.length()) {
                        val e = arr.optJSONObject(i) ?: continue
                        val map = e.optJSONObject("map")
                        list += ActiveEvent(
                            slot = e.optJSONObject("slot")?.optString("name").orEmpty(),
                            map = map?.optString("name").orEmpty(),
                            mode = map?.optJSONObject("gameMode")?.optString("name").orEmpty(),
                        )
                    }
                    events = list
                    "${list.size} db"
                }
                fetchedAt = System.currentTimeMillis()
                status = LoadStatus.LOADED
            } catch (e: Exception) {
                error = e.message ?: e.javaClass.simpleName
                status = LoadStatus.ERROR
            }
        }
    }

    private suspend fun run(step: LoadStep, block: suspend () -> String) {
        step.state = StepState.RUN
        try {
            step.detail = block()
            step.state = StepState.OK
        } catch (e: Exception) {
            step.state = StepState.FAIL
            step.detail = e.message ?: "hiba"
            throw e
        }
    }
}
