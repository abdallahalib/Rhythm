package dev.abdallah.rhythm.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.abdallah.rhythm.ui.theme.Blue
import dev.abdallah.rhythm.ui.theme.LightGray
import dev.abdallah.rhythm.ui.theme.OnPrimary
import dev.abdallah.rhythm.ui.theme.Surface

@Composable
fun Category(
    page: Int, onClick: () -> Unit, targetPage: Int, categories: List<String>
) {
    val paddingStart = if (page == 0) 16.dp else 4.dp
    val paddingEnd = if (page == categories.size - 1) 16.dp else 4.dp
    val backgroundColor by animateColorAsState(
        targetValue = if (targetPage == page) Blue else Surface,
        animationSpec = tween(durationMillis = 250, easing = LinearEasing),
        label = "Background color"
    )
    val textColor by animateColorAsState(
        targetValue = if (targetPage == page) OnPrimary else LightGray,
        animationSpec = tween(durationMillis = 250, easing = LinearEasing),
        label = "Text color"
    )
    Text(
        text = categories[page],
        modifier = Modifier
            .padding(start = paddingStart, end = paddingEnd)
            .background(
                color = backgroundColor, shape = RoundedCornerShape(corner = CornerSize(24.dp))
            )
            .padding(8.dp)
            .clickable {
                onClick()
            },
        color = textColor,
        fontSize = 14.sp,
    )
}