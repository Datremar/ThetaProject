from typing import List
from core.utils.device_maps import DeviceTypes

_CLIMATE_HTML = '''
<style type="text/css">
    div#fig1 {{ text-align: center }}
</style>
<style type="text/css">
    div#fig2 {{ text-align: center }}
</style>
{}
'''

_SENSOR_HTML = '''
<style type="text/css">
    div#fig1 {{ text-align: center }}
</style>
{}
'''

_LOG_LINE = '''<p>{}</p>'''


class HTMLWrap:
    def __new__(cls, html: str) -> str:
        return '''
<DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
    </head>
    <body>
        <script>
                var redirect = function(device_name) {{
                    var form = document.createElement('form');
                    form.method = "post";
                    form.action = "http://localhost:8000/device";

                    var device_name_field = document.createElement('INPUT');
                    device_name_field.type = "HIDDEN";
                    device_name_field.name = "device_name";
                    device_name_field.value = device_name;
                    document.body.appendChild(form);

                    form.appendChild(device_name_field);
                    form.submit();
                }};
            </script>
        ''' + html + '''
    </body>
</html>
'''


class HTMLDeviceWrap:
    def __new__(cls, device_name: str):
        return '''
        <p>
            <a onclick="redirect('{}');">{}</a>
        </p>
        '''.format(device_name, device_name)


class HTMLPlotWrap:
    def __new__(cls, plot_html, device_type):
        if device_type == DeviceTypes.CLIMATE:
            return _CLIMATE_HTML.format(plot_html)
        elif device_type in DeviceTypes.SENSORS:
            return _SENSOR_HTML.format(plot_html)


class HTMLLogWrap:
    def __new__(cls, logs: List[str]) -> str:
        html = ""

        for log in logs:
            html += _LOG_LINE.format(log)

        return html
