package com.thecurumo.qaf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PoetAvatar(
    poetId: Int,
    poetName: String,
    modifier: Modifier = Modifier,
    width: androidx.compose.ui.unit.Dp = 78.dp,
    height: androidx.compose.ui.unit.Dp = 102.dp
) {
    val initial = poetName.trim().firstOrNull()?.toString() ?: "؟"

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .background(MaterialTheme.colorScheme.background)
            .border(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.primary
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}