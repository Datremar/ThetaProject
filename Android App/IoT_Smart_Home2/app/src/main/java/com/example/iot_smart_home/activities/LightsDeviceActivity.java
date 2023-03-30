package com.example.iot_smart_home.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iot_smart_home.R;
import com.example.iot_smart_home.utils.device.Device;
import com.example.iot_smart_home.utils.device.DeviceList;
import com.example.iot_smart_home.utils.mqtt.MQTTClient;

import org.json.JSONException;
import org.json.JSONObject;

public class LightsDeviceActivity extends AppCompatActivity {
    MQTTClient client = MQTTClient.INSTANCE;
    DeviceList devices = DeviceList.INSTANCE;

    Device device;

    Intent intent;
    JSONObject json;

    Switch lightSwitch;
    ImageView lamp;

    boolean on = false;

    private void requestState() {
        try {
            client.publish("/node", "/update" + device.name);
            json = new JSONObject(device.data);

            if (json.getString("state").equals("on")) {
                lamp.setVisibility(View.INVISIBLE);
                lightSwitch.setChecked(false);
            } else {
                lamp.setVisibility(View.VISIBLE);
                lightSwitch.setChecked(true);
            }
        } catch (JSONException e) {
            Log.e("ERROR LightsActivity JSON", "Failed to unpack data: " + device.data + " from device: " + device.name);
        }
    }

    private void switchState(boolean state) {
        on = state;
        json = new JSONObject();

        if (on) {
            try {
                json.put("state", "on");
            } catch (JSONException e) {
                Log.e("ERROR LightsActivity JSON", "Couldn't package the data.");
            }
        } else {
            try {
                json.put("state", "off");
            } catch (JSONException e) {
                Log.e("ERROR LightsActivity JSON", "Couldn't package the data.");
            }
        }

        client.publish(device.name, json.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lights);

        intent = getIntent();
        device = devices.get(intent.getStringExtra("device_name"));

        lightSwitch = findViewById(R.id.lightSwitch);

        lamp = findViewById(R.id.lamp_on);
        lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    lamp.setVisibility(View.VISIBLE);
                    switchState(true);
                }else {
                    lamp.setVisibility(View.INVISIBLE);
                    switchState(false);
                }
            }
        });

        requestState();
    }
}
