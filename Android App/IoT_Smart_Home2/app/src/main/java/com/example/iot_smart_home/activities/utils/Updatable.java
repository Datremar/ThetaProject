package com.example.iot_smart_home.activities.utils;

import org.json.JSONException;

public interface Updatable {
    void requestState();
    void updateState() throws JSONException;
}
