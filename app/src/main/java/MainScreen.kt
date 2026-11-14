package com.example.schooldiary.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.schooldiary.SchoolDiaryApp
import com.example.schooldiary.domain.AppSettings
import com.example.schooldiary.domain.AppTheme
import com.example.schooldiary.ui.grades.GradesScreen
import com.example.schooldiary.ui.grades.GradesViewModel
import com.example.schooldiary.ui.profile.ProfileScreen
import com.example.schooldiary.ui.schedule.ScheduleScreen
import com.example.schooldiary.ui.schedule.ScheduleViewModel
import com.example.schooldiary.ui.stats.StatsScreen
import com.example.schooldiary.ui.stats.StatsViewModel

private enum class Tab(val route: String, val title: String, val icon: ImageVector) {
    SCHEDULE("schedule", "Дневник", Icons.Filled.DateRange),
    GRADES("grades", "Оценки", Icons.Filled.Star),
    STATS("stats", "Статистика", Icons.Filled.Info),
    PROFILE("profile", "Профиль", Icons.Filled.Person)
}

@Composable
fun MainScreen(
    settings: AppSettings,
    onThemeSelected: (AppTheme) -> Unit,
    onBiometricToggle: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    val container = (LocalContext.current.applicationContext as SchoolDiaryApp).container
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val profile = remember { container.diaryRepository.getProfile() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                Tab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Tab.SCHEDULE.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(tween(150)) },
            exitTransition = { fadeOut(tween(100)) },
            popEnterTransition = { fadeIn(tween(150)) },
            popExitTransition = { fadeOut(tween(100)) }
        ) {
            composable(Tab.SCHEDULE.route) {
                val vm: ScheduleViewModel = viewModel(
                    factory = vmFactory { ScheduleViewModel(container.diaryRepository) }
                )
                ScheduleScreen(vm)
            }
            composable(Tab.GRADES.route) {
                val vm: GradesViewModel = viewModel(
                    factory = vmFactory { GradesViewModel(container.diaryRepository) }
                )
                GradesScreen(vm)
            }
            composable(Tab.STATS.route) {
                val vm: StatsViewModel = viewModel(
                    factory = vmFactory { StatsViewModel(container.diaryRepository) }
                )
                StatsScreen(vm)
            }
            composable(Tab.PROFILE.route) {
                ProfileScreen(
                    profile = profile,
                    settings = settings,
                    onThemeSelected = onThemeSelected,
                    onBiometricToggle = onBiometricToggle,
                    onLogout = onLogout
                )
            }
        }
    }
}
