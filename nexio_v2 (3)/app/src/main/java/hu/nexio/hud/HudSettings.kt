package hu.nexio.hud

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** A HUD-panel beállításai (be/ki, átlátszóság, méret, összecsukás, pozíció). Mentve SharedPreferences-be. */
object HudSettings {
    private const val PREFS = "nexio_hud"
    private var initialized = false

    var enabled by mutableStateOf(false); private set
    var opacity by mutableFloatStateOf(0.92f); private set   // 0.3 .. 1.0
    var scale by mutableFloatStateOf(1.0f); private set      // 0.6 .. 1.6
    var collapsed by mutableStateOf(false); private set

    /** Pozíció pixelben; -1 = még nincs beállítva (a service számol alapértéket). */
    var x: Int = -1; private set
    var y: Int = -1; private set

    private fun p(c: Context) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun init(c: Context) {
        if (initialized) return
        initialized = true
        val s = p(c)
        enabled = s.getBoolean("enabled", false)
        opacity = s.getFloat("opacity", 0.92f)
        scale = s.getFloat("scale", 1.0f)
        collapsed = s.getBoolean("collapsed", false)
        x = s.getInt("x", -1)
        y = s.getInt("y", -1)
    }

    fun setEnabled(c: Context, v: Boolean) { enabled = v; p(c).edit().putBoolean("enabled", v).apply() }
    fun setOpacity(c: Context, v: Float) { opacity = v; p(c).edit().putFloat("opacity", v).apply() }
    fun setScale(c: Context, v: Float) { scale = v; p(c).edit().putFloat("scale", v).apply() }
    fun setCollapsed(c: Context, v: Boolean) { collapsed = v; p(c).edit().putBoolean("collapsed", v).apply() }
    fun setPosition(c: Context, nx: Int, ny: Int) {
        x = nx; y = ny
        p(c).edit().putInt("x", nx).putInt("y", ny).apply()
    }
    fun resetPosition(c: Context) {
        x = -1; y = -1
        p(c).edit().remove("x").remove("y").apply()
    }
}
