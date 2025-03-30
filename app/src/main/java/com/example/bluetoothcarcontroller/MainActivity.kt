package com.example.bluetoothcarcontroller

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    private val bluetoothPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            // Permissions granted, proceed with Bluetooth operations
        } else {
            // Handle permission denied
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BluetoothCarControllerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: BluetoothViewModel = viewModel()
                    MainScreen(viewModel)
                }
            }
        }
    }

    private fun checkAndRequestBluetoothPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        }

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            bluetoothPermissionLauncher.launch(permissionsToRequest)
        }
    }
}

@Composable
fun MainScreen(viewModel: BluetoothViewModel) {
    val context = LocalContext.current
    val connectionState by viewModel.connectionState.collectAsState()
    val deviceList by viewModel.deviceList.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Connection status indicator
        ConnectionStatusIndicator(connectionState)

        Spacer(modifier = Modifier.height(16.dp))

        // Scan/Connect button
        Button(
            onClick = {
                when (connectionState) {
                    BluetoothConnectionState.DISCONNECTED -> viewModel.startScan()
                    BluetoothConnectionState.CONNECTED -> viewModel.disconnect()
                    else -> {}
                }
            }
        ) {
            Text(
                when (connectionState) {
                    BluetoothConnectionState.DISCONNECTED -> "Scan for HC-05"
                    BluetoothConnectionState.CONNECTED -> "Disconnect"
                    BluetoothConnectionState.CONNECTING -> "Connecting..."
                    BluetoothConnectionState.SCANNING -> "Scanning..."
                }
            )
        }

        // Device list (only show when scanning)
        if (connectionState == BluetoothConnectionState.SCANNING) {
            DeviceList(deviceList, viewModel::connectToDevice)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Control buttons (only show when connected)
        if (connectionState == BluetoothConnectionState.CONNECTED) {
            CarControlButtons(viewModel)
        }
    }
}