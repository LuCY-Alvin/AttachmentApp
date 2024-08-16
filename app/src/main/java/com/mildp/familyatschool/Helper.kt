package com.mildp.familyatschool

import android.util.Log
import com.mildp.familyatschool.model.database.Debug
import java.text.SimpleDateFormat
import java.util.*

class Helper {

    fun timeString(milliseconds: Long): String {
        return  SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.TAIWAN).format(Date(milliseconds))
    }

    fun log(TAG: String, log: String){
        Log.d(TAG,log)
        App.instance().dataDao.insertDebug(
            Debug(
                TAG,
                log,
                timeString(Calendar.getInstance().timeInMillis)
            )
        )
    }
}