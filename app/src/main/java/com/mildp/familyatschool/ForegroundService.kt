package com.mildp.familyatschool

import android.Manifest
import android.app.*
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.IBinder
import android.os.PowerManager
import android.os.SystemClock
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.mildp.familyatschool.Constants.bridgeSpot_latitude
import com.mildp.familyatschool.Constants.bridgeSpot_longitude
import com.mildp.familyatschool.Constants.gooseSpot_latitude
import com.mildp.familyatschool.Constants.gooseSpot_longitude
import com.mildp.familyatschool.Constants.lakeSpot_latitude
import com.mildp.familyatschool.Constants.lakeSpot_longitude
import com.mildp.familyatschool.Constants.mmkv
import com.mildp.familyatschool.Constants.properDistance
import com.mildp.familyatschool.model.database.AcceleratorData
import com.mildp.familyatschool.model.database.GPSData
import com.mildp.familyatschool.model.database.GyroData
import kotlinx.coroutines.*
import no.nordicsemi.android.support.v18.scanner.*
import java.util.*

class ForegroundService : Service(), SensorEventListener {

    companion object {
        private const val TAG: String = "ForegroundService"
    }

    private lateinit var manager: NotificationManager
    private lateinit var sensorManager: SensorManager
    private lateinit var gpsManager: LocationManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var mScanning = false
    private var id:String = ""

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var gpsTime: String = ""
    private var gpsMilliseconds: Long = 0
    private var speed: Float = 0f

    private var rssi: Int = 0
    private var rssiMilliseconds: Long = 0
    private val rssiList = arrayListOf(0L)

    private lateinit var acc: Sensor
    private lateinit var gyro: Sensor

    private var dataJob: Job = Job()
    private var checkPermissionJob: Job = Job()

    private var start: Boolean = true

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        try{
            createNotificationChannel()
            showNotification()
            id = mmkv.decodeString("subID","").toString()
            getSensorData()
        } catch(e: Exception) {
            Helper().log(TAG, "Initial error: $e")
        }

