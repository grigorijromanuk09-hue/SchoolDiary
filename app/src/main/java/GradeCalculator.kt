package com.example.schooldiary.domain

import kotlin.math.ceil

object GradeCalculator {
    private const val EPS = 1e-9

    fun average(marks: List<Int>): Double =
        if (marks.isEmpty()) 0.0 else round2(marks.sum().toDouble() / marks.size)

    fun overallAverage(subjects: List<SubjectGrades>): Double {
        var sum = 0
        var count = 0
        for (s in subjects) {
            sum += s.marks.sum()
            count += s.marks.size
        }
        return if (count == 0) 0.0 else round2(sum.toDouble() / count)
    }

    /** Средний балл, если получить ещё одну оценку [newMark]. */
    fun averageWith(marks: List<Int>, newMark: Int): Double =
        round2((marks.sum() + newMark).toDouble() / (marks.size + 1))

    /**
     * Сколько оценок [newMark] подряд нужно, чтобы средний балл стал >= [target].
     * 0 — цель уже достигнута, null — этой оценкой цель недостижима.
     */
    fun marksNeeded(marks: List<Int>, target: Double, newMark: Int): Int? {
        val n = marks.size
        val sum = marks.sum()
        if (n == 0) return if (newMark >= target) 1 else null
        if (sum.toDouble() / n + EPS >= target) return 0
        if (newMark <= target) return null
        val k = ceil((target * n - sum) / (newMark - target) - EPS).toInt()
        return k.coerceAtLeast(1)
    }
}
