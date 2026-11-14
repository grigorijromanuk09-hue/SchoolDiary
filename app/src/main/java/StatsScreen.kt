package com.example.schooldiary.ui.stats

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.schooldiary.domain.DiaryRepository
import com.example.schooldiary.domain.GradeCalculator
import com.example.schooldiary.domain.SubjectGrades
import com.example.schooldiary.domain.format2
import com.example.schooldiary.ui.components.AppCard
import com.example.schooldiary.ui.components.GradeBadge
import com.example.schooldiary.ui.components.ScreenTitle
import com.example.schooldiary.ui.components.ThinProgress
import com.example.schooldiary.ui.components.ValuePill
import com.example.schooldiary.ui.components.averageColor
import com.example.schooldiary.ui.components.gradeColor
import com.example.schooldiary.ui.components.ruPlural
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StatsViewModel(repo: DiaryRepository) : ViewModel() {
    val subjects: List<SubjectGrades> = repo.getSubjects()

    /** Пары (оценка, сколько таких оценок) для 5, 4, 3, 2. */
    val distribution: List<Pair<Int, Int>> =
        (5 downTo 2).map { g -> g to subjects.sumOf { s -> s.marks.count { it == g } } }
    val totalMarks: Int = distribution.sumOf { it.second }

    // По умолчанию — предмет с самым низким средним баллом и цель 4.50
    private val _selectedId = MutableStateFlow(subjects.minBy { it.average }.id)
    val selectedId: StateFlow<Int> = _selectedId.asStateFlow()

    private val _target = MutableStateFlow(4.5)
    val target: StateFlow<Double> = _target.asStateFlow()

    fun selectSubject(id: Int) {
        _selectedId.value = id
    }

    fun setTarget(value: Double) {
        _target.value = value
    }
}

@Composable
fun StatsScreen(viewModel: StatsViewModel, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "title") {
            ScreenTitle("Статистика", "Распределение оценок и калькулятор", Modifier.padding(vertical = 8.dp))
        }
        item(key = "distribution") {
            DistributionCard(viewModel.distribution, viewModel.totalMarks)
        }
        item(key = "calculator") {
            CalculatorCard(viewModel)
        }
    }
}

@Composable
private fun DistributionCard(distribution: List<Pair<Int, Int>>, total: Int) {
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Распределение оценок", style = MaterialTheme.typography.titleLarge)
            distribution.forEach { (grade, count) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GradeBadge(grade, size = 30.dp)
                    Spacer(Modifier.width(12.dp))
                    ThinProgress(
                        progress = if (total == 0) 0f else count.toFloat() / total,
                        color = gradeColor(grade),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        count.toString(),
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun CalculatorCard(vm: StatsViewModel) {
    val selectedId by vm.selectedId.collectAsStateWithLifecycle()
    val target by vm.target.collectAsStateWithLifecycle()
    val subject = remember(selectedId) { vm.subjects.first { it.id == selectedId } }
    val needFives = remember(subject, target) { GradeCalculator.marksNeeded(subject.marks, target, 5) }
    val needFours = remember(subject, target) { GradeCalculator.marksNeeded(subject.marks, target, 4) }

    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Калькулятор оценок", style = MaterialTheme.typography.titleLarge)

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(vm.subjects, key = { it.id }) { s ->
                    FilterChip(
                        selected = s.id == selectedId,
                        onClick = { vm.selectSubject(s.id) },
                        label = { Text(s.name) }
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Сейчас по предмету", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "${subject.marks.size} ${ruPlural(subject.marks.size, "оценка", "оценки", "оценок")}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                ValuePill(subject.average.format2(), averageColor(subject.average))
            }

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Хочу получить средний балл", Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    ValuePill(target.format2(), MaterialTheme.colorScheme.primary)
                }
                Slider(
                    value = target.toFloat(),
                    onValueChange = { vm.setTarget(Math.round(it * 100) / 100.0) },
                    valueRange = 3f..4.95f,
                    steps = 38
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(3.5, 4.0, 4.5, 4.75).forEach { t ->
                        FilterChip(
                            selected = target == t,
                            onClick = { vm.setTarget(t) },
                            label = { Text(t.format2()) }
                        )
                    }
                }
            }

            NeedRow(5, needFives, "пятёрка", "пятёрки", "пятёрок")
            NeedRow(4, needFours, "четвёрка", "четвёрки", "четвёрок")

            Column {
                Text(
                    "Если получишь следующую оценку",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(5, 4, 3, 2).forEach { g ->
                        val avg = GradeCalculator.averageWith(subject.marks, g)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            GradeBadge(g, size = 28.dp)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                avg.format2(),
                                style = MaterialTheme.typography.labelLarge,
                                color = averageColor(avg)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NeedRow(grade: Int, count: Int?, one: String, few: String, many: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GradeBadge(grade, size = 36.dp)
        Spacer(Modifier.width(12.dp))
        val text = when {
            count == null -> "Этой оценкой цель недостижима"
            count == 0 -> "Цель уже достигнута"
            else -> "Нужно получить: $count ${ruPlural(count, one, few, many)} подряд"
        }
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}
