package com.rymo.felfel.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tbl_inbox_sms")
data class InboxSmsModel(

    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val from: String,
    val message: String,
    val name: String,
    val companyName: String,
    val date: Long

)
