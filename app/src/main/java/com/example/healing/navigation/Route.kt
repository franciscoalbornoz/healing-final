package com.example.healing.navigation

sealed class Route(val route: String) {
    object Login : Route("login")
    object Register : Route("register")
    object Home : Route("home")
    object Notes : Route("notes")
    object NotesList : Route("notes_list")
    object EmergencyContact : Route("emergency_contact")
    object Personal : Route("personal_data")
    object MedsCalendar : Route("meds_calendar")
    object MedEditor : Route("med_editor/{epochDay}") {
        fun create(epochDay: Long) = "med_editor/$epochDay"
    }
    object FoodPlan : Route("food_plan")  // 👈 NUEVA RUTA
}