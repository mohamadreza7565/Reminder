package com.rymo.felfel.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rymo.felfel.database.dao.ContactDao
import com.rymo.felfel.model.AlarmContact
import com.rymo.felfel.model.Contact


@Database(
    entities = [
        Contact::class,
        AlarmContact::class
    ], version = 2
)
abstract class RoomAppDatabase : RoomDatabase() {

    abstract fun contactDao(): ContactDao

    companion object {
        val DATABASE_NAME = "db_felfel"
    }

}

fun createDataBaseInstance(context: Context): RoomAppDatabase {
    return Room.databaseBuilder(
        context,
        RoomAppDatabase::class.java,
        RoomAppDatabase.DATABASE_NAME
    ).allowMainThreadQueries()
        .build()

}
