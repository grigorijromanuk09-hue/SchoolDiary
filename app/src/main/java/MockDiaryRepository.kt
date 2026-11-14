package com.example.schooldiary.data

import com.example.schooldiary.domain.ClassRating
import com.example.schooldiary.domain.DiaryRepository
import com.example.schooldiary.domain.GradeCalculator
import com.example.schooldiary.domain.Lesson
import com.example.schooldiary.domain.RatingEntry
import com.example.schooldiary.domain.StudentProfile
import com.example.schooldiary.domain.SubjectGrades

class MockDiaryRepository : DiaryRepository {

    private val profile = StudentProfile(
        name = "Алексей Смирнов",
        className = "9 «Б»",
        school = "МБОУ СОШ № 57"
    )

    private val times = listOf(
        "08:30" to "09:15",
        "09:25" to "10:10",
        "10:25" to "11:10",
        "11:25" to "12:10",
        "12:25" to "13:10",
        "13:25" to "14:10"
    )

    private val week: Map<Int, List<Lesson>> by lazy { buildWeek() }

    private val subjects: List<SubjectGrades> = listOf(
        SubjectGrades(1, "Алгебра", "Иванова Е. П.", listOf(5, 4, 5, 5, 4, 5, 5, 4)),
        SubjectGrades(2, "Геометрия", "Иванова Е. П.", listOf(4, 4, 5, 4, 5, 4)),
        SubjectGrades(3, "Русский язык", "Петрова О. А.", listOf(5, 5, 4, 5, 4, 5)),
        SubjectGrades(4, "Литература", "Петрова О. А.", listOf(5, 5, 5, 4, 5)),
        SubjectGrades(5, "Физика", "Сидоров А. В.", listOf(4, 3, 4, 4, 5, 4)),
        SubjectGrades(6, "Химия", "Кузнецова Н. И.", listOf(5, 4, 5, 5)),
        SubjectGrades(7, "История", "Орлов Д. С.", listOf(5, 5, 5, 4, 5, 5)),
        SubjectGrades(8, "Английский язык", "Белова М. К.", listOf(5, 5, 5, 5, 4, 5, 5)),
        SubjectGrades(9, "Информатика", "Громов П. Р.", listOf(5, 5, 5, 5)),
        SubjectGrades(10, "Физкультура", "Волков С. Н.", listOf(5, 5, 5))
    )

    private fun buildWeek(): Map<Int, List<Lesson>> {
        var id = 1
        fun l(day: Int, n: Int, subject: String, room: String, hw: String?, grade: Int?) =
            Lesson(id++, day, n, times[n - 1].first, times[n - 1].second, subject, room, hw, grade)

        val all = listOf(
            // Понедельник
            l(1, 1, "Алгебра", "214", "§12, № 345, 347 (а, б)", 5),
            l(1, 2, "Русский язык", "105", "Упр. 201, выучить правило", null),
            l(1, 3, "Физика", "301", "§8, вопросы 1–5", 4),
            l(1, 4, "История", "210", "Прочитать §6, составить план", 5),
            l(1, 5, "Английский язык", "118", "Ex. 4, p. 56 (письменно)", null),
            l(1, 6, "Физкультура", "Спортзал", null, null),
            // Вторник
            l(2, 1, "Геометрия", "214", "п. 23, задачи 112, 115", 4),
            l(2, 2, "Литература", "105", "Читать «Капитанскую дочку», гл. 5–6", 5),
            l(2, 3, "Химия", "305", "§10, упр. 3–5", null),
            l(2, 4, "Информатика", "402", "Подготовить проект на Python", 5),
            l(2, 5, "Алгебра", "214", "№ 361, 363", null),
            // Среда
            l(3, 1, "Русский язык", "105", "Упр. 215", 5),
            l(3, 2, "Английский язык", "118", "Выучить слова Unit 5", 5),
            l(3, 3, "Физика", "301", "Лабораторная работа № 3, оформить", null),
            l(3, 4, "История", "210", null, null),
            l(3, 5, "Геометрия", "214", "Повторить теоремы п. 20–23", 4),
            l(3, 6, "Физкультура", "Спортзал", null, null),
            // Четверг
            l(4, 1, "Алгебра", "214", "Контрольная: повторить гл. 2", 4),
            l(4, 2, "Литература", "105", "Выучить стихотворение наизусть", null),
            l(4, 3, "Химия", "305", "Задачи на молярную массу", 5),
            l(4, 4, "Английский язык", "118", "Эссе 100 слов", null),
            l(4, 5, "Информатика", "402", null, 5),
            // Пятница
            l(5, 1, "Физика", "301", "§9, задачи 1–4", 4),
            l(5, 2, "Алгебра", "214", "№ 375, 377", 5),
            l(5, 3, "Русский язык", "105", "Сочинение-рассуждение", null),
            l(5, 4, "Геометрия", "214", "п. 24, № 128", 4),
            l(5, 5, "История", "210", "Доклад о Петре I", 5),
            l(5, 6, "Физкультура", "Спортзал", null, null),
            // Суббота
            l(6, 1, "Литература", "105", null, 5),
            l(6, 2, "Английский язык", "118", "Ex. 2, p. 60", null),
            l(6, 3, "Информатика", "402", null, null),
            l(6, 4, "Классный час", "101", null, null)
        )
        return all.groupBy { it.dayOfWeek }
    }

    override fun getProfile(): StudentProfile = profile

    override fun getWeek(): Map<Int, List<Lesson>> = week

    override fun getSubjects(): List<SubjectGrades> = subjects

    override fun getRating(): ClassRating {
        val me = GradeCalculator.overallAverage(subjects)
        return ClassRating(
            place = 4,
            total = 25,
            classAverage = 4.12,
            top = listOf(
                RatingEntry(1, "Ученик 1", 4.91, false),
                RatingEntry(2, "Ученик 2", 4.83, false),
                RatingEntry(3, "Ученик 3", 4.72, false),
                RatingEntry(4, "Ты", me, true),
                RatingEntry(5, "Ученик 5", 4.58, false)
            )
        )
    }
}
