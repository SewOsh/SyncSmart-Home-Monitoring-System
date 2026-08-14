package com.example.syncsmart.data

/**
 * Static metadata for every real device under the Firebase "devices" node
 * (see DevicesRepository). Firebase only stores each device's live boolean
 * state — everything else about a device (its label, which room/floor it
 * belongs to, what kind of control UI it needs) lives here in the app.
 *
 * IDs match the real Firebase document IDs exactly, including the split-out
 * gang switches (each switch in a multi-gang panel is its own device, e.g.
 * "G01_3_gang_switch_s1"/"s2"/"s3" — there is no single combined "switch
 * panel" device).
 */
enum class DeviceKind { BULB, OUTLET, SWITCH, FLAME, CAMERA, AC }

/** Rough typical wattage for each device kind — there's no real power meter
 * anywhere in this project (Firebase only ever stores on/off), so Reports'
 * kWh figures are an estimate: watts × on-hours ÷ 1000, not a measurement. */
fun DeviceKind.estimatedWatts(): Int = when (this) {
    DeviceKind.BULB -> 10
    DeviceKind.OUTLET -> 60
    DeviceKind.SWITCH -> 15
    DeviceKind.FLAME -> 1200 // iron socket
    DeviceKind.CAMERA -> 5
    DeviceKind.AC -> 1500
}

data class RoomInfo(val id: String, val name: String, val floorId: String)

data class DeviceInfo(
    val id: String,
    val label: String,
    val roomId: String,
    val roomName: String,
    val floorId: String,
    val kind: DeviceKind
)

object DeviceCatalog {

    val rooms = listOf(
        RoomInfo("F101", "Master Bedroom", "first"),
        RoomInfo("F102", "Study Room", "first"),
        RoomInfo("F103", "Bathroom", "first"),
        RoomInfo("G01", "Living Room", "ground"),
        RoomInfo("G02", "Kitchen", "ground"),
        RoomInfo("G03", "Front Entrance", "ground")
    )

    val devices = listOf(
        DeviceInfo("F101_bedroom_light", "Bedroom Light", "F101", "Master Bedroom", "first", DeviceKind.BULB),
        DeviceInfo("F101_air_conditioner", "Air Conditioner", "F101", "Master Bedroom", "first", DeviceKind.AC),
        DeviceInfo("F101_2_gang_switch_s1", "Switch 1", "F101", "Master Bedroom", "first", DeviceKind.SWITCH),
        DeviceInfo("F101_2_gang_switch_s2", "Switch 2", "F101", "Master Bedroom", "first", DeviceKind.SWITCH),
        DeviceInfo("F102_study_room_light", "Study Room Light", "F102", "Study Room", "first", DeviceKind.BULB),
        DeviceInfo("F103_bathroom_light", "Bathroom Light", "F103", "Bathroom", "first", DeviceKind.BULB),
        DeviceInfo("G01_living_room_light", "Living Room Light", "G01", "Living Room", "ground", DeviceKind.BULB),
        DeviceInfo("G01_tv_power_outlet", "TV Outlet", "G01", "Living Room", "ground", DeviceKind.OUTLET),
        DeviceInfo("G01_3_gang_switch_s1", "Switch 1", "G01", "Living Room", "ground", DeviceKind.SWITCH),
        DeviceInfo("G01_3_gang_switch_s2", "Switch 2", "G01", "Living Room", "ground", DeviceKind.SWITCH),
        DeviceInfo("G01_3_gang_switch_s3", "Switch 3", "G01", "Living Room", "ground", DeviceKind.SWITCH),
        DeviceInfo("G02_kitchen_light", "Kitchen Light", "G02", "Kitchen", "ground", DeviceKind.BULB),
        DeviceInfo("G02_iron_socket", "Iron Socket", "G02", "Kitchen", "ground", DeviceKind.FLAME),
        DeviceInfo("G03_front_door_camera", "Front Door Camera", "G03", "Front Entrance", "ground", DeviceKind.CAMERA)
    )

    /** The only device with the 60-minute auto shut-off timer today. */
    const val IronSocketId = "G02_iron_socket"

    /** Landing device when Device Control is opened generically (bottom nav), not from a floor plan marker. */
    const val DefaultDeviceId = "G01_living_room_light"

    fun roomsForFloor(floorId: String) = rooms.filter { it.floorId == floorId }
    fun devicesForRoom(roomId: String) = devices.filter { it.roomId == roomId }
    fun byId(deviceId: String): DeviceInfo? = devices.find { it.id == deviceId }
}
