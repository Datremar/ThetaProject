import time

from climate_device import Climate
from lighting_device import Lighting
from soil_device import Soil

if __name__ == "__main__":
    d1 = Climate(client_id="/MockClimate1")
    d2 = Soil(client_id="/MockSoil1")
    d3 = Lighting(client_id="/MockLighting1")

    while True:
        start, end = time.time(), time.time()
        while 1:
            end = time.time()
            if end - start > 10:
                d1.send_data()
                d2.send_data()
                start = end
            d1.run()
            d2.run()
            d3.run()
