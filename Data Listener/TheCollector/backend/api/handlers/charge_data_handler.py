import datetime

from json import loads, JSONDecodeError

from sqlalchemy.orm import Session

from backend.application import app
from backend.database import engine
from backend.database.models import DeviceModel, ChargeDataModel


class ChargeDataHandler:
    @staticmethod
    def register_data(device_name: str, data: str):
        app.logger.info(msg="Registering data {}, from device {}".format(data, device_name))
        try:
            data = loads(data)
        except JSONDecodeError:
            app.logger.error(msg="Failed to decode charge data: {}".format(data))
            return

        try:
            charge = data["charge"]
        except KeyError:
            app.logger.error("Failed to acquire necessary fields from charge data: {}".format(data))
            return

        with Session(bind=engine) as session:
            data = session.query(DeviceModel).where(DeviceModel.name == device_name)
            device = data.first()

            if not device:
                return

            charge_data = ChargeDataModel(
                device=device.id,
                charge=charge,
                date=datetime.datetime.now()
            )

            session.add(charge_data)
            session.commit()

        app.logger.info(msg="Data registered")
        return charge_data
