package com.example.intervaltimer.layout

import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.example.intervaltimer.DynamicTheme
import androidx.compose.runtime.saveable.Saver

/*
val myColors = lightColors(
    primary = Color(0xFF6200EE),
    primaryVariant = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC6),
    secondaryVariant = Color(0xFF018786),
    background = Color.White,
    surface = Color.White,
    error = Color(0xFFB00020),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White)
*/

class Colors{
    private val BACKGROUND_COLOR_KEY = "background_color"
    private val TEXT_COLOR_KEY = "text_color"
    private val FOREGROUND_COLOR_KEY = "button_color"

    val background = mutableStateOf(Color.Magenta)
    val foreground = mutableStateOf(Color.Red)
    val onForeground = mutableStateOf(Color.Black);
    fun save(preferencesEditor: SharedPreferences.Editor) {
        preferencesEditor.putInt(DynamicTheme.BACKGROUND_COLOR_KEY, m_colors.background.toArgb())
        preferencesEditor.putInt(DynamicTheme.FOREGROUND_COLOR_KEY, m_colors.foreground.toArgb())
        preferencesEditor.putInt(DynamicTheme.TEXT_COLOR_KEY, m_colors.text.toArgb())
    }

    fun load(preferences: SharedPreferences) {
        background.value = Color.(
            preferences.getInt(
                BACKGROUND_COLOR_KEY,
                background.toArgb()
            )
        )
        m_colors.foreground = android.graphics.Color.valueOf(
            preferences.getInt(
                DynamicTheme.FOREGROUND_COLOR_KEY,
                m_colors.foreground.toArgb()
            )
        )
        m_colors.text = android.graphics.Color.valueOf(
            preferences.getInt(
                DynamicTheme.TEXT_COLOR_KEY,
                m_colors.text.toArgb()
            )
        )
    }

}