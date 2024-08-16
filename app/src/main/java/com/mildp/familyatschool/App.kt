package com.mildp.familyatschool

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.mildp.familyatschool.model.database.DataBase
import com.mildp.familyatschool.model.database.DataDao
import com.tencent.mmkv.MMKV

class App: Application() {

    companion object{
        private lateinit var instance: App
        fun instance() = instance
    }

    lateinit var dataDao: DataDao
        private set

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        instance = this
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        dataDao = DataBase.getDatabase(this)?.getDataDao()!!
    }
}