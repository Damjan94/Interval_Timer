package com.example.intervaltimer.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import com.godaddy.android.colorpicker.harmony.ColorHarmonyMode
import com.godaddy.android.colorpicker.harmony.HarmonyColorPicker

class ColorPicker (val colorPicked: (Color) -> Unit) {
    var showColorPicker = mutableStateOf(false)
}

@Composable
fun DisplayColorPicker(colorPicker: ColorPicker) {
    //val showColorPicker:Boolean = remember(colorPicker.showColorPicker.value) {colorPicker.showColorPicker.value}
    if(colorPicker.showColorPicker.value) {
        Dialog(onDismissRequest = {colorPicker.showColorPicker.value = false}) {
            HarmonyColorPicker(harmonyMode = ColorHarmonyMode.NONE, onColorChanged = {
                colorPicker.colorPicked(it.toColor())})
        }
    }
}
