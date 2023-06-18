package com.example.iot_smart_home.utils.device;

import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.iot_smart_home.utils.observer.Observer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class AvailableDevices implements Observer {
    public static AvailableDevices INSTANCE = new AvailableDevices();

    private ArrayList<AvailableDevice> devices;
    ArrayList<String> names;
    private JSONObject jsonObject;
    private ArrayAdapter<String> adapter;

    public AvailableDevices() {
        names = new ArrayList<>();
        devices = new ArrayList<>();
    }

    public void init(ArrayAdapter<String> adapter) {
        this.adapter = adapter;
    }

    public ArrayList<String> getDeviceNames() {
         return names;
    }

    public AvailableDevice getDevice(String name) throws IllegalArgumentException {
        for (AvailableDevice device: devices) {
            if (device.name.equals(name)) {
                return device;
            }
        }

        throw new IllegalArgumentException("The device with the name: " + name + " is not in the list.");
    }

    @Override
    public void update(String topic, String message) {
        Log.d("____________________DEBUG UPDATE AVAILABLE DEVICES", "Incoming topic: " + topic);
        Log.d("____________________DEBUG UPDATE AVAILABLE DEVICES", "Updating...");
        if (Objects.equals(topic, "/device_id")) {
            try {
                jsonObject = new JSONObject(message);

                for (String name: names) {
                    if (name.equals(jsonObject.getString("name"))) {
                        Log.d("____________________DEBUG UPDATE AVAILABLE DEVICES", "Skipping " + name);
                        return;
                    }
                }

                Log.d("_____________________________________________________________Available Devices DEBUG", "Adding device " + topic + " with data " + message);

                names.add(jsonObject.getString("name"));
                devices.add(new AvailableDevice(
                            jsonObject.getString("name"),
                            jsonObject.getString("type")
                        ));

                Log.d("____________________DEBUG UPDATE AVAILABLE DEVICESдшывгфапрмзшгфыврмзщшфывомщхшофу", names.toString());

                this.adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("JSON ERROR", "AvailableDevices.update: Failed to unpack this data: " + message);
            }
        }
    }
}
