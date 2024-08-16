package com.mildp.familyatschool.model.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "GPS Data")
data class GPSData(

    @PrimaryKey(autoGenerate = true)
    var uid: Int,

    @ColumnInfo(name = "subID")
    var SubID: String ="",

    @ColumnInfo(name = "Latitude")
    var Lat: Double = 0.0,

    @ColumnInfo(name = "Longitude")
    var Long: Double = 0.0,

    @ColumnInfo(name = "TimeStamp")
    var Time: String = "",

    @ColumnInfo(name = "TimeInMillis")
    var MilliSeconds: Long = 0,

    @ColumnInfo(name = "RSSI")
    var RSSI: Int,

    @ColumnInfo(name = "Speed")
    var Speed: Float,

    @ColumnInfo(name = "Network")
    var Network: String = ""

){
    constructor(
        SubID: String, Lat: Double, Long: Double, Time: String, MilliSeconds: Long, RSSI: Int, Speed: Float, Network: String) :
            this(0, SubID, Lat, Long, Time, MilliSeconds, RSSI, Speed, Network)
}


@Entity(tableName = "AcceleratorData")
data class AcceleratorData(

    @PrimaryKey(autoGenerate = true)
    var uid: Int,

    @ColumnInfo(name = "subID")
    var SubID: String ="",

    @ColumnInfo(name = "X")
    var X: Float? =0f,

    @ColumnInfo(name = "Y")
    var Y: Float? =0f,

    @ColumnInfo(name = "Z")
    var Z: Float? =0f,

    @ColumnInfo(name = "TimeStamp")
    var Time: String = "",

    @ColumnInfo(name = "TimeInMillis")
    var MilliSeconds: Long = 0
){
    constructor(SubID: String,X: Float?, Y: Float?, Z: Float?, Time: String, MilliSeconds: Long) : this(0,SubID ,X, Y, Z, Time, MilliSeconds)
}

@Entity(tableName = "GyroData")
data class GyroData(

    @PrimaryKey(autoGenerate = true)
    var uid: Int,

    @ColumnInfo(name = "subID")
    var SubID: String ="",

    @ColumnInfo(name = "X")
    var X: Float? =0f,

    @ColumnInfo(name = "Y")
    var Y: Float? =0f,

    @ColumnInfo(name = "Z")
    var Z: Float? =0f,

    @ColumnInfo(name = "TimeStamp")
    var Time: String = "",

    @ColumnInfo(name = "TimeInMillis")
    var MilliSeconds: Long = 0
){
    constructor(SubID: String,X: Float?, Y: Float?, Z: Float?, Time: String, MilliSeconds: Long) : this(0,SubID ,X, Y, Z, Time, MilliSeconds)
}

@Entity(tableName = "SurveyData")
data class SurveyData(

    @PrimaryKey(autoGenerate = true)
    var uid: Int,

    @ColumnInfo(name = "subID")
    var SubID: String ="",

    @ColumnInfo(name = "Spot")
    var spot: Int,

    @ColumnInfo(name = "Answer1")
    var ans1: Int ,

    @ColumnInfo(name = "Answer2")
    var ans2: Int ,

    @ColumnInfo(name = "Answer3")
    var ans3: Int ,

    @ColumnInfo(name = "TimeStamp")
    var Time: String = "",

    ){
    constructor(SubID: String,spot: Int, ans1: Int, ans2: Int, ans3: Int, Time: String) : this(0,SubID,spot,ans1,ans2,ans3,Time)
}

@Entity(tableName = "Debug")
data class Debug(

    @PrimaryKey(autoGenerate = true)
    var uid: Int,

    @ColumnInfo(name = "File")
    var File: String? ="",

    @ColumnInfo(name = "Log")
    var Log: String ="",

    @ColumnInfo(name = "TimeStamp")
    var Time: String = "",

    ){
    constructor(File: String, Log: String, Time: String) : this(0, File, Log, Time)
}
