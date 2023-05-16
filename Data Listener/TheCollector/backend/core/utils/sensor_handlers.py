from backend.api.handlers.charge_data_handler import ChargeDataHandler
from backend.api.handlers.climate_data_handler import ClimateDataHandler
from backend.api.handlers.soil_data_handler import SoilDataHandler
from backend.core.utils.device_maps import DeviceTypes


class Sensors:
    handlers = {
        DeviceTypes.CLIMATE: ClimateDataHandler(),
        DeviceTypes.POWER: ChargeDataHandler(),
        DeviceTypes.PLANT_WATERING: SoilDataHandler(),
    }
