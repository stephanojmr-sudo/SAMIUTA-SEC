package com.kitutu.matokeo.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class AppRepository(context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val studentDao = db.studentDao()
    private val archiveDao = db.archiveDao()

    val settings = SettingsRepository(context)

    val students: Flow<List<StudentEntity>> = studentDao.observeAll()
    val archives: Flow<List<ArchiveEntity>> = archiveDao.observeAll()

    suspend fun upsertStudent(student: StudentEntity) = studentDao.upsert(student)

    suspend fun deleteStudent(id: String) = studentDao.deleteById(id)

    suspend fun findStudent(school: String, examNumber: String): StudentEntity? =
        studentDao.findBySchoolAndExam(school, examNumber)

    suspend fun clearAllScores() = studentDao.clearAllScoresRaw()

    suspend fun saveArchive(label: String, students: List<StudentEntity>) {
        archiveDao.upsert(
            ArchiveEntity(
                label = label,
                studentsJson = ArchiveSerializer.serialize(students),
                savedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteArchive(label: String) = archiveDao.deleteByLabel(label)

    suspend fun restoreArchive(archive: ArchiveEntity) {
        studentDao.deleteAll()
        studentDao.insertAll(ArchiveSerializer.deserialize(archive.studentsJson))
    }

    fun studentsInArchive(archive: ArchiveEntity): List<StudentEntity> =
        ArchiveSerializer.deserialize(archive.studentsJson)
}
