import datetime
from json import loads, JSONDecodeError

from sqlalchemy.orm import Session

from database import engine
from database.models import DeviceModel, SoilDataModel


class SoilDataHandler:
    @staticmethod
    def register_data(device_name: str, data: str):
        try:
            data = loads(data)
        except JSONDecodeError:
            print("Failed to decode charge data:", data)
            return

        try:
            humidity = data["humidity"]
        except KeyError:
            print("Failed to acquire necessary fields from charge data:", data)
            return

        with Session(bind=engine) as session:
            data = session.query(DeviceModel).where(DeviceModel.name == device_name)
            device = data.first()

            if not device:
                return

            soil_data = SoilDataModel(
                device=device.id,
                humidity=humidity,
                date=datetime.date.today()
            )

            session.add(soil_data)
            session.commit()

        return soil_data
