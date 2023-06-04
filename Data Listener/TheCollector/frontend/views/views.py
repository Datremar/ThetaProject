from flask import Response, request

from frontend.application import app
from frontend.client.client import Client
from frontend.plotters.data_plotter import Plotter
from frontend.views.utils.html_wrappers import HTMLDeviceWrap, HTMLWrap

client = Client()


@app.route("/", methods=["GET"])
def index():
    return Response(
        HTMLWrap(html='''<p><a href="http://localhost:8000/devices">Устройства</a></p>''')
    )


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


@app.route("/device", methods=["POST"])
def device_details():
    response = None

    if request.method == "POST":
        device_name = request.form["device_name"]

        plot = Plotter().plot(device_name=device_name)

        response = Response(plot.html)

    return response
