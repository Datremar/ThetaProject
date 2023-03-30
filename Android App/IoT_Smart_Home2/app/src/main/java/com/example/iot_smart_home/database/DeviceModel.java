package com.example.iot_smart_home.database;

import android.provider.BaseColumns;

public final class DeviceModel {
    private DeviceModel() {}

    public static class DeviceData implements BaseColumns {
        public static final String TABLE_NAME = "device_data";
        public static final String NAME = "name";
        public static final String TYPE = "type";
    }
}
