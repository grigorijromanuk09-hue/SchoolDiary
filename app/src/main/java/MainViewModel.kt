package com.example.schooldiary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schooldiary.data.SettingsRepository
import com.example.schooldiary.domain.AppSettings
import com.example.schooldiary.domain.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(private val repository: SettingsRepository) : ViewModel() {

    /** null — настройки ещё читаются из DataStore. */
    private val _settings = MutableStateFlow<AppSettings?>(null)
    val settings: StateFlow<AppSettings?> = _settings.asStateFlow()

    private val _authenticated = MutableStateFlow(false)
    val authenticated: StateFlow<Boolean> = _authenticated.asStateFlow()

    /** Чтобы автозапрос биометрии при старте не повторялся при повороте экрана. */
    var startupPromptShown = false

    init {
        viewModelScope.launch {
            repository.settings.collect { _settings.value = it }
        }
    }

    fun setTheme(theme: AppTheme) {
        _settings.update { it?.copy(theme = theme) } // мгновенное переключение
        viewModelScope.launch { repository.setTheme(theme) }
    }

    fun setBiometric(enabled: Boolean) {
        _settings.update { it?.copy(biometricEnabled = enabled) }
        viewModelScope.launch { repository.setBiometricEnabled(enabled) }
    }

    fun onAuthenticated() {
        _authenticated.value = true
    }

    fun logout() {
        _authenticated.value = false
    }
}
