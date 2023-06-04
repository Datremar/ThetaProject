import datetime
import mpld3 as mpld

from matplotlib import pyplot as plt

from core.utils.device_maps import DeviceTypes
from frontend.client.client import Client
from frontend.views.utils.html_wrappers import HTMLLogWrap, HTMLPlotWrap, HTMLWrap


class DataPlot:
    def __init__(self, device_name):
        self.device_name = device_name
        self.client = Client()

    def make_log(self, device_type, data):
        html = HTMLLogWrap(logs=data)
        return html

    def make_figure(self, device_type, data):
        figure = None

        if device_type == DeviceTypes.CLIMATE:
            f = [
                (
                    line["data"]["temperature"],
                    line["data"]["humidity"],
                    datetime.datetime.fromisoformat(line["date"])
                )
                for line in data
            ]
            f = sorted(f, key=lambda x: x[2])

            y1 = [line[0] for line in f]
            y2 = [line[1] for line in f]
            x = [line[-1] for line in f]

            figure, axis = plt.subplots(1, 2, figsize=(10, 5))

            axis[0].set_title("Температура")
            axis[1].set_title("Влажность")

            axis[0].set_xlabel("Время")
            axis[0].set_ylabel("Температура")

            axis[1].set_xlabel("Время")
            axis[1].set_ylabel("Влажность%")

            axis[0].set_ylim(0, 100)
            axis[1].set_ylim(0, 100)

            axis[0].plot(x, y1)
            axis[1].plot(x, y2)

        elif device_type == DeviceTypes.POWER:
            f = [
                (
                    line["data"]["charge"],
                    datetime.datetime.fromisoformat(line["date"])
                )
                for line in data
            ]
            f = sorted(f, key=lambda x: x[1])

            y1 = [line[0] for line in f]
            x = [line[-1] for line in f]

            figure, axis = plt.subplots(1, 1, figsize=(10, 5))

            axis.set_title("Заряд")

            axis.set_xlabel("Время")
            axis.set_ylabel("Заряд%")

            axis.set_ylim(0, 100)
            axis.plot(x, y1)
        elif device_type == DeviceTypes.PLANT_WATERING:
            f = [
                (
                    line["data"]["humidity"],
                    datetime.datetime.fromisoformat(line["date"])
                )
                for line in data
            ]
            f = sorted(f, key=lambda x: x[1])

            y1 = [line[0] for line in f]
            x = [line[-1] for line in f]

            figure, axis = plt.subplots(1, 1, figsize=(10, 5))

            axis.set_title("Влажность почвы")

            axis.set_xlabel("Время")
            axis.set_ylabel("Влажность%")

            axis.set_ylim(0, 100)
            axis.plot(x, y1)

        return figure

    @property
    def html(self):
        html = None

        response = self.client.get_device_data(device_name=self.device_name)
        data = response["data"]
        device_type = response["type"]

        if device_type in DeviceTypes.SENSORS:
            html = HTMLPlotWrap(
                mpld.fig_to_html(
                    self.make_figure(
                        device_type=device_type,
                        data=data
                    ),
                    figid="fig1"
                ),
                device_type=device_type
            )
        elif device_type in DeviceTypes.CONTROLLERS:
            html = self.make_log(device_type, data)

        return HTMLWrap(html=html)


class Plotter:
    def __init__(self):
        self.client = Client()

    def plot(self, device_name: str):
        return DataPlot(device_name=device_name)
