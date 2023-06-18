package com.example.iot_smart_home.utils.device;

import android.util.Log;

import com.example.iot_smart_home.utils.Function;
import com.example.iot_smart_home.utils.observer.Observer;

import org.json.JSONException;

public class Device implements Observer {
    public DeviceType type;
    public String name;
    public String data = "{}";
    private Function callback;

    public Device(String name, DeviceType type) {
        this.name = name;
        this.type = type;
    }

    public Device(AvailableDevice device) {
        this.name = device.name;
        this.type = device.type;
    }

    public void setUpdateStateCallback(Function callback) {
        this.callback = callback;
    }

    @Override
    public void update(String topic, String message) throws JSONException {
        if (topic.equals(name)) {
            data = message;
            Log.d(topic + " DATA ", data);
            this.callback.run();
        }
    }
}



