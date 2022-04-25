package com.example.intervaltimer

import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.intervaltimer.layout.PickColors

val timeLonger = listOf(30, 60, 90, 120, 150, 180)
val timeShorter = listOf(10, 20, 30)

val getTimeLongerNotification = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakeVideo, onResult = {})
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var showColorPicker by remember {mutableStateOf(false)}
            Column{
                DropDownList(list = timeLonger, "Select the longer interval")
                DropDownList(list = timeShorter, "Select the shorter interval")
                Button({lunchRingtonePicker()}) {
                    Text("Start")
                }
                Icon( painter = painterResource(R.drawable.ic_av_timer_black_24dp),
                    contentDescription = "Change colors",
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { showColorPicker = true })

                if(showColorPicker) {
                    Dialog(onDismissRequest = {showColorPicker = false}) {
                        PickColors()
                    }
                }
            }
        }
    }

    fun lunchRingtonePicker() {
        val intent = Intent()
        intent.action = RingtoneManager.ACTION_RINGTONE_PICKER
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
        startActivityForResult(intent)
    }
}

@Composable
fun DropDownList(list: List<Int>, description: String) {
    var isExpanded by remember {mutableStateOf(false)}
    var selectedTime: Int by remember {mutableStateOf(0)}
    val labelFontSize = 30.sp
    Column{
        Text(description, fontSize = labelFontSize)
        Text(
            text = selectedTime.toString(),
            fontSize = labelFontSize,
            modifier = Modifier.clickable(onClick = { isExpanded = true })
        )
        DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
            list.forEach {
                DropdownMenuItem(onClick = {
                    selectedTime = it
                    isExpanded = false
                }) {
                    Text(it.toString())
                }
            }
        }
    }
}

@Composable
@Preview
fun DropDownListPreview() {
    DropDownList(list = listOf(10, 20, 30, 40, 50), "Test label")
}