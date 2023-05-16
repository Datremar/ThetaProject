import logging

from json import loads

from flask import request
from flask_restful import Resource

from backend.application import app
from backend.database.handlers.data_handler import DataHandler
from backend.database.handlers.device_handler import DeviceHandler


class DeviceView(Resource):
    def get(self):
        app.logger.info(msg="Device View GET request")
        return {
            "devices": DeviceHandler().all_devices
        }

    def post(self):
        request_data = loads(request.data)
        app.logger.info(msg=f"Device View POST request data: {request_data}")

        handler = DataHandler()
        data = handler.get_data(request_data["device"]["name"])

        app.logger.info(msg=f"Device View POST handler data: {data}")

        return data
