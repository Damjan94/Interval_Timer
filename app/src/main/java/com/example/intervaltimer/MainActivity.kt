package com.example.intervaltimer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import com.example.intervaltimer.layout.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController

val timeLonger = listOf(30, 60, 90, 120, 150, 180)
val timeShorter = listOf(10, 20, 30)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val colors = Colors()

        setContent {
            val systemUiController = rememberSystemUiController()
            val ringtonePicker = remember {
                RingtonePicker {
                    Log.e("Ringtone Picker", it.toString())
                }
            }
            val colorPicker = remember {
                ColorPicker {
                    colors.background.value = it
                    systemUiController.setSystemBarsColor(it, it.luminance() > 0.5f)
                }
            }
            Column(Modifier.background(colors.background.value)){
                    DropDownList(list = timeLonger, "Select the longer interval")
                    DropDownList(list = timeShorter, "Select the shorter interval")
                    Button({ringtonePicker.showPickerMenu.value = true}) {
                        Text("Start")
                    }
                    Icon( painter = painterResource(R.drawable.ic_av_timer_black_24dp),
                        contentDescription = "Change colors",
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { colorPicker.showColorPicker.value = true })
                    DisplayColorPicker(colorPicker)
                    DisplayRingtonePicker(ringtonePicker)
            }
        }
    }

}