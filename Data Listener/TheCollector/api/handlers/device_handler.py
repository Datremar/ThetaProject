from sqlalchemy.orm import Session

from database import engine
from database.models import DeviceModel


class DeviceHandler:
    @staticmethod
    def register_device(name: str, device_type: str):
        print("Registering device:", name, device_type)
        with Session(bind=engine) as session:
            data = session.query(DeviceModel).where(DeviceModel.name == name).first()

            if not data:
                device = DeviceModel(
                    name=name,
                    type=device_type
                )

                session.add(device)
                session.commit()

                return device

    @staticmethod
    def load_devices() -> dict:
        with Session(bind=engine) as session:
            devices = session.query(DeviceModel).all()

            data = {}

            for device in devices:
                data[device.name] = device.type

            return data
