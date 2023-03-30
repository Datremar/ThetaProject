package com.example.iot_smart_home.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
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

public class WaterDeviceActivity extends AppCompatActivity {

    Switch waterSwitch;
    ImageView droplet;

    MQTTClient client = MQTTClient.INSTANCE;
    DeviceList devices = DeviceList.INSTANCE;

    Device device;

    Intent intent;
    JSONObject json;

    boolean on = false;

    private void requestState() {
        try {
            client.publish("/node", "/update" + device.name);
            json = new JSONObject(device.data);

            if (json.getString("state").equals("on")) {
                droplet.setVisibility(View.INVISIBLE);
                waterSwitch.setChecked(false);
            } else {
                droplet.setVisibility(View.VISIBLE);
                waterSwitch.setChecked(true);
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
        setContentView(R.layout.activity_water);

        intent = getIntent();
        device = devices.get(intent.getStringExtra("device_name"));

        waterSwitch = findViewById(R.id.waterToggle);

        droplet = findViewById(R.id.droplet_water);
        waterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    droplet.setVisibility(View.VISIBLE);
                    switchState(true);
                }else {
                    droplet.setVisibility(View.INVISIBLE);
                    switchState(false);
                }
            }
        });

        requestState();
    }
}
