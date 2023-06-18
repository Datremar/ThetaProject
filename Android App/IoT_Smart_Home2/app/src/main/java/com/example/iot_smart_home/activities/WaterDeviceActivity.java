package com.example.iot_smart_home.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iot_smart_home.R;
import com.example.iot_smart_home.activities.utils.Updatable;
import com.example.iot_smart_home.utils.device.Device;
import com.example.iot_smart_home.utils.device.DeviceList;
import com.example.iot_smart_home.utils.mqtt.MQTTClient;

import org.json.JSONException;
import org.json.JSONObject;

public class WaterDeviceActivity extends AppCompatActivity implements Updatable {

    Switch waterSwitch;
    ImageView droplet;

    MQTTClient client = MQTTClient.INSTANCE;
    DeviceList devices = DeviceList.INSTANCE;

    Device device;

    Intent intent;
    JSONObject json;

    boolean on = false;

    public void requestState() {
        client.publish("/node", "/update" + device.name);
    }

    public void updateState() throws JSONException {
        Log.d("DEBUG UPDATE STATE", "UPDATING STATE");
        try {
            json = new JSONObject(device.data);

            if (json.getString("state").equals("on")) {
                Toast.makeText(getApplicationContext(), "Устройство включено", Toast.LENGTH_LONG).show();
                droplet.setVisibility(View.VISIBLE);
                waterSwitch.setChecked(true);
            } else {
                Toast.makeText(getApplicationContext(), "Устройство выключено", Toast.LENGTH_LONG).show();
                droplet.setVisibility(View.INVISIBLE);
                waterSwitch.setChecked(false);
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Что-то пошло не так...", Toast.LENGTH_LONG).show();
            Log.e("ERROR LightsActivity JSON", "Failed to unpack data: " + device.data + " from device: " + device.name);
        }
    }

    private void switchState(boolean state) {
        on = state;
        json = new JSONObject();

        if (on) {
            try {
                json.put("state", "on");
                Toast.makeText(getApplicationContext(), "Включаю", Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                Log.e("ERROR LightsActivity JSON", "Couldn't package the data.");
                Toast.makeText(getApplicationContext(), "Что-то пошло не так", Toast.LENGTH_LONG).show();
            }
        } else {
            try {
                json.put("state", "off");
                Toast.makeText(getApplicationContext(), "Выключаю", Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                Log.e("ERROR LightsActivity JSON", "Couldn't package the data.");
                Toast.makeText(getApplicationContext(), "Что-то пошло не так", Toast.LENGTH_LONG).show();
            }
        }

        client.publish("/node" + device.name, json.toString());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);

        intent = getIntent();
        device = devices.get(intent.getStringExtra("device_name"));
        device.setUpdateStateCallback(this::updateState);

        waterSwitch = findViewById(R.id.waterToggle);

        droplet = findViewById(R.id.droplet_water);
        waterSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchState(waterSwitch.isChecked());
            }
        });

        requestState();
    }
}
