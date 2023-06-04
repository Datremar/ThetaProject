import time
from json import dumps
from random import randint

from devices.client import MQTTClient


class Charge:
    TYPE = "Power"

    def __init__(self, client_id: str):
        self.client_id = client_id
        self.client = MQTTClient(client_id=client_id)
        self.client.on_connection_established(self.make_connect_fallback())
        self.client.on_message_received(self.make_message_handler())
        self.device_id = {
            "name": client_id,
            "type": self.TYPE
        }

    def make_connect_fallback(self):
        def on_connect(client, userdata, flags, rc):
            if rc == 0:
                print(self.client_id, "Connection established.")
                self.client.publish(topic="/device_id", message=dumps(self.device_id), qos=1)
                self.client.subscribe(topic="/node", qos=1)

                return

            print(self.client_id,"Connection failed.")

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
                    message=dumps({
                        "charge": randint(0, 100)
                    }),
                    qos=1
                )

        return handle_mqtt_message

    def send_data(self):
        self.client.publish(
            message=dumps({
                "charge": randint(0, 100)
            })
        )

    def run(self):
        self.client.run()


if __name__ == "__main__":
    device = Charge(client_id="/MockCharge1")
    start, end = time.time(), time.time()
    while 1:
        end = time.time()
        if end - start > 10:
            device.send_data()
            start = end
        device.run()
