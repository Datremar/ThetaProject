import datetime
from json import loads

import mpld3 as mpld

from matplotlib import pyplot as plt

from frontend.client.client import Client
from frontend.views.utils.html_wrap import HTMLWrap


class ClimatePlot:
    def __init__(self, device_name):
        self.device_name = device_name
        self.client = Client()

    @property
    def html(self):
        data = self.client.get_device_data(device_name=self.device_name)["data"]

        f = [
            (
                line["data"]["temperature"],
                line["data"]["humidity"],
                datetime.datetime.fromisoformat(line["date"])
            )
            for line in data
        ]
        f = sorted(f, key=lambda x: x[2])

        x = [line[2] for line in f]
        y = [(line[0], line[1]) for line in f]

        fig = plt.plot(x, y)[0].figure
        html = mpld.fig_to_html(fig)

        return HTMLWrap(html=html)


class ClimatePlotter:
    def __init__(self):
        self.client = Client()

    def plot(self, device_name: str):
        return ClimatePlot(device_name=device_name)


if __name__ == "__main__":
    plot = ClimatePlot(device_name="/MockClimate1")
    plot.html

    # fig = plt.plot([1, 2, 3, 4], [4, 5, 7, 1])
    # # plt.show()
    # print(type(fig[0]))
    # print(fig[0].figure)
    #
    # print(mpld.fig_to_html(fig[0].figure))
