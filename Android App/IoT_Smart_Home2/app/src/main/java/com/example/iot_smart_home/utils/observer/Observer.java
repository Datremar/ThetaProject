package com.example.iot_smart_home.utils.observer;

import org.json.JSONException;

public interface Observer {
    public void update(String topic, String message) throws JSONException;
}
