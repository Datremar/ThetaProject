from api.handlers.charge_data_handler import ChargeDataHandler
from api.handlers.climate_data_handler import ClimateDataHandler
from api.handlers.soil_data_handler import SoilDataHandler
from core.utils.device_maps import DeviceTypes


class Sensors:
    handlers = {
        DeviceTypes.CLIMATE: ClimateDataHandler(),
        DeviceTypes.POWER: ChargeDataHandler(),
        DeviceTypes.PLANT_WATERING: SoilDataHandler(),
    }
