package com.example.schooldiary.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.schooldiary.domain.DiaryRepository
import com.example.schooldiary.domain.Lesson
import com.example.schooldiary.ui.components.AppCard
import com.example.schooldiary.ui.components.GradeBadge
import com.example.schooldiary.ui.components.ScreenTitle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@Immutable
data class DayTab(val day: Int, val label: String, val dateNumber: Int)

class ScheduleViewModel(repo: DiaryRepository) : ViewModel() {
    private val today: LocalDate = LocalDate.now()

    val week: Map<Int, List<Lesson>> = repo.getWeek()

    val tabs: List<DayTab> = run {
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб").mapIndexed { i, label ->
            DayTab(i + 1, label, monday.plusDays(i.toLong()).dayOfMonth)
        }
    }

    val todayLabel: String =
        "Сегодня, " + today.format(DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("ru")))

    private val _selectedDay = MutableStateFlow(today.dayOfWeek.value.let { if (it > 6) 1 else it })
    val selectedDay: StateFlow<Int> = _selectedDay.asStateFlow()

    fun selectDay(day: Int) {
        _selectedDay.value = day
    }
}

@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel, modifier: Modifier = Modifier) {
    val selectedDay by viewModel.selectedDay.collectAsStateWithLifecycle()
    val lessons by remember(viewModel) {
        derivedStateOf { viewModel.week[selectedDay].orEmpty() }
    }
    val gradesCount by remember { derivedStateOf { lessons.count { it.grade != null } } }

    Column(modifier.fillMaxSize()) {
        ScreenTitle(
            title = "Дневник",
            subtitle = viewModel.todayLabel,
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 12.dp)
        )
        DayTabsRow(viewModel.tabs, selectedDay, viewModel::selectDay)
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item(key = "summary") {
                Text(
                    text = if (lessons.isEmpty()) "Занятий нет"
                    else "Уроков: ${lessons.size} · Оценок: $gradesCount",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(lessons, key = { it.id }, contentType = { "lesson" }) { lesson ->
                LessonItem(lesson)
            }
        }
    }
}

@Composable
private fun DayTabsRow(tabs: List<DayTab>, selected: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        tabs.forEach { tab ->
            val isSelected = tab.day == selected
            val scheme = MaterialTheme.colorScheme
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) scheme.primary else scheme.surfaceContainer)
                    .clickable { onSelect(tab.day) }
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    tab.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) scheme.onPrimary else scheme.onSurfaceVariant
                )
                Text(
                    tab.dateNumber.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSelected) scheme.onPrimary else scheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun LessonItem(lesson: Lesson) {
    AppCard(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Column(Modifier.width(54.dp)) {
                Text(lesson.start, style = MaterialTheme.typography.titleMedium)
                Text(
                    lesson.end,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(Modifier.weight(1f)) {
                Text(lesson.subject, style = MaterialTheme.typography.titleMedium)
                val room = if (lesson.room.all { it.isDigit() }) "каб. ${lesson.room}" else lesson.room
                Text(
                    "${lesson.number} урок · $room",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (lesson.homework != null) {
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            .padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            "ДЗ",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            lesson.homework,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            lesson.grade?.let { GradeBadge(it, Modifier.padding(start = 10.dp)) }
        }
    }
}
