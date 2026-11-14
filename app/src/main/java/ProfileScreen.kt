package com.example.schooldiary.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.schooldiary.domain.AppSettings
import com.example.schooldiary.domain.AppTheme
import com.example.schooldiary.domain.StudentProfile
import com.example.schooldiary.ui.components.AppCard
import com.example.schooldiary.ui.components.ScreenTitle
import com.example.schooldiary.ui.theme.colorSchemeFor

@Composable
fun ProfileScreen(
    profile: StudentProfile,
    settings: AppSettings,
    onThemeSelected: (AppTheme) -> Unit,
    onBiometricToggle: (Boolean) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ScreenTitle("Профиль", "Данные ученика и настройки", Modifier.padding(vertical = 8.dp))

        // Карточка ученика
        AppCard(Modifier.fillMaxWidth()) {
            Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                val initials = remember(profile.name) {
                    profile.name.split(" ").filter { it.isNotBlank() }.take(2)
                        .map { it.first() }.joinToString("")
                }
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        initials,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(profile.name, style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Класс ${profile.className}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        profile.school,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Темы
        Text("Тема оформления", style = MaterialTheme.typography.titleLarge)
        AppTheme.entries.chunked(2).forEach { rowThemes ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowThemes.forEach { theme ->
                    ThemeOption(
                        theme = theme,
                        selected = theme == settings.theme,
                        onClick = { onThemeSelected(theme) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Безопасность
        Text("Безопасность", style = MaterialTheme.typography.titleLarge)
        AppCard(Modifier.fillMaxWidth()) {
            Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Вход по отпечатку / лицу", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Системный сканер, запасной вариант — PIN-код устройства",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.width(12.dp))
                Switch(checked = settings.biometricEnabled, onCheckedChange = onBiometricToggle)
            }
        }

        Spacer(Modifier.height(4.dp))
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Выйти из аккаунта")
        }
    }
}

@Composable
private fun ThemeOption(
    theme: AppTheme,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val preview = remember(theme) { colorSchemeFor(theme) }
    val shape = RoundedCornerShape(18.dp)
    val border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)

    Column(
        modifier = modifier
            .clip(shape)
            .background(preview.background)
            .border(border, shape)
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(preview.surfaceContainer)
                    .border(1.dp, preview.outline, CircleShape)
            )
            Box(Modifier.size(22.dp).clip(CircleShape).background(preview.primary))
            Box(Modifier.size(22.dp).clip(CircleShape).background(preview.onBackground))
        }
        Spacer(Modifier.height(10.dp))
        Text(
            theme.title,
            style = MaterialTheme.typography.labelLarge,
            color = preview.onBackground
        )
    }
}
