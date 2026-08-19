package com.kitutu.matokeo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val id: String,
    val firstName: String,
    val middleName: String,
    val lastName: String,
    val sex: String,
    val examNumber: String,
    val school: String,
    val scores: Map<String, Int> = emptyMap()
) {
    val fullName: String get() = buildFullName(firstName, middleName, lastName)

    fun hasScore(subject: String): Boolean =
        scores[subject] != null

    fun enteredSubjects(): List<String> =
        SUBJECTS.filter { hasScore(it) }

    fun total(): Int =
        enteredSubjects().sumOf { scores[it] ?: 0 }

    fun average(): Double {
        val entered = enteredSubjects()
        return if (entered.isEmpty()) 0.0 else total().toDouble() / entered.size
    }

    fun overallGrade(): String =
        if (enteredSubjects().isEmpty()) "-" else gradeLetter(average())

    fun overallComment(): String =
        if (enteredSubjects().isEmpty()) "BADO HAJAJAZWA" else commentFor(average())
}
