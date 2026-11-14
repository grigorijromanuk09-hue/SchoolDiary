package com.example.schooldiary.util

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Обёртка над системным BiometricPrompt.
 * Отпечаток / лицо, а если их нет — PIN, графический ключ или пароль устройства.
 * (BIOMETRIC_WEAK | DEVICE_CREDENTIAL поддерживается начиная с API 28.)
 */
class BiometricHelper(private val activity: FragmentActivity) {

    private val authenticators = BIOMETRIC_WEAK or DEVICE_CREDENTIAL

    fun isAvailable(): Boolean =
        BiometricManager.from(activity).canAuthenticate(authenticators) ==
            BiometricManager.BIOMETRIC_SUCCESS

    fun authenticate(
        title: String = "Вход в дневник",
        subtitle: String = "Отпечаток пальца, лицо или PIN-код устройства",
        onSuccess: () -> Unit,
        onError: (String) -> Unit = {}
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    onError(errString.toString())
                }
            }
        )
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(authenticators)
            .build()
        prompt.authenticate(info)
    }
}
