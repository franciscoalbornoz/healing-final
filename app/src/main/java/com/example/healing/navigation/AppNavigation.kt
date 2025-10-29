
package com.example.healing.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.navArgument
import androidx.navigation.NavType
import java.time.LocalDate

// pantallas
import com.example.healing.ui.screens.EmergencyContactScreen
import com.example.healing.ui.screens.PersonalDataScreen
import com.example.healing.ui.screens.LoginScreen
import com.example.healing.ui.screens.RegisterScreen
import com.example.healing.ui.screens.HomeScreen
import com.example.healing.ui.screens.NotesScreen
import com.example.healing.ui.screens.NotesListScreen
import com.example.healing.ui.screens.MedicationCalendarScreen
import com.example.healing.ui.screens.MedicationEditorScreen
import com.example.healing.ui.screens.MealPlanScreen

// data & viewmodels
import com.example.healing.data.Prefs
import com.example.healing.data.NotesDatabase
import com.example.healing.viewmodel.NotesViewModel
import com.example.healing.viewmodel.NotesViewModelFactory
import com.example.healing.data.MedsDatabase
import com.example.healing.viewmodel.MedicationViewModel
import com.example.healing.viewmodel.MedicationViewModelFactory
import com.example.healing.data.MealsDatabase
import com.example.healing.viewmodel.MealPlanViewModel
import com.example.healing.viewmodel.MealPlanViewModelFactory

@Composable
fun AppNavigation(
    navController: NavHostController
) {
    val context = LocalContext.current

    // Notas
    val notesDao = remember { NotesDatabase.get(context).dao() }
    val notesVm: NotesViewModel = viewModel(factory = NotesViewModelFactory(notesDao))

    // Medicamentos
    val medsDao = remember { MedsDatabase.get(context).dao() }
    val medsVm: MedicationViewModel = viewModel(factory = MedicationViewModelFactory(medsDao, context))

    // Plan alimenticio
    val mealsDao = remember { MealsDatabase.get(context).dao() }
    val mealsVm: MealPlanViewModel = viewModel(factory = MealPlanViewModelFactory(mealsDao))


    val prefs = remember { Prefs(context) }
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = Route.Login.route
    ) {
        // LOGIN
        composable(Route.Login.route) {
            LoginScreen(
                onLogin = {

                    // scope.launch { prefs.setLoggedIn(true) }
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegister = { navController.navigate(Route.Register.route) }
            )
        }

        // REGISTER
        composable(Route.Register.route) {
            RegisterScreen(
                onBackToLogin = {
                    val popped = navController.popBackStack(Route.Login.route, false)
                    if (!popped) {
                        navController.navigate(Route.Login.route) {
                            launchSingleTop = true
                            popUpTo(Route.Login.route) { inclusive = false }
                        }
                    }
                }
            )
        }

        // HOME
        composable(Route.Home.route) {
            HomeScreen(navController = navController)
        }

        // NOTAS
        composable(Route.Notes.route) {
            NotesScreen(navController = navController, vm = notesVm)
        }

        // LISTA DE NOTAS
        composable(Route.NotesList.route) {
            NotesListScreen(navController = navController, vm = notesVm)
        }

        // CONTACTO DE EMERGENCIA
        composable(Route.EmergencyContact.route) {
            EmergencyContactScreen(navController)
        }

        // DATOS PERSONALES
        composable(Route.Personal.route) {
            PersonalDataScreen(navController)
        }

        //  CALENDARIO DE MEDICAMENTOS
        composable(Route.MedsCalendar.route) {
            MedicationCalendarScreen(navController = navController, vm = medsVm)
        }

        //  EDITOR DE MEDICAMENTO
        composable(
            Route.MedEditor.route,
            arguments = listOf(navArgument("epochDay") { type = NavType.LongType })
        ) { backStackEntry ->
            val day = backStackEntry.arguments?.getLong("epochDay") ?: LocalDate.now().toEpochDay()
            MedicationEditorScreen(navController = navController, vm = medsVm, epochDay = day)
        }

        //  PLAN ALIMENTICIO
        composable(Route.FoodPlan.route) {
            MealPlanScreen(navController = navController, vm = mealsVm)
        }
    }
}
