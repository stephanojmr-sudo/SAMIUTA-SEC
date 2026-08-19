package com.kitutu.matokeo.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "matokeo_settings")

/**
 * Mipangilio midogo isiyo ya orodha (password za walimu/admin, hali ya
 * kufungwa, kichwa cha mwezi wa sasa) — inahifadhiwa kwa Jetpack DataStore
 * badala ya Room, kwa sababu ni thamani moja moja tu (si jedwali).
 */
class SettingsRepository(private val context: Context) {

    private object Keys {
        val ADMIN_PASSWORD = stringPreferencesKey("admin_password")
        val LOCKED = booleanPreferencesKey("locked")
        val PERIOD_LABEL = stringPreferencesKey("period_label")
        fun teacherPassword(school: String) = stringPreferencesKey("teacher_pw_$school")
        fun teacherPasswordChanged(school: String) = booleanPreferencesKey("teacher_pw_changed_$school")
    }

    val adminPassword: Flow<String> =
        context.dataStore.data.map { it[Keys.ADMIN_PASSWORD] ?: "admin123" }

    val locked: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.LOCKED] ?: false }

    val periodLabel: Flow<String> =
        context.dataStore.data.map { it[Keys.PERIOD_LABEL] ?: "" }

    fun teacherPassword(school: String): Flow<String> =
        context.dataStore.data.map { it[Keys.teacherPassword(school)] ?: "1234" }

    fun teacherPasswordChanged(school: String): Flow<Boolean> =
        context.dataStore.data.map { it[Keys.teacherPasswordChanged(school)] ?: false }

    suspend fun setAdminPassword(value: String) {
        context.dataStore.edit { it[Keys.ADMIN_PASSWORD] = value }
    }

    suspend fun setLocked(value: Boolean) {
        context.dataStore.edit { it[Keys.LOCKED] = value }
    }

    suspend fun setPeriodLabel(value: String) {
        context.dataStore.edit { it[Keys.PERIOD_LABEL] = value }
    }

    suspend fun setTeacherPassword(school: String, value: String) {
        context.dataStore.edit { it[Keys.teacherPassword(school)] = value }
    }

    suspend fun setTeacherPasswordChanged(school: String, value: Boolean) {
        context.dataStore.edit { it[Keys.teacherPasswordChanged(school)] = value }
    }
}
