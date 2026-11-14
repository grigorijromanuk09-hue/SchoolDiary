package com.example.schooldiary.ui.grades

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.schooldiary.domain.ClassRating
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

class GradesViewModel(repo: DiaryRepository) : ViewModel() {
    val subjects: List<SubjectGrades> = repo.getSubjects()
    val rating: ClassRating = repo.getRating()
    val overall: Double = GradeCalculator.overallAverage(subjects)
    val totalMarks: Int = subjects.sumOf { it.marks.size }
}

private fun performanceLabel(avg: Double) = when {
    avg >= 4.5 -> "Отличная успеваемость"
    avg >= 3.5 -> "Хорошая успеваемость"
    avg >= 2.5 -> "Удовлетворительная"
    else -> "Нужно подтянуть"
}

@Composable
fun GradesScreen(viewModel: GradesViewModel, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "title") {
            ScreenTitle("Оценки", "Успеваемость и рейтинг", Modifier.padding(vertical = 8.dp))
        }
        item(key = "overall") {
            OverallCard(viewModel.overall, viewModel.totalMarks)
        }
        item(key = "rating") {
            RatingCard(viewModel.rating)
        }
        item(key = "subjects_title") {
            Text(
                "Предметы",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        items(viewModel.subjects, key = { it.id }, contentType = { "subject" }) { subject ->
            SubjectItem(subject)
        }
    }
}

@Composable
private fun OverallCard(average: Double, totalMarks: Int) {
    val color = averageColor(average)
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = average.format2(),
                    color = color,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Средний балл", style = MaterialTheme.typography.titleMedium)
                    Text(
                        performanceLabel(average),
                        style = MaterialTheme.typography.bodyMedium,
                        color = color
                    )
                    Text(
                        "Всего оценок: $totalMarks",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            ThinProgress((average / 5.0).toFloat(), color, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun RatingCard(rating: ClassRating) {
    val progress = (rating.total - rating.place).toFloat() / (rating.total - 1)
    val ahead = (rating.total - rating.place) * 100 / rating.total
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp)) {
            Text("Рейтинг в классе", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text(
                "Твоё место: ${rating.place} из ${rating.total} учеников",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(12.dp))
            ThinProgress(progress, MaterialTheme.colorScheme.primary, Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Text(
                "Ты впереди $ahead% одноклассников · средний по классу ${rating.classAverage.format2()}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            rating.top.forEach { entry ->
                val bg = if (entry.isMe) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(bg)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        entry.place.toString(),
                        modifier = Modifier.width(28.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        entry.name,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (entry.isMe) FontWeight.Bold else FontWeight.Normal
                    )
                    Text(
                        entry.average.format2(),
                        style = MaterialTheme.typography.titleMedium,
                        color = averageColor(entry.average)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SubjectItem(subject: SubjectGrades) {
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(subject.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        subject.teacher,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                ValuePill(subject.average.format2(), averageColor(subject.average))
            }
            Spacer(Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                subject.marks.forEach { GradeBadge(it, size = 26.dp) }
            }
        }
    }
}
