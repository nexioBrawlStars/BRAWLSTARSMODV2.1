package hu.nexio.hud

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import hu.nexio.hud.overlay.OverlayLifecycleOwner
import hu.nexio.hud.ui.components.HudPanel
import hu.nexio.hud.ui.theme.NexioTheme
import kotlin.math.roundToInt

/**
 * Foreground service: a lebegő HUD-panelt a WindowManager-en át a többi alkalmazás fölé teszi.
 * A játékot nem érinti, nem olvas belőle és nem ír bele.
 */
class OverlayService : Service() {

    private lateinit var wm: WindowManager
    private var composeView: ComposeView? = null
    private var params: WindowManager.LayoutParams? = null
    private val owner = OverlayLifecycleOwner()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        NexioInit.init(this)
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIF_ID, buildNotification())
        if (!Settings.canDrawOverlays(this)) {
            HudSettings.setEnabled(this, false)
            stopSelf()
            return START_NOT_STICKY
        }
        if (composeView == null) showOverlay()
        return START_NOT_STICKY
    }

    private fun showOverlay() {
        val dm = resources.displayMetrics
        val lp = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            // Alapérték: felső-közép (a játék bal felső és jobb felső elemei szabadon maradnak).
            x = if (HudSettings.x >= 0) HudSettings.x.coerceAtMost(dm.widthPixels - 60) else (dm.widthPixels * 0.35f).roundToInt()
            y = if (HudSettings.y >= 0) HudSettings.y.coerceAtMost(dm.heightPixels - 60) else (8 * dm.density).roundToInt()
        }
        params = lp

        val view = ComposeView(this).apply {
            setViewTreeLifecycleOwner(owner)
            setViewTreeSavedStateRegistryOwner(owner)
            setContent {
                NexioTheme {
                    HudPanel(
                        onDragBy = { dx, dy -> moveBy(dx, dy) },
                        onDragEnd = { params?.let { HudSettings.setPosition(this@OverlayService, it.x, it.y) } },
                        onClose = {
                            HudSettings.setEnabled(this@OverlayService, false)
                            stopSelf()
                        },
                    )
                }
            }
        }
        owner.onCreate()
        owner.onResume()
        composeView = view
        wm.addView(view, lp)
    }

    private fun moveBy(dx: Float, dy: Float) {
        val lp = params ?: return
        val view = composeView ?: return
        val dm = resources.displayMetrics
        lp.x = (lp.x + dx.roundToInt()).coerceIn(0, (dm.widthPixels - 60).coerceAtLeast(0))
        lp.y = (lp.y + dy.roundToInt()).coerceIn(0, (dm.heightPixels - 60).coerceAtLeast(0))
        wm.updateViewLayout(view, lp)
    }

    override fun onDestroy() {
        composeView?.let {
            it.disposeComposition()
            runCatching { wm.removeView(it) }
        }
        composeView = null
        owner.onDestroy()
        super.onDestroy()
    }

    private fun buildNotification(): Notification {
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(NotificationChannel(CHANNEL, "NEXIO HUD", NotificationManager.IMPORTANCE_LOW))
        val pi = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, CHANNEL)
            .setSmallIcon(R.drawable.ic_nexio)
            .setContentTitle("NEXIO HUD")
            .setContentText("A HUD fut a háttérben")
            .setContentIntent(pi)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val CHANNEL = "nexio_hud"
        private const val NOTIF_ID = 1001
    }
}
