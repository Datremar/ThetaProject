package com.example.iot_smart_home.utils.observer;

import android.util.Log;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Observable {
    private List<Observer> observers;

    public Observable() {
        this.observers = new ArrayList<>();
    }

    public void subscribeObserver(Observer observer) {
        this.observers.add(observer);
    }

    public void unsubscribeObserver(Observer observer) {
        this.observers.remove(observer);
    }

    public void notifyObservers(String topic, String message) throws JSONException {
        Log.d("MQTT DEBUG", "Notifying observers.");

        for (Observer observer: this.observers) {
            observer.update(topic, message);
        }

        Log.d("MQTT DEBUG", "Done notifying");
    }
}