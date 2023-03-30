package com.example.iot_smart_home.utils.device;

import android.util.Log;

import com.example.iot_smart_home.R;

import java.util.HashMap;
import java.util.Map;

public class DeviceIconMap {
    private Map<DeviceType, Integer> typeMap = new HashMap<>();

    public DeviceIconMap() {
        typeMap.put(DeviceType.GENERIC, R.drawable.ic_gear);
        typeMap.put(DeviceType.CLIMATE_GATHERING, R.drawable.ic_thermometer);
        typeMap.put(DeviceType.LED, R.drawable.ic_led);
        typeMap.put(DeviceType.LIGHTING, R.drawable.ic_lamp);
        typeMap.put(DeviceType.PLANT_WATERING, R.drawable.ic_plant);
        typeMap.put(DeviceType.POWER, R.drawable.ic_power);
        typeMap.put(DeviceType.WATER_CONTROL, R.drawable.ic_water);
    }

    public int get(DeviceType type) {
        try {
            return typeMap.get(type);
        } catch (NullPointerException e) {
            Log.e("ERROR DEVICE ICON MAP ON GET", "Couldn't retrieve icon");
            return R.drawable.ic_gear;
        }
    }

}
