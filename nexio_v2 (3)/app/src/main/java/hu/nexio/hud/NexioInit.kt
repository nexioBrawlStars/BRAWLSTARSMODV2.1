package hu.nexio.hud

import android.content.Context

/** Egyetlen belépési pont: az Activity és az overlay service is ezt hívja. */
object NexioInit {
    fun init(context: Context) {
        HudSettings.init(context.applicationContext)
    }
}
