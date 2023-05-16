from json import loads

from backend.api.handlers.controller_data_handler import ControllerDataHandler
from backend.application import app
from backend.core.utils.sensor_handlers import Sensors
from backend.mqtt.client import MQTTClient

from backend.api.handlers.device_handler import DeviceHandler
from backend.core.utils.device_maps import DeviceTypes

device_handler = DeviceHandler()
controller_handler = ControllerDataHandler()

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
                app.logger.info("Connection established.")
                return

            app.logger.error("Connection failed.")

        return on_connect

    def make_disconnect_fallback(self):
        def on_disconnect(client, userdata, rc):
            if rc != 0:
                app.logger.warning("Unexpected disconnect. Trying to reconnect...")
                self.client.connect()

        return on_disconnect

    def make_message_handler(self):
        def handle_mqtt_message(client, userdata, message):
            app.logger.info(
                "Receiving topic {}, message {}".format(message.topic, message.payload.decode(encoding="utf-8"))
            )
            if message.topic == "/device_id":
                app.logger.info("Found new device")
                data = loads(message.payload.decode(encoding="utf-8"))
                devices[data["name"]] = data["type"]

                device_handler.register_device(data["name"], data["type"])

                self.client.subscribe(data["name"], qos=1)
            elif message.topic in devices:
                data = message.payload.decode(encoding="utf-8")

                if devices[message.topic] in DeviceTypes.SENSORS:
                    Sensors.handlers[devices[message.topic]].register_data(message.topic, data)
                elif devices[message.topic] in DeviceTypes.CONTROLLERS:
                    controller_handler.register_data(message.topic, data)
                else:
                    app.logger.error("Couldn't identify device")

        return handle_mqtt_message

    def run(self):
        self.client.run()
