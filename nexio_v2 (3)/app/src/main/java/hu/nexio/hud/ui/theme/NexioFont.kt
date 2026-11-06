package hu.nexio.hud.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily

/**
 * Lilita One betűtípus. A fájlt (lilita_one.ttf) az app/src/main/res/font/ mappába kell tenni.
 * Ha a fájl nincs ott, az app a rendszer alapbetűjével fut tovább (nem fordítási hiba).
 */
@Composable
fun rememberNexioFont(): FontFamily {
    val ctx = LocalContext.current
    return remember {
        val id = ctx.resources.getIdentifier("lilita_one", "font", ctx.packageName)
        if (id != 0) FontFamily(Font(id)) else FontFamily.Default
    }
}
