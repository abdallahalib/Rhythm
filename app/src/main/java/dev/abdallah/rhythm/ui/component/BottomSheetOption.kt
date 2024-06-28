package dev.abdallah.rhythm.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomSheetOption(
    text: String,
    icon: Int? = null,
    onClick: () -> Unit,
    textColor: Color = Color.White,
    iconColor: Color = Color.White,
) {
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp)) {
        icon?.let {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = it),
                contentDescription = "contentDescription",
                tint = iconColor,
            )
        }
        Text(
            text = text,
            modifier = Modifier
                .weight(1f, false)
                .padding(start = 8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.W500,
        )
    }
}