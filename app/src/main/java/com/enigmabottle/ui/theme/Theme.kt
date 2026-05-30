package com.enigmabottle.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme =
  darkColorScheme(
      primary = SleekDarkPrimary,
      onPrimary = SleekOnPrimary,
      background = SleekDarkBg,
      onBackground = SleekDarkOnBg,
      surface = SleekDarkSurface,
      onSurface = SleekDarkOnSurface,
      surfaceVariant = SleekDarkSurfaceVariant,
      onSurfaceVariant = SleekDarkOnSurface,
      outline = SleekDarkOutline
  )

private val LightColorScheme =
  lightColorScheme(
      primary = SleekPrimary,
      onPrimary = SleekOnPrimary,
      background = SleekBg,
      onBackground = SleekOnBg,
      surface = SleekSurface,
      onSurface = SleekOnSurface,
      surfaceVariant = SleekSurfaceVariant,
      onSurfaceVariant = SleekOnSurfaceVariant,
      outline = SleekOutline
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Set to false to strictly enforce the "Sleek Interface" design layout colors!
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
