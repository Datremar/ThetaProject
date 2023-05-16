from flask import Response, request

from frontend.application import app
from frontend.client.client import Client
from frontend.plotters.climate_plotter import ClimatePlotter
from frontend.views.utils.html_device_wrap import HTMLDeviceWrap
from frontend.views.utils.html_wrap import HTMLWrap

client = Client()


@app.route("/devices", methods=["GET"])
def devices_view():
    response = None

    if request.method == "GET":
        devices = client.get_devices()["devices"]
        html = ""

        for line in devices:
            html += HTMLDeviceWrap(device_name=line["device"]["name"])

        response = Response(HTMLWrap(html=html))

    return response


@app.route("/device", methods=["GET"])
def device_details(device_name: str):
    response = None

    if request.method == "GET":
        device_data = client.get_device_data(device_name)
        print(device_data)

        plot = ClimatePlotter().plot(device_name=device_name)

        response = Response(plot.html)

    return response
