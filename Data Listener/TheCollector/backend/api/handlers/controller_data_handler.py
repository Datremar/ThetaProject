import datetime

from sqlalchemy.orm import Session

from application import app
from database import engine
from database.models import DeviceModel, ControllerDataModel


class ControllerDataHandler:
    @staticmethod
    def register_data(device_name: str, data: str):
        app.logger.info(msg="Registering data {} from device {}".format(data, device_name))
        with Session(bind=engine) as session:
            device = session.query(DeviceModel).where(DeviceModel.name == device_name)
            device = device.first()

            if not device:
                return

            controller_data = ControllerDataModel(
                device=device.id,
                json_data=data,
                date=datetime.datetime.now()
            )

            session.add(controller_data)
            session.commit()

        app.logger.info("Data registered")
        return controller_data
