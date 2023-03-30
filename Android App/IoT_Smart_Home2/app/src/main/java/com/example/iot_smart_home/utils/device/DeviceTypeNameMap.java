package com.example.iot_smart_home.utils.device;

import java.util.HashMap;
import java.util.Map;

public class DeviceTypeNameMap {
    private final Map<DeviceType, String> typeMap = new HashMap<>();

    public DeviceTypeNameMap() {
        typeMap.put(DeviceType.GENERIC, "Generic");
        typeMap.put(DeviceType.CLIMATE_GATHERING, "Climate");
        typeMap.put(DeviceType.LED, "LED");
        typeMap.put(DeviceType.LIGHTING, "Lighting");
        typeMap.put(DeviceType.PLANT_WATERING, "PlantWatering");
        typeMap.put(DeviceType.POWER, "Power");
        typeMap.put(DeviceType.WATER_CONTROL, "WaterControl");
    }

    public String get(DeviceType type) {
        return typeMap.get(type);
    }
}
