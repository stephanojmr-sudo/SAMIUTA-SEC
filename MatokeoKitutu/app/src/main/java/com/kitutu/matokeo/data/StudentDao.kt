package com.kitutu.matokeo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Query("SELECT * FROM students")
    fun observeAll(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE school = :school AND examNumber = :examNumber LIMIT 1")
    suspend fun findBySchoolAndExam(school: String, examNumber: String): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(student: StudentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(students: List<StudentEntity>)

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM students")
    suspend fun deleteAll()

    // Njia ya haraka ya "Futa Alama Zote" bila kupakia kila safu mmoja mmoja kwenye kumbukumbu.
    @Query("UPDATE students SET scores = '{}'")
    suspend fun clearAllScoresRaw()
}
