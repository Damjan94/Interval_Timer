package com.example.intervaltimer.layout

import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*

class RingtonePicker(val ringtonePicked: (Uri) -> Unit) {
    val showPickerMenu = mutableStateOf(false)
}
@Composable
fun DisplayRingtonePicker(ringtonePicker: RingtonePicker) {
    val getTimeLongerNotification = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            ringtonePicker.showPickerMenu.value = false
            val data = it.data
            if(data == null) {
                return@rememberLauncherForActivityResult
            }
            val ringtoneUri = data.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            if(ringtoneUri == null) {
                return@rememberLauncherForActivityResult
            }
            ringtonePicker.ringtonePicked(ringtoneUri)
        })

    if(ringtonePicker.showPickerMenu.value) {
        lunchRingtonePicker(getTimeLongerNotification)
    }
}

private fun lunchRingtonePicker(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
    val intent = Intent()
    intent.action = RingtoneManager.ACTION_RINGTONE_PICKER
    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
    launcher.launch(intent)
}
