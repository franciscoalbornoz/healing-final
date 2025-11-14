package com.example.healing.data

import android.content.Context

class MealImageStore(context: Context) {

    private val prefs = context.getSharedPreferences(
        "meal_images",
        Context.MODE_PRIVATE
    )

    fun saveImage(dayOfWeek: Int, mealType: String, uri: String) {
        prefs.edit()
            .putString(key(dayOfWeek, mealType), uri)   // ✔️ AQUÍ SE ARREGLA
            .apply()
    }

    fun getImage(dayOfWeek: Int, mealType: String): String? {
        return prefs.getString(key(dayOfWeek, mealType), null)
    }

    private fun key(day: Int, type: String) = "img_${day}_$type"
}
