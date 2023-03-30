package com.example.iot_smart_home.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iot_smart_home.R;
import com.example.iot_smart_home.utils.device.AvailableDevice;
import com.example.iot_smart_home.utils.device.AvailableDevices;
import com.example.iot_smart_home.utils.device.Device;
import com.example.iot_smart_home.utils.device.DeviceList;
import com.example.iot_smart_home.utils.device.DeviceType;
import com.example.iot_smart_home.utils.device.DeviceNameTypeMap;
import com.example.iot_smart_home.utils.mqtt.MQTTClient;

public class AddDeviceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    AvailableDevices availableDevices;
    AvailableDevice deviceSelected = null;

    MQTTClient client = MQTTClient.INSTANCE;

    Button addDevice;
    DeviceList devices;
    Spinner choices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device);

        availableDevices = AvailableDevices.INSTANCE;
        choices = findViewById(R.id.deviceTypeChoices);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, availableDevices.getDeviceNames());
        availableDevices.init(adapter);

        choices.setAdapter(adapter);
        choices.setOnItemSelectedListener(this);

        devices = DeviceList.INSTANCE;

        addDevice = findViewById(R.id.add_new_device);
        addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deviceSelected == null) {
                    Toast.makeText(getApplicationContext(), "No device was selected.", Toast.LENGTH_LONG).show();
                    return;
                }
                devices.add(new Device(deviceSelected));
                finish();
            }
        });

        client.subscribeObserver(availableDevices);

        if (client.isConnected()) {
            client.publish("/node", "/identify");
        } else {
            Toast.makeText(getApplicationContext(), "No connection to the broker. Try again later.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {
            deviceSelected = availableDevices.getDevice((String) parent.getItemAtPosition(position));
        } catch (IllegalArgumentException e) {
            Log.e("ERROR", "No such device is registered in AvailableDevices.");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        ;
    }
}
