from sqlalchemy.orm import Session

from application import app
from core.utils.device_maps import DeviceTypes
from database import engine
from database.models import DeviceModel, ClimateDataModel, ChargeDataModel, SoilDataModel, ControllerDataModel


class DataHandler:
    _model_map = {
        DeviceTypes.CLIMATE: ClimateDataModel,
        DeviceTypes.POWER: ChargeDataModel,
        DeviceTypes.PLANT_WATERING: SoilDataModel,

        DeviceTypes.LIGHTING: ControllerDataModel,
        DeviceTypes.WATER_CONTROL: ControllerDataModel,
        DeviceTypes.LED: ControllerDataModel,
    }

    def get_data(self, device_name: str):
        app.logger.info(msg="Getting {} data".format(device_name))
        with Session(bind=engine) as session:
            device = session.query(DeviceModel).where(DeviceModel.name == device_name).first()

            if device is not None:
                data_model = DataHandler._model_map[device.type]
                query = session.query(data_model).where(data_model.device == device.id)

                app.logger.info(msg="Data retrieved")
                return {
                    "name": device_name,
                    "type": device.type,
                    "data": [line.__json__() for line in query]
                }

            app.logger.error(msg="Failed to retrieve data for {}".format(device_name))
            return []
