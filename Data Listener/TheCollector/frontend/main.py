from views.views import *


class Main:
    def run(self):
        app.run(host="localhost", port=8000)


if __name__ == "__main__":
    Main().run()
