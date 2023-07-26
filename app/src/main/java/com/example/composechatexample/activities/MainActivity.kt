package com.example.composechatexample.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.example.composechatexample.navigation.MainEntryPoint
import com.example.composechatexample.ui.theme.ComposeChatExampleTheme
import com.example.composechatexample.utils.Ext.getLocale
import com.example.composechatexample.utils.Ext.setLanguage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguage(this, viewModel.getLocale(getLocale(this)))
        setContent {
            ComposeChatExampleTheme {
                MainEntryPoint()
            }
        }
    }
}