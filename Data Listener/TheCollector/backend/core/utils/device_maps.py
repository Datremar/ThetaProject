class DeviceTypes:
    CLIMATE = "Climate"
    PLANT_WATERING = "PlantWatering"
    POWER = "Power"
    LIGHTING = "Lighting"
    WATER_CONTROL = "WaterControl"
    LED = "LED"
    GENERIC = "Generic"

    CONTROLLERS = {LIGHTING, WATER_CONTROL, LED}
    SENSORS = {CLIMATE, PLANT_WATERING, POWER}
