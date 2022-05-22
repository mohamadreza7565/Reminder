package com.rymo.felfel.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rymo.felfel.database.dao.ContactDao
import com.rymo.felfel.database.dao.SmsMessageDao
import com.rymo.felfel.model.*


@Database(
    entities = [
        Contact::class,
        AlarmContact::class,
        Group::class,
        ContactGroup::class,
        SmsMessageModel::class,
        SmsMessageSendTime::class,
        ExportSmsMessage::class,
    ], version = 3
)
abstract class RoomAppDatabase : RoomDatabase() {

    abstract fun contactDao(): ContactDao
    abstract fun smsMessageDao(): SmsMessageDao

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
