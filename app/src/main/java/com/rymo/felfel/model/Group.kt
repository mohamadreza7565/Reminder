package com.rymo.felfel.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_group")
data class Group(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var name: String = " ",
    @Ignore
    var selected: Boolean = false
)

@Entity(tableName = "tbl_contact_group")
data class ContactGroup(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var contactId: Long = 0,
    var groupId: Long = 0
)
