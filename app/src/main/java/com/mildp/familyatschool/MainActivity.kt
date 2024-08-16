package com.mildp.familyatschool

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mildp.familyatschool.Constants.mmkv
import com.mildp.familyatschool.ui.components.HomeScreen
import com.mildp.familyatschool.ui.theme.FamilyAtSchoolTheme
import com.mildp.familyatschool.viewmodels.HomeViewModel

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG: String = "MainActivity"
    }

    private lateinit var mBluetoothAdapter: BluetoothAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FamilyAtSchoolTheme {
                val homeViewModel = viewModel<HomeViewModel>()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    HomeScreen(homeViewModel)
                }
            }
        }
        onPermissionGranted()
        initBlueAdapter()
        checkParticipant()
    }

    private fun onPermissionGranted() {
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val hasPermissions = permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
        if (!hasPermissions) {
            requestPermissions()
        }
    }

    private fun requestPermissions(){
        val permissions = mutableListOf(
            Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE)
        }
        ActivityCompat.requestPermissions(
            this, permissions.toTypedArray(), 520
        )
    }

    private fun initBlueAdapter() {
        mBluetoothAdapter =
            (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }

    private fun checkParticipant(){
        when(getDeviceName()) {
            "SOYES MAXo" -> {
                mmkv.encode("participant", true)
                mmkv.encode("bandName", "小孩：手機A & 手環B")
                Helper().log(TAG, "小孩：手機A & 手環B")
            }
            "S10 Max" -> {
                mmkv.encode("participant", false)
                mmkv.encode("bandName", "大人：手機B & 手環A")
                Helper().log(TAG, "大人：手機B & 手環A")
            }
            else -> {
                Toast.makeText(App.instance(), "ERROR!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else {
            capitalize(manufacturer) + " " + model
        }
    }

    private fun capitalize(s: String): String {
        if (s.isNotEmpty()) {
            val firstChar = s[0]
            if (Character.isLowerCase(firstChar)) {
                return Character.toUpperCase(firstChar) + s.substring(1)
            }
        }
        return s
    }
}

