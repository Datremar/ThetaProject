package com.example.iot_smart_home.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iot_smart_home.R;
import com.example.iot_smart_home.utils.device.Device;
import com.example.iot_smart_home.utils.device.DeviceList;
import com.example.iot_smart_home.utils.mqtt.MQTTClient;

import org.json.JSONException;
import org.json.JSONObject;

public class ClimateDeviceActivity extends AppCompatActivity {
    MQTTClient client = MQTTClient.INSTANCE;
    DeviceList devices = DeviceList.INSTANCE;

    Device device;

    Intent intent;
    JSONObject json;

    TextView temperature;
    TextView humidity;

    Button refresh;

    private void refreshView(Device device) {
        client.publish("/node", "/update" + device.name);

        try {
            json = new JSONObject(device.data);
        } catch (JSONException e) {
            Log.e("DEBUG JSON ClimateActivity", "Failed to create JSON object from string: " + device.data);
        }

        try {
            temperature.setText(json.getString("temperature") + "C");
            humidity.setText(json.getString("humidity") + "%");
        } catch (JSONException e) {
            Log.e("DEBUG JSON ClimateActivity", "Failed to retrieve the necessary fields from JSON object");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climate);

        refresh = findViewById(R.id.refresh_climate);
        temperature = findViewById(R.id.temperature);
        humidity = findViewById(R.id.humidity);

        intent = getIntent();

        String device_name = intent.getStringExtra("device_name");
        device = devices.get(device_name);

        refreshView(device);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshView(device);
            }
        });
    }
}
