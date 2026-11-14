package com.example.schooldiary.domain

import androidx.compose.runtime.Immutable
import java.util.Locale

enum class AppTheme(val title: String) {
    LIGHT("Светлая"),
    AMOLED("Чёрная AMOLED"),
    BLUE("Синяя"),
    CYBER("Чёрно-оранжевая")
}

@Immutable
data class AppSettings(
    val theme: AppTheme = AppTheme.LIGHT,
    val biometricEnabled: Boolean = true
)

@Immutable
data class StudentProfile(val name: String, val className: String, val school: String)

@Immutable
data class Lesson(
    val id: Int,
    val dayOfWeek: Int, // 1 = Пн ... 6 = Сб
    val number: Int,
    val start: String,
    val end: String,
    val subject: String,
    val room: String,
    val homework: String?,
    val grade: Int?
)

@Immutable
data class SubjectGrades(
    val id: Int,
    val name: String,
    val teacher: String,
    val marks: List<Int>
) {
    val average: Double = GradeCalculator.average(marks)
}

@Immutable
data class RatingEntry(val place: Int, val name: String, val average: Double, val isMe: Boolean)

@Immutable
data class ClassRating(
    val place: Int,
    val total: Int,
    val classAverage: Double,
    val top: List<RatingEntry>
)

fun round2(value: Double): Double = Math.round(value * 100.0) / 100.0

fun Double.format2(): String = String.format(Locale.US, "%.2f", this)
