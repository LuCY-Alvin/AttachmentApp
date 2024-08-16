package com.mildp.familyatschool.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mildp.familyatschool.Constants.mmkv
import java.io.File

@Database(
    entities = [GPSData::class, AcceleratorData::class, GyroData::class, Debug::class, SurveyData::class],
    version =1, exportSchema = false
)
abstract class DataBase: RoomDatabase() {

    abstract fun getDataDao(): DataDao

    companion object {
        private var INSTANCE: DataBase? = null

        fun getDatabase(context: Context): DataBase? {
            val participantType = mmkv.getBoolean("participant",false)
            val name = if (participantType) {
                "Child_database"
            } else {
                "Parent_database"
            }

            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, DataBase::class.java, name)
                    .allowMainThreadQueries()
                    .build()
            }
            return INSTANCE
        }
    }

    fun backupDatabase(context: Context): Int {
        var result = -99
        if (INSTANCE == null) return result
        val participantType = mmkv.getBoolean("participant",false)
        val name = if (participantType) {
            "Child_database"
        } else {
            "Parent_database"
        }

        val dbFile = context.getDatabasePath(name)
        val dbWalFile = File(dbFile.path + "-wal")
        val dbShmFile = File(dbFile.path + "-shm")
        val bkpFile = File(dbFile.path + "-backup")
        val bkpWalFile = File(bkpFile.path + "-wal")
        val bkpShmFile = File(bkpFile.path + "-shm")
        if (bkpFile.exists()) bkpFile.delete()
        if (bkpWalFile.exists()) bkpWalFile.delete()
        if (bkpShmFile.exists()) bkpShmFile.delete()
        checkpoint()
        try {
            dbFile.copyTo(bkpFile, true)
            if (dbWalFile.exists()) dbWalFile.copyTo(bkpWalFile, true)
            if (dbShmFile.exists()) dbShmFile.copyTo(bkpShmFile, true)
            result = 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    private fun checkpoint() {
        val db = this.openHelper.writableDatabase
        db.query("PRAGMA wal_checkpoint(FULL);", emptyArray())
        db.query("PRAGMA wal_checkpoint(TRUNCATE);", emptyArray())
    }
}