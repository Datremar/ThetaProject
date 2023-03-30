import datetime
from json import loads, JSONDecodeError

from sqlalchemy.orm import Session

from database import engine
from database.models import DeviceModel, ClimateDataModel


class ClimateDataHandler:

    @staticmethod
    def register_data(device_name: str, data: str):
        try:
            data = loads(data)
        except JSONDecodeError:
            print("Failed to decode climate data:", data)
            return

        try:
            humidity = data["humidity"]
            temperature = data["temperature"]
        except KeyError:
            print("Failed to acquire necessary fields from climate data:", data)
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
                date=datetime.date.today()
            )

            session.add(climate_data)
            session.commit()

            return climate_data
