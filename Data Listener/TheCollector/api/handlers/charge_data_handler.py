import datetime
from json import loads, JSONDecodeError

from sqlalchemy.orm import Session

from database import engine
from database.models import DeviceModel, ChargeDataModel


class ChargeDataHandler:
    @staticmethod
    def register_data(device_name: str, data: str):
        try:
            data = loads(data)
        except JSONDecodeError:
            print("Failed to decode charge data:", data)
            return

        try:
            charge = data["charge"]
        except KeyError:
            print("Failed to acquire necessary fields from charge data:", data)
            return

        with Session(bind=engine) as session:
            data = session.query(DeviceModel).where(DeviceModel.name == device_name)
            device = data.first()

            if not device:
                return

            charge_data = ChargeDataModel(
                device=device.id,
                charge=charge,
                date=datetime.date.today()
            )

            session.add(charge_data)
            session.commit()

        return charge_data
