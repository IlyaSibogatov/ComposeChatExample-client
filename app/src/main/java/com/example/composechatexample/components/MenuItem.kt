package com.example.composechatexample.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.composechatexample.utils.Type

@Composable
fun <T>ShowMenu(
    expanded: MutableState<Boolean>,
    data: List<Type<T>>,
    onCLick: (T) -> Unit,
) {
    Column(horizontalAlignment = Alignment.End) {
        DropdownMenu(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background),
            expanded = expanded.value,
            onDismissRequest = {
                expanded.value = false
            }
        ) {
            data.forEach {
                MenuItem(
                    name = stringResource(id = it.str),
                    expanded = expanded
                ) {
                    onCLick(it.nameType)
                }
            }
        }
    }
}

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