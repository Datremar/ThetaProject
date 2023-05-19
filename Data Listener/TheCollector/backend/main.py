from flask_restful import Api

from api.views.device_view import DeviceView
from application import app
from mqtt.client import MQTTClient
from mqtt.collector import Collector


class Main:
    def __init__(self):
        self.app = app
        self.api = Api(app=self.app)
        self.collector = Collector(MQTTClient(client_id="___Collector___"))

    def resource_init(self):
        self.api.add_resource(DeviceView, "/devices")

    def run(self):
        self.resource_init()
        self.collector.run()
        self.app.run(host="localhost", port=8080, debug=False)


if __name__ == '__main__':
    Main().run()
