package com.example.iot_smart_home.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iot_smart_home.R;
import com.example.iot_smart_home.utils.device.Device;
import com.example.iot_smart_home.utils.device.DeviceList;
import com.example.iot_smart_home.utils.mqtt.MQTTClient;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.sliders.AlphaSlideBar;

import org.json.JSONException;
import org.json.JSONObject;

public class LEDDeviceActivity extends AppCompatActivity {
    MQTTClient client = MQTTClient.INSTANCE;
    DeviceList devices = DeviceList.INSTANCE;

    Device device;

    Intent intent;
    JSONObject json;

    ColorPickerView colorPickerView;
    AlphaSlideBar alphaSlideBar;
    Button turnOffButton;

    String color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led);

        intent = getIntent();
        device = devices.get(intent.getStringExtra("device_name"));

        json = new JSONObject();

        colorPickerView = findViewById(R.id.colorPickerView);
        colorPickerView.setDebounceDuration(0);

        alphaSlideBar = findViewById(R.id.alphaSlideBar);
        colorPickerView.attachAlphaSlider(alphaSlideBar);

        turnOffButton = findViewById(R.id.turnOffButton);

        colorPickerView.setColorListener(new ColorEnvelopeListener() {
            @Override
            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                if (fromUser) {
                    color = envelope.getHexCode();
                    Log.d("COLOR PICKER DEBUG", color);

                    try {
                        json.put("type", "RGB");
                        json.put("alpha", Integer.parseInt(color.substring(0, 2), 16));
                        json.put("r", Integer.parseInt(color.substring(2, 4), 16));
                        json.put("g", Integer.parseInt(color.substring(4, 6), 16));
                        json.put("b", Integer.parseInt(color.substring(6), 16));

                        Log.d("COLOR PICKER DEBUG", "JSON color: " + json.toString());

                        client.publish(device.name, json.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        turnOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    json.put("type", "RGB");
                    json.put("r", 0);
                    json.put("g", 0);
                    json.put("b", 0);

                    client.publish(device.name, json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}