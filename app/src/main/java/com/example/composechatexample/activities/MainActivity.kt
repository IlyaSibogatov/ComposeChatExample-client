package com.example.composechatexample.activities

import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import com.example.composechatexample.navigation.MainEntryPoint
import com.example.composechatexample.ui.theme.ComposeChatExampleTheme
import com.example.composechatexample.ui.theme.configurationState
import com.example.composechatexample.ui.theme.themeState
import com.example.composechatexample.utils.Ext.getLocale
import com.example.composechatexample.utils.Ext.setLanguage
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguage(this, viewModel.getLocale(getLocale(this)))
        setContent {
            themeState = remember {
                mutableStateOf(viewModel.getTheme())
            }
            val langState = remember {
                mutableStateOf(Locale(viewModel.getLocale(getLocale(this))))
            }
            val config = LocalConfiguration.current
            config.setLocales(LocaleList(langState.value))
            configurationState = remember {
                mutableStateOf(config)
            }
            CompositionLocalProvider(
                values = arrayOf(LocalConfiguration provides configurationState.value)
            ) {
                ComposeChatExampleTheme {
                    MainEntryPoint()
                }
            }
        }
    }
}