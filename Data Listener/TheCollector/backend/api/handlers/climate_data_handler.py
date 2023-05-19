import datetime

from json import loads, JSONDecodeError

from sqlalchemy.orm import Session

from application import app
from database import engine
from database.models import DeviceModel, ClimateDataModel


class ClimateDataHandler:
    @staticmethod
    def register_data(device_name: str, data: str):
        app.logger.info("Registering data {} from device {}".format(data, device_name))
        try:
            data = loads(data)
        except JSONDecodeError:
            app.logger.error(msg="Failed to decode climate data {}".format(data))
            return

        try:
            humidity = data["humidity"]
            temperature = data["temperature"]
        except KeyError:
            app.logger.error(msg="Failed to acquire necessary fields from climate data: {}".format(data))
            return

        with Session(bind=engine) as session:
            data = session.query(DeviceModel).where(DeviceModel.name == device_name)
            device = data.first()

            if not device:
                return

            climate_data = ClimateDataModel(
                device=device.id,
                temperature=temperature,
                humidity=humidity,
                date=datetime.datetime.now()
            )

            session.add(climate_data)
            session.commit()

            app.logger.info(msg="Data registered")
            return climate_data
