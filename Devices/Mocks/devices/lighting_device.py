import time
from json import dumps, loads

from devices.client import MQTTClient


class Lighting:
    TYPE = "Lighting"

    def __init__(self, client_id: str):
        self.state = False
        self.client_id = client_id
        self.client = MQTTClient(client_id=client_id)
        self.client.on_connection_established(self.make_connect_fallback())
        self.client.on_message_received(self.make_message_handler())
        self.device_id = {
            "name": client_id,
            "type": self.TYPE
        }

    @property
    def current_state(self):
        state = {}

        if self.state:
            state["state"] = "on"
        else:
            state["state"] = "off"

        return dumps(state)

    def make_connect_fallback(self):
        def on_connect(client, userdata, flags, rc):
            if rc == 0:
                print(self.client_id, "Connection established.")
                self.client.publish(topic="/device_id", message=dumps(self.device_id), qos=1)
                self.client.subscribe(topic="/node", qos=1)
                self.client.subscribe(topic="/node" + self.client_id, qos=1)

                return

            print(self.client_id, "Connection failed.")

        return on_connect

    def make_message_handler(self):
        def handle_mqtt_message(client, userdata, message):
            topic = message.topic
            mssg = message.payload.decode(encoding="utf-8")

            if topic == "/node" and mssg == "/identify":
                self.client.publish(
                    topic="/device_id",
                    message=dumps(self.device_id),
                    qos=1
                )
            elif topic == "/node" and mssg == "/update" + self.client_id:
                self.client.publish(
                    topic=self.client_id,
                    message=self.current_state,
                    qos=1
                )
            elif topic == "/node" + self.client_id:
                mssg = loads(mssg)
                if mssg["state"] == "on":
                    self.state = True
                elif mssg["state"] == "off":
                    self.state = False
                else:
                    print("Unknown command")
                print(self.state)
                self.client.publish(
                    topic=self.client_id,
                    message=self.current_state,
                    qos=1
                )

        return handle_mqtt_message

    def send_data(self):
        self.client.publish(
            message=self.current_state
        )

    def run(self):
        self.client.run()


if __name__ == "__main__":
    device = Lighting(client_id="/MockLighting1")
    start, end = time.time(), time.time()
    while 1:
        device.run()
