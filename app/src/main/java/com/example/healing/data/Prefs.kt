package com.example.healing.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.stringPreferencesKey

private val Context.dataStore by preferencesDataStore(name = "healing_prefs")

class Prefs(private val context: Context) {

    private object Keys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val SELECTED_HABITS = stringSetPreferencesKey("selected_habits")
        val EC_NAME  = stringPreferencesKey("ec_name")
        val EC_PHONE = stringPreferencesKey("ec_phone")
        val EC_ADDR  = stringPreferencesKey("ec_addr")

        // ⇩⇩⇩ NUEVO: claves para Datos personales
        val PD_NAME   = stringPreferencesKey("pd_name")
        val PD_RUT    = stringPreferencesKey("pd_rut")
        val PD_ADDR   = stringPreferencesKey("pd_addr")
        val PD_BLOOD  = stringPreferencesKey("pd_blood")
        val PD_ALLERG = stringPreferencesKey("pd_allerg")
        // ⇧⇧⇧

        // ⇩⇩⇩ NUEVO: claves para Login/Registro local
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_PASSWORD = stringPreferencesKey("user_password")
        // ⇧⇧⇧
    }

    val isLoggedInFlow: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.IS_LOGGED_IN] ?: false }

    suspend fun setLoggedIn(value: Boolean) {
        context.dataStore.edit { it[Keys.IS_LOGGED_IN] = value }
    }

    val selectedHabitsFlow: Flow<Set<String>> =
        context.dataStore.data.map { it[Keys.SELECTED_HABITS] ?: emptySet() }

    suspend fun setSelectedHabits(ids: Set<String>) {
        context.dataStore.edit { it[Keys.SELECTED_HABITS] = ids }
    }

    // --- Contacto de emergencia ---
    data class EmergencyContact(val name: String, val phone: String, val address: String)

    val emergencyContactFlow: Flow<EmergencyContact?> =
        context.dataStore.data.map { p ->
            val n = p[Keys.EC_NAME]
            val ph = p[Keys.EC_PHONE]
            val a = p[Keys.EC_ADDR]
            if (n.isNullOrBlank() && ph.isNullOrBlank() && a.isNullOrBlank()) null
            else EmergencyContact(n.orEmpty(), ph.orEmpty(), a.orEmpty())
        }

    suspend fun saveEmergencyContact(c: EmergencyContact) {
        context.dataStore.edit { p ->
            p[Keys.EC_NAME]  = c.name
            p[Keys.EC_PHONE] = c.phone
            p[Keys.EC_ADDR]  = c.address
        }
    }

    // =======================
    //    DATOS PERSONALES
    // =======================

    data class PersonalData(
        val name: String,
        val rut: String,
        val address: String,
        val blood: String,
        val allergies: String
    )

    val personalDataFlow: Flow<PersonalData?> =
        context.dataStore.data.map { p ->
            val n  = p[Keys.PD_NAME]
            val r  = p[Keys.PD_RUT]
            val ad = p[Keys.PD_ADDR]
            val b  = p[Keys.PD_BLOOD]
            val al = p[Keys.PD_ALLERG]
            if ((n.isNullOrBlank() && r.isNullOrBlank() && ad.isNullOrBlank() && b.isNullOrBlank() && al.isNullOrBlank()))
                null
            else
                PersonalData(
                    name = n.orEmpty(),
                    rut = r.orEmpty(),
                    address = ad.orEmpty(),
                    blood = b.orEmpty(),
                    allergies = al.orEmpty()
                )
        }

    suspend fun savePersonalData(d: PersonalData) {
        context.dataStore.edit { p ->
            p[Keys.PD_NAME]   = d.name
            p[Keys.PD_RUT]    = d.rut
            p[Keys.PD_ADDR]   = d.address
            p[Keys.PD_BLOOD]  = d.blood
            p[Keys.PD_ALLERG] = d.allergies
        }
    }

    // ==============================
    //   NUEVO: LOGIN / REGISTRO LOCAL
    // ==============================

    data class User(val name: String, val email: String, val password: String)

    val userFlow: Flow<User?> = context.dataStore.data.map { prefs ->
        val n = prefs[Keys.USER_NAME]
        val e = prefs[Keys.USER_EMAIL]
        val p = prefs[Keys.USER_PASSWORD]
        if (n != null && e != null && p != null) User(n, e, p) else null
    }

    suspend fun saveUser(user: User) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_NAME] = user.name
            prefs[Keys.USER_EMAIL] = user.email
            prefs[Keys.USER_PASSWORD] = user.password
        }
    }

    suspend fun clearUser() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.USER_NAME)
            prefs.remove(Keys.USER_EMAIL)
            prefs.remove(Keys.USER_PASSWORD)
        }
    }
}
