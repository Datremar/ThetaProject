package com.example.iot_smart_home.database;

public final class DeviceQueries {
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DeviceModel.DeviceData.TABLE_NAME + " (" +
                    DeviceModel.DeviceData._ID + " INTEGER PRIMARY KEY," +
                    DeviceModel.DeviceData.NAME + " TEXT," +
                    DeviceModel.DeviceData.TYPE + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DeviceModel.DeviceData.TABLE_NAME;
}
