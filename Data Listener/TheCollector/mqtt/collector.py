from json import loads

from mqtt.client import MQTTClient

from api.handlers.charge_data_handler import ChargeDataHandler
from api.handlers.climate_data_handler import ClimateDataHandler
from api.handlers.device_handler import DeviceHandler
from api.handlers.soil_data_handler import SoilDataHandler

device_handler = DeviceHandler()
climate_handler = ClimateDataHandler()
soil_handler = SoilDataHandler()
charge_handler = ChargeDataHandler()

devices = device_handler.load_devices()


class Collector:
    def __init__(self, client: MQTTClient):
        self.client = client
        self.client.on_message_received(self.make_message_handler())

        for device in devices:
            self.client.subscribe(device, qos=1)

        self.client.on_disconnect(self.make_disconnect_fallback())
        self.client.on_connection_established(self.make_connect_fallback())

        self.client.subscribe("/device_id", qos=1)

    def make_connect_fallback(self):
        def on_connect(client, userdata, flags, rc):
            if rc == 0:
                print("Connection established.")
                return

            print("Connection failed.")

        return on_connect

    def make_disconnect_fallback(self):
        def on_disconnect(client, userdata, rc):
            if rc != 0:
                print("Unexpected disconnect. Trying to reconnect...")
                self.client.connect()

        return on_disconnect

    def make_message_handler(self):
        def handle_mqtt_message(client, userdata, message):
            print(message.topic, message.payload)
            if message.topic == "/device_id":
                print("Found new device")
                data = loads(message.payload.decode(encoding="utf-8"))
                devices[data["name"]] = data["type"]

                device_handler.register_device(data["name"], data["type"])

                self.client.subscribe(data["name"], qos=1)
            elif message.topic in devices:
                data = message.payload.decode(encoding="utf-8")

                if devices[message.topic] == "Climate":
                    climate_handler.register_data(message.topic, data)
                elif devices[message.topic] == "PlantWatering":
                    soil_handler.register_data(message.topic, data)
                elif devices[message.topic] == "Power":
                    charge_handler.register_data(message.topic, data)
                else:
                    print("Couldn't identify device")

        return handle_mqtt_message

    def run(self):
        self.client.run()
