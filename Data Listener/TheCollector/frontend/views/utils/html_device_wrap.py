class HTMLDeviceWrap:
    def __new__(cls, device_name: str):
        return f'<p><a href="http://127.0.0.1:8000/device/{device_name}">{device_name}</a></p>'
