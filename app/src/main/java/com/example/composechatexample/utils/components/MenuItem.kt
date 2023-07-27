package com.example.composechatexample.utils.components

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun MenuItem(
    name: String,
    expanded: MutableState<Boolean>,
    onCLick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(
                text = name,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        onClick = {
            expanded.value = false
            onCLick()
        },
        colors = MenuDefaults.itemColors(
            textColor = MaterialTheme.colorScheme.onBackground
        )
    )
}