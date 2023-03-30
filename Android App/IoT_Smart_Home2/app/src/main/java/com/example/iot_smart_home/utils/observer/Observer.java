package com.example.iot_smart_home.utils.observer;

public interface Observer {
    public void update(String topic, String message);
}
