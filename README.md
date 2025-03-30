# Bluetooth Car Controller

A Native Android (Kotlin) application for controlling an Arduino-based car via HC-05 Bluetooth module. The app provides a modern, user-friendly interface with a dark theme and handles all Bluetooth Classic operations including scanning, pairing, connecting, and sending serial commands.

## Features

- **Bluetooth Connection Management**
  - Automatic scanning for HC-05 devices
  - Seamless pairing and connection
  - Auto-reconnect functionality
  - Visual connection status indicator

- **Car Control**
  - Forward movement ('U')
  - Backward movement ('D')
  - Left turn ('L')
  - Right turn ('R')

- **Modern UI**
  - Dark theme
  - Intuitive control buttons
  - Smooth animations
  - Status indicators

## Build Instructions

### Prerequisites
- Codemagic CI/CD setup
- Android device with Bluetooth support
- Arduino car with HC-05 module

### Building with Codemagic
1. Fork/clone this repository
2. Set up a new project in Codemagic
3. Configure the build settings using the provided `codemagic.yaml`
4. Trigger the build

## Permissions
The app requires the following permissions:
- Bluetooth
- Bluetooth Admin
- Bluetooth Connect (Android 12+)
- Bluetooth Scan (Android 12+)

## Architecture
The app follows a clean architecture pattern with:
- Jetpack Compose for UI
- ViewModel for state management
- Kotlin Coroutines for asynchronous operations
- Android Bluetooth API for device communication