package com.example.schooldiary.domain

interface DiaryRepository {
    fun getProfile(): StudentProfile
    fun getWeek(): Map<Int, List<Lesson>>
    fun getSubjects(): List<SubjectGrades>
    fun getRating(): ClassRating
}
