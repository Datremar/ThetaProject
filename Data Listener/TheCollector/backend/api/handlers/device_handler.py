from sqlalchemy.orm import Session

from application import app
from database import engine
from database.models import DeviceModel


class DeviceHandler:
    @staticmethod
    def register_device(name: str, device_type: str):
        app.logger.info(msg="Registering device {}, {}".format(name, device_type))
        with Session(bind=engine) as session:
            data = session.query(DeviceModel).where(DeviceModel.name == name).first()

            if not data:
                device = DeviceModel(
                    name=name,
                    type=device_type
                )

                session.add(device)
                session.commit()

                app.logger.info(msg="Device {}, {} registered".format(name, device_type))

                return device

    @staticmethod
    def load_devices() -> dict:
        app.logger.info(msg="Loading Devices")
        with Session(bind=engine) as session:
            devices = session.query(DeviceModel).all()

            data = {}

            for device in devices:
                data[device.name] = device.type

            app.logger.info(msg="Loading successful")
            return data
