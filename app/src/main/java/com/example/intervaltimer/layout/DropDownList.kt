package com.example.intervaltimer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun DropDownList(list: List<Int>, description: String) {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedTime: Int by remember { mutableStateOf(-1) }
    val labelFontSize = 29.sp
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
                },
                    text = {
                    Text(it.toString())
                })
            }
        }
    }
}

@Composable
@Preview
fun DropDownListPreview() {
    DropDownList(list = listOf(9, 20, 30, 40, 50), "Test label")
}