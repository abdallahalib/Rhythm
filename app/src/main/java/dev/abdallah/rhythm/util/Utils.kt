package dev.abdallah.rhythm.util

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import kotlin.time.times

fun millisecondsToMinutes(milliseconds: Long): String {
    val seconds = milliseconds / 1000
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "${minutes}:${remainingSeconds.toString().padStart(2, '0')}"
}

@Stable
fun Modifier.mirror(): Modifier = composed {
    if (LocalLayoutDirection.current == LayoutDirection.Rtl) {
        this.scale(scaleX = -1f, scaleY = 1f)
    } else {
        this
    }
}

fun getScreenWidthInPx(): Int {
    val displayMetrics = Resources.getSystem().displayMetrics
    return displayMetrics.widthPixels
}

fun Float.toPx(context: Context): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)
}

fun minutesToMilliseconds(minutes: Int): Long {
    return minutes.times(60).times(1000).toLong()
}
