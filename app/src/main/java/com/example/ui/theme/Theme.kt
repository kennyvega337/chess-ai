package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color
import com.example.chess.model.ChessTheme

private val DarkColorScheme =
  darkColorScheme(
    primary = ChessAmberPrimary,
    secondary = ChessAmberSecondary,
    tertiary = TextGold,
    background = ChessDarkBg,
    surface = ChessSurfaceDark,
    surfaceVariant = ChessSurfaceVariant,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = TextLight,
    onSurface = TextLight
  )

private val LightColorScheme = DarkColorScheme

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  selectedTheme: ChessTheme = ChessTheme.CLASSIC,
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme.copy(
      primary = Color(selectedTheme.buttonColor),
      onPrimary = Color(selectedTheme.onAccentColor),
      secondary = Color(selectedTheme.accentColor),
      onSecondary = Color(selectedTheme.onAccentColor),
      tertiary = Color(selectedTheme.accentColor),
      onTertiary = Color(selectedTheme.onAccentColor),
      background = Color(selectedTheme.backgroundColors[0]),
      onBackground = Color(selectedTheme.textColor),
      surface = Color(selectedTheme.surfaceColor),
      onSurface = Color(selectedTheme.textColor),
      surfaceVariant = Color(selectedTheme.surfaceColor).copy(alpha = 0.8f),
      onSurfaceVariant = Color(selectedTheme.secondaryTextColor),
      outline = Color(selectedTheme.borderColor),
      outlineVariant = Color(selectedTheme.dividerColor)
  )

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
