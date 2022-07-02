package com.rymo.felfel.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tbl_workshop")
data class WorkshopModel(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var name: String,
    var phone: String
)
