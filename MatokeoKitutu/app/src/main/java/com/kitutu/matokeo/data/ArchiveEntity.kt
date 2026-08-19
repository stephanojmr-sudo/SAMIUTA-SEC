package com.kitutu.matokeo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "archives")
data class ArchiveEntity(
    @PrimaryKey val label: String,
    val studentsJson: String,
    val savedAt: Long
)
