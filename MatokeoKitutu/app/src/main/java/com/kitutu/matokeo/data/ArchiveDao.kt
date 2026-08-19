package com.kitutu.matokeo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ArchiveDao {

    @Query("SELECT * FROM archives ORDER BY savedAt DESC")
    fun observeAll(): Flow<List<ArchiveEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(archive: ArchiveEntity)

    @Query("DELETE FROM archives WHERE label = :label")
    suspend fun deleteByLabel(label: String)
}
