package com.example.composechatexample.screens.settings.model

import com.example.composechatexample.utils.PassUpdateState
import com.example.composechatexample.utils.SettingsDialogs

data class SettingsUIState(
    val language: String? = null,
    val theme: String? = null,
    val privacy: String? = null,
    val notification: Boolean? = null,
    val support: String? = null,
    val blackList: List<String> = listOf("user1", "user2", "user3", "user4"),

    val dialogs: SettingsDialogs? = null,
    val pass: PassState = PassState(),
    val errors: PassErrors = PassErrors()
)

data class PassState (
    val currentPass: String = "",
    val newPass: String = "",
    val repeatedPass: String = "",
)

data class PassErrors(
    var currentField: PassUpdateState = PassUpdateState.FIELD_CORRECTLY,
    var newField: PassUpdateState = PassUpdateState.FIELD_CORRECTLY,
    var repeatedField: PassUpdateState = PassUpdateState.FIELD_CORRECTLY,
)