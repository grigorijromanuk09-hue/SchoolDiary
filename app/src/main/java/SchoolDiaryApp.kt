package com.example.schooldiary

import android.app.Application
import android.content.Context
import com.example.schooldiary.data.MockDiaryRepository
import com.example.schooldiary.data.SettingsRepository
import com.example.schooldiary.domain.DiaryRepository

class SchoolDiaryApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

class AppContainer(context: Context) {
    val settingsRepository = SettingsRepository(context.applicationContext)
    val diaryRepository: DiaryRepository = MockDiaryRepository()
}
