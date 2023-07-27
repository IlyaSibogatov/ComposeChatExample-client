package com.example.composechatexample.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import com.example.composechatexample.utils.TypeTheme

private val DarkColorScheme = darkColorScheme(
    primary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimary = DarkOnPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondary = DarkOnSecondary,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiary = DarkOnTertiary,
    onTertiaryContainer = DarkOnTertiaryContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkNeutral,
    onSurface = DarkOnBackground,
    outline = DarkSurfaceVariant,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    onError = DarkOnError,
    error = DarkError
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    primaryContainer = PrimaryContainer,
    onPrimary = OnPrimary,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    secondaryContainer = SecondaryContainer,
    onSecondary = OnSecondary,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiary = OnTertiary,
    onTertiaryContainer = OnTertiaryContainer,
    background = Background,
    onBackground = OnBackground,
    surface = Background,
    onSurface = OnBackground,
    outline = SurfaceVariant,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    onError = OnError,
    error = Error
)

@Composable
fun ComposeChatExampleTheme(
    themeUser: String,
    content: @Composable () -> Unit
) {
    val colorScheme = when(themeUser) {
        TypeTheme.DARK.name -> DarkColorScheme
        TypeTheme.LIGHT.name -> LightColorScheme
        else -> if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = LightTypography,
        content = content
    )
}