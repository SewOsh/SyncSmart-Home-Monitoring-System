export const FLOORS = [
  {
    id: "ground",
    label: "Ground floor",
    rooms: [
      {
        id: "G01",
        label: "G01 - Living Room",
        navIcon: "sofa",
        devices: [
          { id: "G01_living_room_light", type: "toggle", icon: "bulb", title: "Living Room Light" },
          { id: "G01_tv_power_outlet", type: "toggle", icon: "plug", title: "TV Power Outlet" },
          {
            id: "G01_3_gang_switch",
            type: "switchPanel",
            title: "3-Gang Switch Panel",
            switches: [
              { id: "G01_3_gang_switch_s1", label: "Switch 1" },
              { id: "G01_3_gang_switch_s2", label: "Switch 2" },
              { id: "G01_3_gang_switch_s3", label: "Switch 3" },
            ],
          },
        ],
      },
      {
        id: "G02",
        label: "G02 - Kitchen",
        navIcon: "utensils",
        devices: [
          { id: "G02_kitchen_light", type: "toggle", icon: "bulb", title: "Kitchen Light" },
          { id: "G02_iron_socket", type: "toggle", icon: "plug", title: "Iron Socket", offNote: "safety cutoff", hasTimer: true },
        ],
      },
      {
        id: "G03",
        label: "G03 - Front Entrance",
        navIcon: "door",
        devices: [
          { id: "G03_front_door_camera", type: "camera", icon: "camera", title: "Front Door Camera" },
        ],
      },
    ],
  },
  {
    id: "first",
    label: "First floor",
    rooms: [
      {
        id: "F101",
        label: "F101 - Master Bedroom",
        navIcon: "bed",
        devices: [
          { id: "F101_bedroom_light", type: "toggle", icon: "bulb", title: "Bedroom Light" },
          { id: "F101_air_conditioner", type: "toggle", icon: "snowflake", title: "Air Conditioner" },
          {
            id: "F101_2_gang_switch",
            type: "switchPanel",
            title: "2-Gang Switch Panel",
            switches: [
              { id: "F101_2_gang_switch_s1", label: "Switch 1" },
              { id: "F101_2_gang_switch_s2", label: "Switch 2" },
            ],
          },
        ],
      },
      {
        id: "F102",
        label: "F102 - Study Room",
        navIcon: "book",
        devices: [
          { id: "F102_study_room_light", type: "toggle", icon: "bulb", title: "Study Room Light" },
        ],
      },
      {
        id: "F103",
        label: "F103 - Bathroom",
        navIcon: "bath",
        devices: [
          { id: "F103_bathroom_light", type: "toggle", icon: "bulb", title: "Bathroom Light" },
        ],
      },
    ],
  },
];