        dataJob = repeatScanAndGPS()
        checkPermissionJob = repeatCheckPermission()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_REDELIVER_INTENT
    }

    private fun createNotificationChannel(){
        manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val serviceChannel = NotificationChannel(
            "ForegroundService", "MY SERVICE", NotificationManager.IMPORTANCE_LOW
        )
        serviceChannel.setSound(null, null)
        serviceChannel.setShowBadge(false)
        manager.createNotificationChannel(serviceChannel)
    }

    private fun showNotification(){
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(notificationIntent)
            getPendingIntent(500, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val notification = Notification
            .Builder(this, "ForegroundService")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setVisibility(Notification.VISIBILITY_SECRET)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(555, notification)
    }

    private fun getSensorData() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL)
        for (sensor in sensorList) {
            when (sensor.type) {
                Sensor.TYPE_GYROSCOPE -> gyro = sensor
                Sensor.TYPE_ACCELEROMETER -> acc = sensor
            }
        }
        try {
            if (::acc.isInitialized){
                sensorManager.registerListener(this,acc,SensorManager.SENSOR_DELAY_NORMAL)
                Helper().log(TAG,"Initialize ACC")
            }
            if (::gyro.isInitialized){
                sensorManager.registerListener(this,gyro,SensorManager.SENSOR_DELAY_NORMAL)
                Helper().log(TAG,"Initialize Gyro")
            }
        } catch(e: Exception) {
            Helper().log(TAG, "Fail to get SensorData: $e")
        }
    }

    private fun repeatScanAndGPS(): Job {
        return CoroutineScope(Dispatchers.Main + dataJob).launch {
            while(start){
                startLocationUpdates()
                scanBleDevices()
                delay(5 * 1000)
                checkRange()
                gpsInsert()
                scanBleDevices()
                delay(5 * 1000)
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        getLocationUpdates()
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    private fun scanBleDevices() {
        val mBluetoothAdapter = (App.instance().getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter
        if (mBluetoothAdapter != null) {
            val scanner by lazy { BluetoothLeScannerCompat.getScanner() }
            val setting = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()
            if(!mScanning) {
                mScanning = true
                try {
                    scanner.startScan(scanFilters(), setting, scanCallback)
                } catch(e: Exception) {
                    Helper().log( TAG, "Start scan error: $e")
                }
            } else {
                mScanning = false
                try{
                    scanner.stopScan(scanCallback)
                } catch(e: Exception) {
                    Helper().log( TAG, "Stop scan error: $e")
                }
            }
        } else {
            Helper().log(TAG,"找不到Bluetooth Adapter")
        }
    }

    private val scanCallback = object : ScanCallback(){
        override fun onScanResult(
            callbackType: Int,
            result: ScanResult
        ) {
            super.onScanResult(callbackType, result)
            Helper().log(TAG, "onScanResult: ${result.scanRecord.toString()}")
            rssi = result.rssi
            rssiMilliseconds = System.currentTimeMillis() - SystemClock.elapsedRealtime() + result.timestampNanos / 1000000
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Helper().log(TAG,"未開啟藍芽連接權限")
                return
            }
        }
        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            Helper().log(TAG,  "onBatchScanResults: ")
            super.onBatchScanResults(results)
        }
        override fun onScanFailed(errorCode: Int) {
            Helper().log(TAG, "onScanFailed: $errorCode")
            super.onScanFailed(errorCode)
        }
    }

    private fun scanFilters(): MutableList<ScanFilter> {
        val list: MutableList<ScanFilter> = ArrayList()
        val checked = mmkv.getBoolean("participant",false)
        val miBandAddress: String = if(checked) {
            "EA:CF:FB:C1:2F:80"
        } else {
            "C9:DF:44:A4:A4:E5"
        }
        list.add(ScanFilter.Builder().setDeviceAddress(miBandAddress).build())
        return list
    }

    private fun checkRange() {
        val gooseDone = mmkv.getBoolean("gooseDone",false)
        val lakeDone = mmkv.getBoolean("lakeDone",false)
        val bridgeDone = mmkv.getBoolean("bridgeDone",false)
        val notice = mmkv.getBoolean("noticeSurvey", false)
        val gooseDistance = getDistance(latitude, longitude, gooseSpot_latitude, gooseSpot_longitude)
        val lakeDistance = getDistance(latitude, longitude, lakeSpot_latitude, lakeSpot_longitude)
        val bridgeDistance = getDistance(latitude, longitude, bridgeSpot_latitude, bridgeSpot_longitude)
        if (!notice) {
            if (gooseDistance < properDistance && !gooseDone) {
                val gooseIntent = Intent()
                gooseIntent.action = "gooseSpot"
                gooseIntent.setClass(this, Receiver::class.java)
                this.sendBroadcast(gooseIntent)
            }
            if (lakeDistance < properDistance && !lakeDone) {
                val lakeIntent = Intent()
                lakeIntent.action = "lakeSpot"
                lakeIntent.setClass(this, Receiver::class.java)
                this.sendBroadcast(lakeIntent)
            }
            if (bridgeDistance < properDistance && !bridgeDone) {
                val bridgeIntent = Intent()
                bridgeIntent.action = "bridgeSpot"
                bridgeIntent.setClass(this, Receiver::class.java)
                this.sendBroadcast(bridgeIntent)
            }
        }
    }

    private fun getDistance(Lat1: Double, Lon1: Double, Lat2: Double, Lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(Lat1,Lon1,Lat2,Lon2, results)
        return results[0]
    }

    private fun gpsInsert() {
        if (rssiMilliseconds == rssiList[rssiList.size-1]){
            rssi = -100
        }
        rssiList.add(rssiMilliseconds)
        val gpsData = GPSData(id,latitude,longitude,gpsTime,gpsMilliseconds,rssi,speed,networkType(this))
        App.instance().dataDao.insertGPS(gpsData)
    }

    private fun networkType(context: Context): String{
        val result : String
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return "false"
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return "false"
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "wifi"
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "cellular"
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ethernet"
            else -> "false"
        }
        return result
    }

    private fun repeatCheckPermission(): Job {
        return CoroutineScope(Dispatchers.IO + checkPermissionJob).launch {
            while(start) {
                check()
                delay(60 * 1000)
            }
        }
    }

    private fun check() {
        gpsManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        val mBluetoothAdapter = (App.instance().getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter

        if (mBluetoothAdapter?.isEnabled == null) {
            val statusIntent = Intent()
            statusIntent.action = "BlueTooth"
            statusIntent.setClass(this, CheckStatusReceiver::class.java)
            this.sendBroadcast(statusIntent)
        }

        if (!gpsManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            val statusIntent = Intent()
            statusIntent.action = "GPS"
            statusIntent.setClass(this, CheckStatusReceiver::class.java)
            this.sendBroadcast(statusIntent)
        }

        if (!pm.isIgnoringBatteryOptimizations(this.packageName)) {
            val statusIntent = Intent()
            statusIntent.action = "BatteryOptimization"
            statusIntent.setClass(this, CheckStatusReceiver::class.java)
            this.sendBroadcast(statusIntent)
        }
    }

    private fun getLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                            .setMinUpdateIntervalMillis(1000)
                            .setMinUpdateDistanceMeters(1F)
                            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                if (p0.locations.isNotEmpty()) {
                    latitude = p0.lastLocation!!.latitude
                    longitude = p0.lastLocation!!.longitude
                    gpsMilliseconds = p0.lastLocation!!.time
                    gpsTime = Helper().timeString(gpsMilliseconds)
                    speed = p0.lastLocation!!.speed
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val values = event?.values
        val alpha = values?.get(0)
        val beta = values?.get(1)
        val gamma = values?.get(2)
        val milliseconds = Calendar.getInstance().timeInMillis
        if (event != null) {
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    val acceleratorData = AcceleratorData(
                        id, alpha, beta, gamma, Helper().timeString(milliseconds), milliseconds
                    )
                   App.instance().dataDao.insertAccelerator(acceleratorData)
                }
                else -> {
                    val gyroData = GyroData(
                        id, alpha, beta, gamma, Helper().timeString(milliseconds), milliseconds
                    )
                    App.instance().dataDao.insertGyro(gyroData)
                }
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onDestroy() {
        stopSensorManager()
        stopLocationUpdates()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        start = false
        super.onDestroy()
    }

    private fun stopSensorManager() {
        if(::acc.isInitialized) {
            sensorManager.unregisterListener(this, acc)
            Helper().log(TAG, "Cancel ACC")
        } else {
            Helper().log(TAG, "Unable to Cancel ACC")
        }

        if(::gyro.isInitialized) {
            sensorManager.unregisterListener(this, gyro)
            Helper().log(TAG, "Cancel GYRO")
        } else {
            Helper().log(TAG, "Unable to Cancel GYRO")
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }



}