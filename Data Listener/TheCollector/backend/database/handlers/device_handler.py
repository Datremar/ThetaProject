from sqlalchemy.orm import Session

from backend.application import app
from backend.database import engine
from backend.database.models import DeviceModel


class DeviceHandler:
    @property
    def all_devices(self):
        app.logger.info("Retrieving all devices")
        with Session(bind=engine) as session:
            devices = session.query(DeviceModel).all()

        return [device.__json__() for device in devices]

    def get_device(self, device_name: str):
        app.logger.info("Getting {} data".format(device_name))
        with Session(bind=engine) as session:
            device = session.query(DeviceModel).where(DeviceModel.name == device_name).first()

            return device.__json__()
