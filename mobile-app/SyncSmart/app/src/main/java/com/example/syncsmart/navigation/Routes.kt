package com.example.syncsmart.navigation

/** Central place for every navigation route string, so screens never hardcode paths. */
sealed class Routes(val route: String) {
    object Splash : Routes("splash")
    object Welcome : Routes("welcome")
    object Login : Routes("login")
    object Dashboard : Routes("dashboard")
    object FloorPlan : Routes("floor/{floorId}") {
        fun with(floorId: String) = "floor/$floorId"
    }
    // The "Devices" bottom-nav tab — lists every real device with live status,
    // grouped by room. Tapping one opens DeviceControl for that specific device.
    object Devices : Routes("devices")
    object DeviceControl : Routes("device-control?deviceId={deviceId}") {
        const val base = "device-control"
        fun with(deviceId: String) = "$base?deviceId=$deviceId"
    }
    object Reports : Routes("reports")
    // Add Settings here once that screen is built.
}
