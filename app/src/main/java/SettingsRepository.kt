package com.example.schooldiary.data

import android.content.Context
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.schooldiary.domain.AppSettings
import com.example.schooldiary.domain.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private val themeKey = stringPreferencesKey("theme")
    private val biometricKey = booleanPreferencesKey("biometric_enabled")

    val settings: Flow<AppSettings> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs ->
            AppSettings(
                theme = prefs[themeKey]
                    ?.let { name -> runCatching { AppTheme.valueOf(name) }.getOrNull() }
                    ?: AppTheme.LIGHT,
                biometricEnabled = prefs[biometricKey] ?: true
            )
        }

    suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit { it[themeKey] = theme.name }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { it[biometricKey] = enabled }
    }
}
