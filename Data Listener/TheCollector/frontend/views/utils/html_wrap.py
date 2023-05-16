class HTMLWrap:
    def __new__(cls, html: str) -> str:
        return '''<DOCTYPE html>
        <html>
        <head>
        <meta charset="utf-8">
        </head>
        <body>''' + html + '''</body>
    </html>'''