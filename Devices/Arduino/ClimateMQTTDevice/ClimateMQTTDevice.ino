#include "ESP8266WiFi.h"
#include "ESP8266mDNS.h"
#include <WiFiUdp.h>
#include <ArduinoOTA.h>
#include "EspMQTTClient.h"
#include <ArduinoJson.h>
#include <Adafruit_AHTX0.h>

#define STASSID "Etaj2"
#define STAPSK  "bX4ekLMJ"
#define HOST_IP "192.168.0.21"
#define CLIENT_NAME "/ClimateMonitor1"

#define DEVICE_TYPE "Climate"

#define ESP_BUILTIN_LED 2


const char* ssid = STASSID;
const char* password = STAPSK;


unsigned long start_t = 0;
unsigned long now_t = 0;


String json;

Adafruit_AHTX0 aht;
sensors_event_t humidity, temp;

EspMQTTClient client(
  STASSID,
  STAPSK,
  HOST_IP,   
  CLIENT_NAME
);


DynamicJsonDocument doc(1024);

void setup() {
  Serial.begin(115200);
  Serial.println("Booting...");
  
  if (!aht.begin()) {
    Serial.println("Could not find AHT? Check wiring");
    while (1) delay(10);
  }
  Serial.println("AHT10 or AHT20 found");

  client.setKeepAlive(1000);
  client.enableDebuggingMessages();
  client.enableOTA();
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
  } else if (topic == "/node" && message == "/update" + String(CLIENT_NAME)) {
    aht.getEvent(&humidity, &temp);

    Serial.print("Temperature: "); Serial.println(temp.temperature);
    Serial.print("Humidity: "); Serial.println(humidity.relative_humidity);

    doc["temperature"] = temp.temperature;
    doc["humidity"] = humidity.relative_humidity;
    
    serializeJson(doc, json);

    client.publish(CLIENT_NAME, json);

    doc.clear();
    start_t = millis();
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

  if (now_t - start_t > 10000) {
    aht.getEvent(&humidity, &temp);

    Serial.print("Temperature: "); Serial.println(temp.temperature);
    Serial.print("Humidity: "); Serial.println(humidity.relative_humidity);

    doc["temperature"] = temp.temperature;
    doc["humidity"] = humidity.relative_humidity;
    
    serializeJson(doc, json);

    client.publish(CLIENT_NAME, json);

    doc.clear();
    start_t = millis();
    json = "";
  }

  client.loop();
  ArduinoOTA.handle();
}

