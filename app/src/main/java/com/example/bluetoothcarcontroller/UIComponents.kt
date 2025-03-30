package com.example.bluetoothcarcontroller

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ConnectionStatusIndicator(state: BluetoothConnectionState) {
    val color by animateColorAsState(
        targetValue = when (state) {
            BluetoothConnectionState.CONNECTED -> Color.Green
            BluetoothConnectionState.DISCONNECTED -> Color.Red
            else -> Color.Yellow
        },
        animationSpec = tween(durationMillis = 500)
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Bluetooth,
            contentDescription = "Bluetooth Status",
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = when (state) {
                BluetoothConnectionState.CONNECTED -> "Connected"
                BluetoothConnectionState.DISCONNECTED -> "Disconnected"
                BluetoothConnectionState.CONNECTING -> "Connecting..."
                BluetoothConnectionState.SCANNING -> "Scanning..."
            }
        )
    }
}

@Composable
fun DeviceList(
    devices: List<BluetoothDevice>,
    onDeviceClick: (BluetoothDevice) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        devices.forEach { device ->
            OutlinedButton(
                onClick = { onDeviceClick(device) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(device.name ?: "Unknown Device")
            }
        }
    }
}

@Composable
fun CarControlButtons(viewModel: BluetoothViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Forward button
        Button(
            onClick = { viewModel.sendCommand('U') },
            modifier = Modifier.size(80.dp)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Forward",
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Left, Right buttons in a row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { viewModel.sendCommand('L') },
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Left",
                    modifier = Modifier.size(40.dp)
                )
            }

            Button(
                onClick = { viewModel.sendCommand('R') },
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Right",
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Backward button
        Button(
            onClick = { viewModel.sendCommand('D') },
            modifier = Modifier.size(80.dp)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Backward",
                modifier = Modifier.size(40.dp)
            )
        }
    }
}