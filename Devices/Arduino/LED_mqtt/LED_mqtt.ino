#include "ESP8266WiFi.h"
#include "ESP8266mDNS.h"
#include "Adafruit_NeoPixel.h"
#include <WiFiUdp.h>
#include <ArduinoOTA.h>
#include "EspMQTTClient.h"
#include <ArduinoJson.h>

#define STASSID "Etaj2"
#define STAPSK  "bX4ekLMJ"
#define HOST_IP "192.168.0.21"
#define CLIENT_NAME "/LEDDevice1"

#define DEVICE_TYPE "LED"

#define ESP_BUILTIN_LED 2

#define LED_COUNT 120
#define LED_PIN 4


const char* ssid = STASSID;
const char* password = STAPSK;


Adafruit_NeoPixel strip(LED_COUNT, LED_PIN, NEO_GRB + NEO_KHZ800);


EspMQTTClient client(
  STASSID,
  STAPSK,
  HOST_IP,    
  CLIENT_NAME
);


DynamicJsonDocument doc(1024);

String json;

unsigned long system_time = millis();
unsigned long step_time = millis();
int t = 0;
bool is_dynamic = false;
int dynamic_type = -1;
int dynamic_type_speed = 0;


void handle_mode(const String& mode);
void set_uniform_color(int r, int g, int b);
void glow_red();
void glow_green();
void glow_blue();
void go_black();
void rainbow(int x);
void custom_color(int r, int g, int b);


void setup() {
  Serial.begin(115200);
  Serial.println("Booting...");

  client.setKeepAlive(1000);
  client.enableDebuggingMessages();
  client.enableOTA();
  client.enableLastWillMessage(CLIENT_NAME, "I am going offline");

  strip.begin();
}


void handle_dynamic_mode(const String& mode) {
  if (mode == "rainbow") {
    is_dynamic = true;
    dynamic_type = 1;
  }
}


void handle_led_data(JsonObject& data) {
  String type = data["type"];
  Serial.println(type);


  if (type == "RGB") {
    is_dynamic = false;
    dynamic_type = -1;
    int r = 0, g = 0, b = 0, alpha = 255;

    if (data.containsKey("alpha")) {
      alpha = data["alpha"];
    }

    r = data["r"];
    g = data["g"];
    b = data["b"];

    custom_color_alpha(r, g, b, alpha);
  }

  if (type == "MODE") {
    String mode = data["mode"];
    dynamic_type_speed = data["speed"];

    handle_dynamic_mode(mode);
  }
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

void onLEDCommandRecieved(const String& topic, const String& message) {
  Serial.println(topic + ": " + message);

  deserializeJson(doc, message);
  JsonObject data = doc.as<JsonObject>();

  handle_led_data(data);
}


void onConnectionEstablished() {
  client.publish(CLIENT_NAME, "ONLINE");
  client.subscribe("/node", onIDCommandRecieved, 1);
  client.subscribe(String(CLIENT_NAME), onLEDCommandRecieved, 1);
  pinMode(ESP_BUILTIN_LED, OUTPUT);
}


void main_exec() {
  if (is_dynamic) {
    if (dynamic_type == 1) {
      rainbow(dynamic_type_speed);
    }
  }
}


void loop() {
  main_exec();

  client.loop();
  ArduinoOTA.handle();
}


void set_uniform_color(int r, int g, int b) {
  Serial.println("Setting Color");

  for (int i = 0; i < LED_COUNT; i++) {
    strip.setPixelColor(i, strip.Color(r, g, b));
  }
  strip.show();
}


void set_uniform_color_alpha(int r, int g, int b, int alpha) {
  Serial.println("Setting Color");

  for (int i = 0; i < LED_COUNT; i++) {
    strip.setPixelColor(i, strip.Color(r, g, b));
  }

  strip.setBrightness(alpha);
  strip.show();
}


void custom_color(int r, int g, int b) {
  Serial.println("custom_color called");
  set_uniform_color(r, g, b);
}

void custom_color_alpha(int r, int g, int b, int alpha) {
  Serial.println("custom_color called");
  set_uniform_color_alpha(r, g, b, alpha);
}

void rainbow(int x) {
  if (millis() - step_time > x) {
    int pixelHue;

    t++;
    t = t % 256;

    for (int i = 0; i < strip.numPixels(); i++) {
      pixelHue = t * 256L + (i * 65536L / strip.numPixels());
      strip.setPixelColor(i, strip.gamma32(strip.ColorHSV(pixelHue)));
    }

    strip.show();

    step_time = millis();
  }
}

