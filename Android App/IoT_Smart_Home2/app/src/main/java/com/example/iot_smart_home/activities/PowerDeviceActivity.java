package com.example.iot_smart_home.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iot_smart_home.R;
import com.example.iot_smart_home.activities.utils.Updatable;
import com.example.iot_smart_home.utils.device.Device;
import com.example.iot_smart_home.utils.device.DeviceList;
import com.example.iot_smart_home.utils.mqtt.MQTTClient;

import org.json.JSONException;
import org.json.JSONObject;

public class PowerDeviceActivity extends AppCompatActivity implements Updatable {
    MQTTClient client = MQTTClient.INSTANCE;
    DeviceList devices = DeviceList.INSTANCE;

    Device device;

    Intent intent;
    JSONObject json;

    TextView charge;

    Button refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);

        refresh = findViewById(R.id.refresh_charge);
        charge = findViewById(R.id.charge);

        intent = getIntent();

        String device_name = intent.getStringExtra("device_name");
        device = devices.get(device_name);
        device.setUpdateStateCallback(this::updateState);

        requestState();

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestState();
            }
        });
    }

    @Override
    public void requestState() {
        client.publish("/node", "/update" + device.name);
    }

    @Override
    public void updateState() throws JSONException {
        try {
            json = new JSONObject(device.data);
        } catch (JSONException e) {
            Log.e("DEBUG JSON ClimateActivity", "Failed to create JSON object from string: " + device.data);
            Toast.makeText(getApplicationContext(), "Что-то пошло не так", Toast.LENGTH_LONG).show();
        }

        try {
            charge.setText(json.getString("charge") + "%");
            Toast.makeText(getApplicationContext(), "Данные успешно обновлены", Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            Log.e("DEBUG JSON ClimateActivity", "Failed to retrieve the necessary fields from JSON object");
            Toast.makeText(getApplicationContext(), "Что-то пошло не так", Toast.LENGTH_LONG).show();
        }
    }
}
