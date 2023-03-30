from flask import Flask

from mqtt.client import MQTTClient
from mqtt.collector import Collector

app = Flask(__name__)

collector = Collector(MQTTClient(client_id="___Collector___"))

if __name__ == '__main__':
    collector.run()
    app.run(host="localhost", port=8080)
