package com.example.iot_smart_home.utils.mqtt;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.iot_smart_home.utils.observer.Observable;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTClient extends Observable {
    public static MQTTClient INSTANCE = null;

    private static final String broker       = "tcp://192.168.0.21:1883";
    private final String topic_device_id = "/device_id";
    private final String clientId = "IoTAppClient";
    private final MQTTClient self;
    private final MqttAndroidClient client;
    private final MqttConnectOptions options;

    private Context context;

    public static void init(Context context) {
        INSTANCE = new MQTTClient(context);

    }

    private MQTTClient(Context context) {
        this.context = context;
        self = this;
        this.client = new MqttAndroidClient(
                context,
                broker,
                clientId,
                Ack.AUTO_ACK
        );

        this.client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (reconnect) {
                    Log.d("MQTT DEBUG", "Reconnected to: " + serverURI);
                } else {
                    Log.d("MQTT DEBUG", "Connected to: " + serverURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.d("MQTT DEBUG", "The Connection was lost.");
                try {
                    client.reconnect();
                } catch (MqttException e) {
                    Log.e("ERROR", "MQTT Client failed to reconnect");
                    e.printStackTrace();
                    Toast.makeText(context, "Connection lost.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                Log.d("MQTT DEBUG", "Incoming message: " + new String(message.getPayload()));
                self.notifyObservers(topic, new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d("MQTT DEBUG", "Message delivered.");
            }
        });

        this.options = new MqttConnectOptions();
        this.options.setAutomaticReconnect(true);
        this.options.setCleanSession(false);
        this.options.setKeepAliveInterval(120);

        this.connect();
    }

    private void connect() {
        this.client.connect(this.options, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d("MQTT DEBUG","Connecting to broker: " + broker);

                DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                disconnectedBufferOptions.setBufferEnabled(true);
                disconnectedBufferOptions.setBufferSize(100);
                disconnectedBufferOptions.setPersistBuffer(false);
                disconnectedBufferOptions.setDeleteOldestMessages(false);
                client.setBufferOpts(disconnectedBufferOptions);

                subscribe(topic_device_id, 1);

                Log.d("MQTT DEBUG","Successfully connected.");
                Toast.makeText(context, "Connection established.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.d("MQTT DEBUG","Failed to connect to: " + broker);
                Toast.makeText(context, "Connection to broker failed.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean isConnected() {
        return this.client.isConnected();
    }

    public void subscribe(String topic){
        subscribe(topic, 0);
    }

    public void subscribe(String topic, int qos){
        Log.d("DEBUG_______________", "subscribe: " + topic);

        if (this.client.isConnected()) {
            Log.d("DEBUG____", "CLIENT IS CONNECTED, SUBSCRIBING");
            this.client.subscribe(topic, qos, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT DEBUG", "Subscription successful.");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("MQTT DEBUG", "Failed to subscribe.");
                }
            });

            Toast.makeText(context, "Subscribed to topic: " + topic, Toast.LENGTH_LONG).show();
        }
    }

    public void unsubscribe(String topic) {
        this.client.unsubscribe(topic);
        Toast.makeText(context, "Unsubscribed from topic: " + topic, Toast.LENGTH_LONG).show();
    }

    public void publish(String topic, String message){
        if (!this.client.isConnected()) {
            Toast.makeText(context, "No connection to broker.", Toast.LENGTH_SHORT).show();
            return;
        }

        MqttMessage mssg = new MqttMessage();
        mssg.setPayload(message.getBytes());

        this.client.publish(topic, mssg);

        Log.d("MQTT DEBUG","Message published.");

        if(!this.client.isConnected()){
            Log.d("MQTT DEBUG",this.client.getBufferedMessageCount() + " messages in buffer.");
            Toast.makeText(context, "Failed to publish on topic: " + topic, Toast.LENGTH_LONG).show();
        }
    }

    public void disconnect() {
        Log.d("MQTT DEBUG","Disconnecting...");

        this.client.disconnect();

        Log.d("MQTT DEBUG","Disconnected successfully.");
    }
}
