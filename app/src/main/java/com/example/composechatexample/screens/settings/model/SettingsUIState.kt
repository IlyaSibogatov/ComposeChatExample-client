package com.example.composechatexample.screens.settings.model

data class SettingsUIState(
    val language: String? = null,
    val theme: String? = null,
    val privacy: String = "",
    val notification: String = "",
    val blackList: List<String> = listOf("user1", "user2", "user3", "user4"),
    val support: String = "",

    val showPassDialog: Boolean = false,
    val pass: PassState = PassState(),

    val errors: PassErrors = PassErrors()
)

data class PassState (
    val currentPass: String = "",
    val newPass: String = "",
    val repeatedPass: String = "",
)

data class PassErrors(
    var currentNewSame: Boolean = false,
    var newestNotSame: Boolean = false,

    var currentNotMatch: Boolean = false,
    var newNotMatch: Boolean = false,
    var repeatNotMatch: Boolean = false,

    var currentIsEmpty: Boolean = false,
    var newIsEmpty: Boolean = false,
    var repeatIsEmpty: Boolean = false,

    var newEqOld: Boolean = false,
    var currentNotEqOld: Boolean = false,
)