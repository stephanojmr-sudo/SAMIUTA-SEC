package com.kitutu.matokeo.data

// ---------------------------------------------------------------------------
// VIGEZO VYA MSINGI (mfano wa mfumo wa HTML uliotangulia)
// ---------------------------------------------------------------------------

val SCHOOLS = listOf("SAMAMBA SS", "UTAHO SS", "MIANDI SS")

val SUBJECTS = listOf(
    "KISWAHILI",
    "HISTORIA YA TANZANIA NA MAADILI",
    "BUSINESS STUDIES",
    "ENGLISH LANGUAGE"
)

val SUBJECT_SHORT = mapOf(
    "KISWAHILI" to "KISW",
    "HISTORIA YA TANZANIA NA MAADILI" to "HIST",
    "BUSINESS STUDIES" to "BIZ",
    "ENGLISH LANGUAGE" to "ENG"
)

const val FORM_LABEL = "MATOKEO YA KIDATO CHA PILI"

val SEX_OPTIONS = listOf("Kiume", "Kike")

// ---------------------------------------------------------------------------
// UPANGAJI WA DARAJA / GPA (A=1 ... F=5, mfumo wa NECTA — ndogo ni bora)
// ---------------------------------------------------------------------------

fun gradeLetter(score: Double): String = when {
    score >= 75 -> "A"
    score >= 65 -> "B"
    score >= 45 -> "C"
    score >= 29 -> "D"
    else -> "F"
}

fun commentFor(avg: Double): String = when {
    avg >= 75 -> "VIZURI SANA"
    avg >= 65 -> "VIZURI"
    avg >= 45 -> "WASTANI"
    avg >= 29 -> "DHAIFU"
    else -> "MBAYA SANA"
}

fun gradePoint(grade: String): Int = when (grade) {
    "A" -> 1
    "B" -> 2
    "C" -> 3
    "D" -> 4
    else -> 5
}

fun buildFullName(first: String, middle: String, last: String): String =
    listOf(first, middle, last)
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .joinToString(" ")
