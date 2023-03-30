package com.example.iot_smart_home.utils.device;

public class AvailableDevice {
    public String name;
    public DeviceType type;

    private static final DeviceNameTypeMap typeMap = new DeviceNameTypeMap();

    public AvailableDevice(String name, DeviceType type) {
        this.name = name;
        this.type = type;
    }

    public AvailableDevice(String name, String type) {
        this.name = name;
        this.type = typeMap.get(type);
    }
}
