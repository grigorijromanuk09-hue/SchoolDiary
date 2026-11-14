package com.example.schooldiary

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.schooldiary.ui.MainScreen
import com.example.schooldiary.ui.login.LoginScreen
import com.example.schooldiary.ui.theme.SchoolDiaryTheme
import com.example.schooldiary.ui.vmFactory
import com.example.schooldiary.util.BiometricHelper

// FragmentActivity (а не ComponentActivity) — требование BiometricPrompt
class MainActivity : FragmentActivity() {

    private val viewModel: MainViewModel by viewModels {
        vmFactory { MainViewModel((application as SchoolDiaryApp).container.settingsRepository) }
    }

    private lateinit var biometric: BiometricHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        biometric = BiometricHelper(this)

        setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            val authenticated by viewModel.authenticated.collectAsStateWithLifecycle()
            val current = settings

            if (current == null) {
                // Настройки ещё читаются — пустой кадр, чтобы не мигала неверная тема
                Box(Modifier.fillMaxSize())
            } else {
                SchoolDiaryTheme(current.theme) {
                    // Системный сканер вызывается при входе в приложение
                    LaunchedEffect(Unit) {
                        if (!viewModel.startupPromptShown) {
                            viewModel.startupPromptShown = true
                            val s = viewModel.settings.value
                            if (!viewModel.authenticated.value &&
                                s?.biometricEnabled == true &&
                                biometric.isAvailable()
                            ) {
                                promptBiometric()
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        if (authenticated) {
                            MainScreen(
                                settings = current,
                                onThemeSelected = viewModel::setTheme,
                                onBiometricToggle = ::onBiometricToggle,
                                onLogout = viewModel::logout
                            )
                        } else {
                            val biometricAvailable = remember { biometric.isAvailable() }
                            LoginScreen(
                                biometricAvailable = biometricAvailable,
                                onLogin = { login, password ->
                                    if (login.isNotBlank() && password.isNotBlank()) {
                                        viewModel.onAuthenticated()
                                        true
                                    } else {
                                        false
                                    }
                                },
                                onBiometric = ::promptBiometric
                            )
                        }
                    }
                }
            }
        }
    }

    private fun promptBiometric() {
        biometric.authenticate(
            onSuccess = { viewModel.onAuthenticated() },
            onError = { /* остаёмся на экране входа */ }
        )
    }

    private fun onBiometricToggle(enable: Boolean) {
        when {
            !enable -> viewModel.setBiometric(false)
            !biometric.isAvailable() -> Toast.makeText(
                this,
                "На устройстве не настроен отпечаток, лицо или PIN-код",
                Toast.LENGTH_LONG
            ).show()
            else -> biometric.authenticate(
                title = "Включить вход по биометрии",
                onSuccess = { viewModel.setBiometric(true) }
            )
        }
    }
}
