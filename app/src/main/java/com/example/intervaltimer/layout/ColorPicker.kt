package com.example.intervaltimer.layout

import androidx.compose.runtime.Composable
import com.godaddy.android.colorpicker.harmony.ColorHarmonyMode
import com.godaddy.android.colorpicker.harmony.HarmonyColorPicker

@Composable
fun PickColors() {
    HarmonyColorPicker(harmonyMode = ColorHarmonyMode.NONE, onColorChanged = { it.toColor()})
}