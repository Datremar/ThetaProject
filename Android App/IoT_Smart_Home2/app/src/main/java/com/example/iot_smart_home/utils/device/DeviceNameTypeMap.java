package com.example.iot_smart_home.utils.device;

import java.util.HashMap;
import java.util.Map;

public class DeviceNameTypeMap {
    private final Map<String, DeviceType> typeMap = new HashMap<>();

    public DeviceNameTypeMap() {
        typeMap.put("Generic", DeviceType.GENERIC);
        typeMap.put("Climate", DeviceType.CLIMATE_GATHERING);
        typeMap.put("LED", DeviceType.LED);
        typeMap.put("Lighting", DeviceType.LIGHTING);
        typeMap.put("PlantWatering", DeviceType.PLANT_WATERING);
        typeMap.put("Power", DeviceType.POWER);
        typeMap.put("WaterControl", DeviceType.WATER_CONTROL);
    }

    public DeviceType get(String typeName) {
        return typeMap.get(typeName);
    }
}
