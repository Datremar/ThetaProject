from json import loads

from requests import get, post


class Client:
    def get_devices(self):
        response = get("http://localhost:8080/devices").json()
        return response

    def get_device_data(self, device_name: str):
        return post("http://localhost:8080/devices", json={
            "device": {
                "name": device_name
            }
        }).json()


if __name__ == "__main__":
    client = Client()
    print(client.get_devices())
    # print(client.get_device_data(device_name="/LEDDevice1"))
