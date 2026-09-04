package com.thecurumo.qaf.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val QafLightColors: ColorScheme = lightColorScheme(
    background = VellumLight,
    surface = ChalkLight,
    onBackground = InkLight,
    onSurface = InkLight,
    primary = GoldLight,
    onPrimary = VellumLight,
    secondary = StoneLight,
    onSecondary = InkLight
)

private val QafDarkColors: ColorScheme = darkColorScheme(
    background = VellumDark,
    surface = ChalkDark,
    onBackground = InkDark,
    onSurface = InkDark,
    primary = GoldDark,
    onPrimary = VellumDark,
    secondary = StoneDark,
    onSecondary = InkDark
)

@Composable
fun QafTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) QafDarkColors else QafLightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = QafTypography,
        content = content
    )
}