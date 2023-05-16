import datetime
import logging
from json import loads, JSONDecodeError

from sqlalchemy.orm import Session

from backend.application import app
from backend.database import engine
from backend.database.models import DeviceModel, SoilDataModel


class SoilDataHandler:
    @staticmethod
    def register_data(device_name: str, data: str):
        app.logger.info(msg="Registering data {}, from device {}".format(data, device_name))
        try:
            data = loads(data)
        except JSONDecodeError:
            app.logger.error(msg="Failed to decode soil data: {}".format(data))
            return

        try:
            humidity = data["humidity"]
        except KeyError:
            app.logger.error("Failed to acquire necessary fields from soil data: {}".format(data))
            return

        with Session(bind=engine) as session:
            data = session.query(DeviceModel).where(DeviceModel.name == device_name)
            device = data.first()

            if not device:
                return

            soil_data = SoilDataModel(
                device=device.id,
                humidity=humidity,
                date=datetime.datetime.now()
            )

            session.add(soil_data)
            session.commit()

        app.logger.info(msg="Data registered")
        return soil_data
