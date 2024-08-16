package com.mildp.familyatschool

import com.tencent.mmkv.MMKV

object Constants {

    var mmkv: MMKV = MMKV.defaultMMKV()

    const val gooseSpot_latitude:Double = 25.020653 //25.020653  //南館 25.019669
    const val gooseSpot_longitude:Double = 121.537915 //121.537915 //南館 121.540411

    const val lakeSpot_latitude:Double = 25.020893
    const val lakeSpot_longitude:Double = 121.537280

    const val bridgeSpot_latitude:Double = 25.019870
    const val bridgeSpot_longitude:Double = 121.537717

    const val properDistance: Float = 20F
}