package com.mildp.familyatschool.viewmodels

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mildp.familyatschool.App
import com.mildp.familyatschool.Constants.mmkv
import com.mildp.familyatschool.ForegroundService
import com.mildp.familyatschool.Helper

class HomeViewModel: ViewModel() {

    companion object {
        private const val TAG: String = "HomePage"
    }

    private lateinit var gpsManager: LocationManager
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    var subID by mutableStateOf(mmkv.decodeString("subID","").toString())
    var participant by mutableStateOf(mmkv.decodeString("bandName","").toString())

    @SuppressLint("BatteryLife")
    fun onClicked(): Boolean {
        val pm = App.instance().getSystemService(AppCompatActivity.POWER_SERVICE) as PowerManager
        val packageName = App.instance().packageName
        mBluetoothAdapter = (App.instance().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        gpsManager = App.instance().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!mBluetoothAdapter.isEnabled) {
            Toast.makeText(App.instance(),"請開啟藍芽", Toast.LENGTH_SHORT).show()
        } else if (!gpsManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(App.instance(),"請開啟GPS定位系統", Toast.LENGTH_SHORT).show()
        } else if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            Toast.makeText(App.instance(),"請關閉省電模式", Toast.LENGTH_SHORT).show()
            val intent = Intent()
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            App.instance().startActivity(intent)
        } else {
            Toast.makeText(App.instance(),"設定皆已完成，已開始實驗", Toast.LENGTH_SHORT).show()
            mmkv.encode("subID", subID)
            App.instance().startForegroundService(Intent(App.instance(), ForegroundService::class.java))
            Helper().log(TAG,"Start Task of $subID ($participant)")
            return true
        }
        return false
    }
}