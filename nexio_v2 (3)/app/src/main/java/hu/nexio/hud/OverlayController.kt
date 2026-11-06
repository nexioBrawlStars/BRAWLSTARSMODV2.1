package hu.nexio.hud

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.content.ContextCompat

/** A HUD service indítása/leállítása. */
object OverlayController {
    fun canDraw(ctx: Context) = Settings.canDrawOverlays(ctx)

    fun start(ctx: Context) {
        ContextCompat.startForegroundService(ctx, Intent(ctx, OverlayService::class.java))
    }

    fun stop(ctx: Context) {
        ctx.stopService(Intent(ctx, OverlayService::class.java))
    }
}
