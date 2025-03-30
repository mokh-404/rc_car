package com.example.bluetoothcarcontroller

import android.bluetooth.*
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

enum class BluetoothConnectionState {
    DISCONNECTED,
    SCANNING,
    CONNECTING,
    CONNECTED
}

class BluetoothViewModel : ViewModel() {
    private val _connectionState = MutableStateFlow(BluetoothConnectionState.DISCONNECTED)
    val connectionState: StateFlow<BluetoothConnectionState> = _connectionState

    private val _deviceList = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val deviceList: StateFlow<List<BluetoothDevice>> = _deviceList

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private val HC05_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    fun initialize(context: Context) {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
    }

    fun startScan() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _connectionState.value = BluetoothConnectionState.SCANNING
                bluetoothAdapter?.startDiscovery()
            } catch (e: Exception) {
                _connectionState.value = BluetoothConnectionState.DISCONNECTED
            }
        }
    }

    fun stopScan() {
        bluetoothAdapter?.cancelDiscovery()
        if (_connectionState.value == BluetoothConnectionState.SCANNING) {
            _connectionState.value = BluetoothConnectionState.DISCONNECTED
        }
    }

    fun addDeviceToList(device: BluetoothDevice) {
        val currentList = _deviceList.value.toMutableList()
        if (!currentList.contains(device)) {
            currentList.add(device)
            _deviceList.value = currentList
        }
    }

    fun connectToDevice(device: BluetoothDevice) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _connectionState.value = BluetoothConnectionState.CONNECTING
                bluetoothSocket = device.createRfcommSocketToServiceRecord(HC05_UUID)
                bluetoothSocket?.connect()
                _connectionState.value = BluetoothConnectionState.CONNECTED
            } catch (e: IOException) {
                disconnect()
            }
        }
    }

    fun disconnect() {
        try {
            bluetoothSocket?.close()
        } catch (e: IOException) {
            // Handle error
        } finally {
            bluetoothSocket = null
            _connectionState.value = BluetoothConnectionState.DISCONNECTED
            _deviceList.value = emptyList()
        }
    }

    fun sendCommand(command: Char) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                bluetoothSocket?.outputStream?.write(command.code)
            } catch (e: IOException) {
                disconnect()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}