package com.example.iot_smart_home.utils.device;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.iot_smart_home.DeviceRecyclerViewAdapter;
import com.example.iot_smart_home.database.DeviceDBHelper;
import com.example.iot_smart_home.database.DeviceModel;
import com.example.iot_smart_home.utils.mqtt.MQTTClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DeviceList {
    public static final DeviceList INSTANCE = new DeviceList();

    private DeviceTypeNameMap typeNameMap;
    private DeviceNameTypeMap nameTypeMap;

    private ArrayList<Device> devices;
    private Set<String> names;

    private DeviceRecyclerViewAdapter adapter;
    private SQLiteDatabase db;
    private MQTTClient client;

    public DeviceList() {
        typeNameMap = new DeviceTypeNameMap();
        nameTypeMap = new DeviceNameTypeMap();
        devices = new ArrayList<>();
        names = new HashSet<>();
    }

    public void init(DeviceRecyclerViewAdapter adapter, DeviceDBHelper db, MQTTClient client) {
        this.adapter = adapter;
        this.db = db.getReadableDatabase();
        this.client = client;
    }

    public void add(Device device) {
        if (names.contains(device.name)) {
            return;
        }
        devices.add(device);
        names.add(device.name);
        client.subscribeObserver(device);
        client.subscribe(device.name, 1);
        adapter.notifyItemInserted(devices.size() - 1);
        saveData();
    }

    public Device get(String name) {
        if (!names.contains(name)) {
            return null;
        }

        for (Device device: devices) {
            if (device.name.equals(name)) {
                return device;
            }
        }

        throw new IllegalArgumentException("No such device registered in the DeviceList, however, the name is still there in 'names' map.");
    }

    public void delete(String name) {
        if (!names.contains(name)) {
            return;
        }
        names.remove(name);
        int pos = -1;

        for (int i = 0; i < this.devices.size(); i++) {
            if (devices.get(i).name.equals(name)) {
                pos = i;
                break;
            }
        }

        client.unsubscribe(devices.get(pos).name);
        devices.remove(pos);
        saveData();
        adapter.notifyItemRemoved(pos);
    }

    public ArrayList<Device> getArray() {
        return devices;
    }

    private void saveDevice(Device device) {
        ContentValues values = new ContentValues();
        values.put(DeviceModel.DeviceData.NAME, device.name);
        values.put(DeviceModel.DeviceData.TYPE, typeNameMap.get(device.type));

        db.insert(DeviceModel.DeviceData.TABLE_NAME, null, values);
    }

    private void flushData() {
        db.execSQL("DELETE FROM " + DeviceModel.DeviceData.TABLE_NAME + ";");
    }

    public void saveData() {
        flushData();
        for (Device device: devices) {
            saveDevice(device);
        }
    }

    public void loadData() {
        String[] projection = {
                DeviceModel.DeviceData.NAME,
                DeviceModel.DeviceData.TYPE
        };

        String sortOrder =
                BaseColumns._ID + " ASC";

        Cursor cursor = db.query(
                DeviceModel.DeviceData.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        while(cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DeviceModel.DeviceData.NAME));
            DeviceType type = nameTypeMap.get(cursor.getString(cursor.getColumnIndexOrThrow(DeviceModel.DeviceData.TYPE)));
            add(new Device(name, type));
        }
        cursor.close();
    }
}