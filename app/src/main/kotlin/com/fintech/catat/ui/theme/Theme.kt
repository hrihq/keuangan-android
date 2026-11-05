package com.fintech.catat.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val PrimaryBlue = Color(0xFF0B57D0)
private val SecondaryYellow = Color(0xFFFFE08A)
private val TertiaryLightBlue = Color(0xFFC2DAFF)
private val TertiaryTeal = Color(0xFFC4EED0)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryYellow,
    tertiary = TertiaryTeal,
    background = Color(0xFFFAFAFA),
    surface = Color.White,
)

object CatatTheme {
    val colorScheme
        @Composable
        get() = LightColorScheme
}

@Composable
fun CatatKeuanganTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
