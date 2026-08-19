package com.kitutu.matokeo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Green900,
    onPrimary = Color.White,
    secondary = Gold,
    secondaryContainer = Green050,
    background = Cream,
    surface = Color.White,
    error = Brick
)

@Composable
fun MatokeoKitutuTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}
