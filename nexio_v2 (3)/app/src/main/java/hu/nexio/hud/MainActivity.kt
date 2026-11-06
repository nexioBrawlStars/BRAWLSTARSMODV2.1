package hu.nexio.hud

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import hu.nexio.hud.ui.screens.MainScreen
import hu.nexio.hud.ui.theme.NexioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NexioInit.init(this)

        // Android 13+: értesítési engedély (a háttérben futó HUD értesítéséhez).
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)

        // Ha a HUD be volt kapcsolva (és az engedély megvan), induláskor visszaáll.
        if (HudSettings.enabled && OverlayController.canDraw(this)) OverlayController.start(this)
        else if (HudSettings.enabled) HudSettings.setEnabled(this, false)

        setContent { NexioTheme { MainScreen() } }
    }
}
