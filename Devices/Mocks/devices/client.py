from paho.mqtt import client as clt


class MQTTClient:
    def __init__(self, client_id: str):
        self.client_id = client_id
        self.client = clt.Client(client_id=client_id)

        self.connect()

    def on_connection_established(self, func):
        self.client.on_connect = func

    def on_message_received(self, func):
        self.client.on_message = func

    def on_disconnect(self, func):
        self.client.on_disconnect = func

    def publish(self, message: str, topic=None, qos=0):
        print(self.client_id, "publishing topic:", topic if topic is not None else self.client_id, "message:", message)
        self.client.publish(
            topic=self.client_id if not topic else topic,
            payload=message,
            qos=qos
        )

    def subscribe(self, topic, qos=0):
        self.client.subscribe(
            topic=topic,
            qos=qos
        )

    def reconnect(self):
        self.client.reconnect()

    def connect(self):
        try:
            self.client.connect(
                host="test.mosquitto.org",
                port=1883
            )
        except ConnectionRefusedError:
            print("Connection failed.")
            self.connect()

    def run(self):
        self.client.loop_start()

    def run_forever(self):
        self.client.loop_forever()

    def stop(self):
        self.client.loop_stop()
