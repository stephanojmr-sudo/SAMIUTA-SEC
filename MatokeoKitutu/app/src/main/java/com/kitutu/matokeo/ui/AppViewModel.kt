package com.kitutu.matokeo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kitutu.matokeo.data.AppRepository
import com.kitutu.matokeo.data.ArchiveEntity
import com.kitutu.matokeo.data.StudentEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class AppViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AppRepository(app)

    val students: StateFlow<List<StudentEntity>> =
        repo.students.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val archives: StateFlow<List<ArchiveEntity>> =
        repo.archives.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val locked: StateFlow<Boolean> =
        repo.settings.locked.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val periodLabel: StateFlow<String> =
        repo.settings.periodLabel.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    // ---- Mwalimu: uthibitisho na password ----

    suspend fun verifyTeacherPassword(school: String, input: String): Boolean =
        repo.settings.teacherPassword(school).first() == input

    suspend fun isFirstTeacherLogin(school: String): Boolean =
        !repo.settings.teacherPasswordChanged(school).first()

    fun setTeacherPassword(school: String, newPassword: String) {
        viewModelScope.launch {
            repo.settings.setTeacherPassword(school, newPassword)
            repo.settings.setTeacherPasswordChanged(school, true)
        }
    }

    fun resetTeacherPassword(school: String, newPassword: String) {
        viewModelScope.launch {
            repo.settings.setTeacherPassword(school, newPassword)
            // Alazimike kuibadili tena akiingia mara ijayo.
            repo.settings.setTeacherPasswordChanged(school, false)
        }
    }

    // ---- Admin ----

    suspend fun verifyAdminPassword(input: String): Boolean =
        repo.settings.adminPassword.first() == input

    fun setAdminPassword(value: String) {
        viewModelScope.launch { repo.settings.setAdminPassword(value) }
    }

    fun setLocked(value: Boolean) {
        viewModelScope.launch { repo.settings.setLocked(value) }
    }

    fun setPeriodLabel(value: String) {
        viewModelScope.launch { repo.settings.setPeriodLabel(value) }
    }

    // ---- Wanafunzi (roster + alama) ----

    fun addOrUpdateRoster(
        existingId: String?,
        firstName: String,
        middleName: String,
        lastName: String,
        sex: String,
        examNumber: String,
        school: String
    ) {
        viewModelScope.launch {
            val id = existingId ?: UUID.randomUUID().toString()
            val existingScores = existingId
                ?.let { eid -> students.value.find { it.id == eid }?.scores }
                ?: emptyMap()
            repo.upsertStudent(
                StudentEntity(
                    id = id,
                    firstName = firstName,
                    middleName = middleName,
                    lastName = lastName,
                    sex = sex,
                    examNumber = examNumber,
                    school = school,
                    scores = existingScores
                )
            )
        }
    }

    fun deleteStudent(id: String) {
        viewModelScope.launch { repo.deleteStudent(id) }
    }

    fun setScore(studentId: String, subject: String, value: Int?) {
        viewModelScope.launch {
            val current = students.value.find { it.id == studentId } ?: return@launch
            val newScores = current.scores.toMutableMap()
            if (value == null) newScores.remove(subject) else newScores[subject] = value
            repo.upsertStudent(current.copy(scores = newScores))
        }
    }

    fun clearAllScores() {
        viewModelScope.launch { repo.clearAllScores() }
    }

    // ---- Hifadhi (archives) ----

    fun saveArchive(label: String) {
        viewModelScope.launch { repo.saveArchive(label, students.value) }
    }

    fun deleteArchive(label: String) {
        viewModelScope.launch { repo.deleteArchive(label) }
    }

    fun restoreArchive(archive: ArchiveEntity) {
        viewModelScope.launch { repo.restoreArchive(archive) }
    }

    fun studentsInArchive(archive: ArchiveEntity): List<StudentEntity> =
        repo.studentsInArchive(archive)

    companion object {
        fun factory(app: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    AppViewModel(app) as T
            }
    }
}
