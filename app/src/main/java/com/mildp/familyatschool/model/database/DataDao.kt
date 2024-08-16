package com.mildp.familyatschool.model.database

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface DataDao {

    @Insert
    fun insertGPS(gpsData: GPSData)

    @Insert
    fun insertAccelerator(accelerator: AcceleratorData)

    @Insert
    fun insertGyro(gyroData: GyroData)

    @Insert
    fun insertSurvey(surveyData: SurveyData)

    @Insert
    fun insertDebug(log: Debug)

}