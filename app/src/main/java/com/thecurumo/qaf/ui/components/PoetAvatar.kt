package com.thecurumo.qaf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * نمایش آواتار یک شاعر.
 * فعلاً فقط حالت "حرف اول اسم" پیاده‌سازی شده.
 * در آینده وقتی عکس واقعی شاعران آماده شد، کافیست منطق
 * چک‌کردن وجود عکس (مثلاً در assets/poet_images/{poetId}.webp)
 * را در همینجا اضافه کنیم؛ استفاده‌کنندگان این Composable
 * نیازی به تغییر ندارند.
 */
@Composable
fun PoetAvatar(
    poetId: Int,
    poetName: String,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 72.dp
) {
    // TODO: در آینده اینجا چک شود که آیا عکس واقعی برای poetId موجود است.
    // اگر موجود بود، با Coil/AsyncImage نمایش داده شود.
    // اگر موجود نبود، همین حالت آواتار حرفی به عنوان fallback باقی می‌ماند.

    val initial = poetName.trim().firstOrNull()?.toString() ?: "؟"

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFF4F2EB)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            fontSize = (size.value / 2.2).sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFBFA054)
        )
    }
}