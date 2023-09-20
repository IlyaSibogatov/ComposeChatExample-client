package com.example.composechatexample.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.composechatexample.data.preferences.PreferencesManager
import com.example.composechatexample.navigation.MainEntryPoint
import com.example.composechatexample.ui.theme.ComposeChatExampleTheme
import com.example.composechatexample.ui.theme.configurationState
import com.example.composechatexample.ui.theme.themeState
import com.example.composechatexample.utils.Ext.getLocale
import com.example.composechatexample.utils.Ext.setLanguage
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupPermissions()
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
                    MainEntryPoint(preferencesManager)
                }
            }
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("TOKEN GET FAILED")
                return@OnCompleteListener
            }
        })
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makePermissionRequest()
        }
    }

    private fun makePermissionRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            NOTIFICATION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            NOTIFICATION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    viewModel.setNotificationEnabled(false)
                else viewModel.setNotificationEnabled(true)
            }
        }
    }

    companion object {
        const val NOTIFICATION_REQUEST_CODE = 101
    }
}