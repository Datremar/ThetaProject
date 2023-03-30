#include "ESP8266WiFi.h"
#include "ESP8266mDNS.h"
#include <WiFiUdp.h>
#include <ArduinoOTA.h>
#include "EspMQTTClient.h"
#include <ArduinoJson.h>

#define STASSID "Etaj2"
#define STAPSK  "bX4ekLMJ"
#define HOST_IP "192.168.0.21"
#define CLIENT_NAME "/WateringDevice1"

#define DEVICE_TYPE "WaterControl"

#define ESP_BUILTIN_LED 2
#define RELAY_IN 14


const char* ssid = STASSID;
const char* password = STAPSK;

String json;


struct State {
  bool on;
};

State state;


EspMQTTClient client(
  STASSID,
  STAPSK,
  HOST_IP,   
  CLIENT_NAME
);

DynamicJsonDocument doc(1024);


unsigned long start_t = 0;
unsigned long now_t = 0;


void setup() {
  Serial.begin(115200);
  Serial.println("Booting...");

  state.on = false;

  client.setKeepAlive(1000);
  client.enableDebuggingMessages();
  client.enableOTA();

  pinMode(RELAY_IN, OUTPUT);
}


void onIDCommandRecieved(const String& topic, const String& message) {
  Serial.println(topic + ": " + message);

  if (topic == "/node" && message == "/identify") {
    doc["name"] = CLIENT_NAME;
    doc["type"] = DEVICE_TYPE;
    serializeJson(doc, json);

    client.publish("/device_id", json);
    doc.clear();
    json = "";
  }
}


void onStateCommandRecieved(const String& topic, const String& message) {
  Serial.println(topic + ": " + message);
  if (topic == CLIENT_NAME) {
    deserializeJson(doc, message);

    String s = doc["state"];

    if (s == "on") {
      state.on = true;
    } else if (s == "off") {
      state.on = false;
    }

    doc.clear();
    json = "";
  } else if (topic == "/node" && message == "/update" + String(CLIENT_NAME)) {
    if (state.on) {
      doc["state"] = "on";
    } else {
      doc["state"] = "off";
    }
    
    serializeJson(doc, json);

    client.publish("/device_id", json);
    doc.clear();
    json = "";
  }
}


void onConnectionEstablished() {
  doc["name"] = CLIENT_NAME;
  doc["type"] = DEVICE_TYPE;
  serializeJson(doc, json);

  client.publish("/device_id", json);
  doc.clear();
  json = "";

  client.subscribe(CLIENT_NAME, onStateCommandRecieved, 1);
  client.subscribe("/node", onIDCommandRecieved, 1);
  pinMode(ESP_BUILTIN_LED, OUTPUT);
}


void loop() {
  if (client.isConnected()) {
    pinMode(ESP_BUILTIN_LED, OUTPUT);
  } else {
    pinMode(ESP_BUILTIN_LED, INPUT);
  }

  now_t = millis();

  if (now_t - start_t > 1000) {
    Serial.print("State: "); Serial.println(state.on);

    start_t = millis();
  }

  if (state.on) {
    digitalWrite(RELAY_IN, LOW);
  } else {
    digitalWrite(RELAY_IN, HIGH);
  }

  client.loop();
  ArduinoOTA.handle();
}

