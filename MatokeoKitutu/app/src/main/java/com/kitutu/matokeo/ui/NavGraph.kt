package com.kitutu.matokeo.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kitutu.matokeo.ui.screens.AdminLoginScreen
import com.kitutu.matokeo.ui.screens.AdminPanelScreen
import com.kitutu.matokeo.ui.screens.ArchiveDetailScreen
import com.kitutu.matokeo.ui.screens.HomeScreen
import com.kitutu.matokeo.ui.screens.RosterFormScreen
import com.kitutu.matokeo.ui.screens.StudentPortalScreen
import com.kitutu.matokeo.ui.screens.TeacherDashboardScreen
import com.kitutu.matokeo.ui.screens.TeacherSchoolsScreen

@Composable
fun NavGraph(viewModel: AppViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(navController)
        }

        composable("teacher_schools") {
            TeacherSchoolsScreen(navController, viewModel)
        }

        composable(
            route = "teacher_dashboard/{school}",
            arguments = listOf(navArgument("school") { type = NavType.StringType })
        ) { backStackEntry ->
            val school = backStackEntry.arguments?.getString("school") ?: return@composable
            TeacherDashboardScreen(navController, viewModel, school)
        }

        composable(
            route = "roster_form/{school}?studentId={studentId}",
            arguments = listOf(
                navArgument("school") { type = NavType.StringType },
                navArgument("studentId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val school = backStackEntry.arguments?.getString("school") ?: return@composable
            val studentId = backStackEntry.arguments?.getString("studentId")?.takeIf { it.isNotEmpty() }
            RosterFormScreen(navController, viewModel, school, studentId)
        }

        composable("student_portal") {
            StudentPortalScreen(navController, viewModel)
        }

        composable("admin_login") {
            AdminLoginScreen(navController, viewModel)
        }

        composable("admin_panel") {
            AdminPanelScreen(navController, viewModel)
        }

        composable(
            route = "archive_detail/{label}",
            arguments = listOf(navArgument("label") { type = NavType.StringType })
        ) { backStackEntry ->
            val label = backStackEntry.arguments?.getString("label") ?: return@composable
            ArchiveDetailScreen(navController, viewModel, label)
        }
    }
}
