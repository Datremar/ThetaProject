package com.example.iot_smart_home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_smart_home.activities.AddDeviceActivity;
import com.example.iot_smart_home.activities.ClimateDeviceActivity;
import com.example.iot_smart_home.activities.GenericDeviceActivity;
import com.example.iot_smart_home.activities.LEDDeviceActivity;
import com.example.iot_smart_home.activities.LightsDeviceActivity;
import com.example.iot_smart_home.activities.PlantDeviceActivity;
import com.example.iot_smart_home.activities.PowerDeviceActivity;
import com.example.iot_smart_home.activities.WaterDeviceActivity;
import com.example.iot_smart_home.database.DeviceDBHelper;
import com.example.iot_smart_home.utils.device.Device;
import com.example.iot_smart_home.utils.device.DeviceList;
import com.example.iot_smart_home.utils.device.DeviceType;
import com.example.iot_smart_home.utils.mqtt.MQTTClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements DeviceRecyclerViewAdapter.ItemClickListener {

    Toolbar toolbar;
    DeviceRecyclerViewAdapter adapter;
    DeviceDBHelper db;
    MQTTClient client;

    FloatingActionButton addDevice;

    static Intent selectClimate;
    static Intent selectPower;
    static Intent selectGeneric;
    static Intent selectPlant;
    static Intent selectLED;
    static Intent selectLights;
    static Intent selectWater;
    static Intent selectAddDevice;

    DeviceList devices;

    private Map<DeviceType, Intent> typeIntentMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DeviceDBHelper(getApplicationContext());
        MQTTClient.init(getApplicationContext());
        client = MQTTClient.INSTANCE;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Умный Дом");

        devices = DeviceList.INSTANCE;

        RecyclerView recyclerView = findViewById(R.id.device_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeviceRecyclerViewAdapter(this, devices.getArray());
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        devices.init(adapter, db, client);
        devices.loadData();

        selectClimate = new Intent(MainActivity.this, ClimateDeviceActivity.class);
        selectPower = new Intent(MainActivity.this, PowerDeviceActivity.class);
        selectPlant = new Intent(MainActivity.this, PlantDeviceActivity.class);
        selectLED = new Intent(MainActivity.this, LEDDeviceActivity.class);
        selectLights = new Intent(MainActivity.this, LightsDeviceActivity.class);
        selectWater = new Intent(MainActivity.this, WaterDeviceActivity.class);
        selectGeneric = new Intent(MainActivity.this, GenericDeviceActivity.class);
        selectAddDevice = new Intent(MainActivity.this, AddDeviceActivity.class);

        typeIntentMap.put(DeviceType.GENERIC, MainActivity.selectGeneric);
        typeIntentMap.put(DeviceType.CLIMATE_GATHERING, MainActivity.selectClimate);
        typeIntentMap.put(DeviceType.LED, MainActivity.selectLED);
        typeIntentMap.put(DeviceType.LIGHTING, MainActivity.selectLights);
        typeIntentMap.put(DeviceType.PLANT_WATERING, MainActivity.selectPlant);
        typeIntentMap.put(DeviceType.POWER, MainActivity.selectPower);
        typeIntentMap.put(DeviceType.WATER_CONTROL, MainActivity.selectWater);


        addDevice = findViewById(R.id.add_device_button);
        addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.startActivity(selectAddDevice);
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Device device = adapter.getItem(position);
        Intent intent = typeIntentMap.get(device.type);

        Log.d("DEBUG onItemClick MainActivity", "device name: " + device.name + " device type: " + device.type);

        intent.putExtra("device_name", device.name);
        MainActivity.this.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        db.close();
        client.disconnect();
        super.onDestroy();
    }
}