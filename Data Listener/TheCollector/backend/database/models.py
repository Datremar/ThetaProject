from json import dumps

from sqlalchemy import Column, Integer, String, ForeignKey, Float, DateTime
from sqlalchemy.orm import declarative_base

Base = declarative_base()


class DeviceModel(Base):
    __tablename__ = 'Device'

    id = Column(Integer, primary_key=True)
    name = Column(String, unique=True, nullable=False)
    type = Column(String, nullable=False)

    def __str__(self):
        return dumps({
            "device": {
                "name": self.name,
                "type": self.type
            }
        })

    def __json__(self):
        return {
            "device": {
                "name": self.name,
                "type": self.type
            }
        }


class ClimateDataModel(Base):
    __tablename__ = 'ClimateData'

    id = Column(Integer, primary_key=True)
    device = Column(Integer, ForeignKey('Device.id', ondelete='CASCADE'), nullable=False)
    humidity = Column(Float, nullable=False)
    temperature = Column(Float, nullable=False)
    date = Column(DateTime, nullable=False)

    def __str__(self):
        return dumps({
            "device": self.device,
            "data": {
                "temperature": self.temperature,
                "humidity": self.humidity
            },
            "date": self.date.__str__()
        })

    def __json__(self):
        return {
            "data": {
                "temperature": self.temperature,
                "humidity": self.humidity
            },
            "date": self.date.__str__()
        }


class ChargeDataModel(Base):
    __tablename__ = 'ChargeData'

    id = Column(Integer, primary_key=True)
    device = Column(Integer, ForeignKey('Device.id', ondelete='CASCADE'), nullable=False)
    charge = Column(Float, nullable=False)
    date = Column(DateTime, nullable=False)

    def __str__(self):
        return dumps({
            "device": self.device,
            "data": {
                "charge": self.charge
            },
            "date": self.date.__str__()
        })

    def __json__(self):
        return {
            "data": {
                "charge": self.charge
            },
            "date": self.date.__str__()
        }


class SoilDataModel(Base):
    __tablename__ = 'SoilData'

    id = Column(Integer, primary_key=True)
    device = Column(Integer, ForeignKey('Device.id', ondelete='CASCADE'), nullable=False)
    humidity = Column(Float, nullable=False)
    date = Column(DateTime, nullable=False)

    def __str__(self):
        return dumps({
            "device": self.device,
            "data": {
                "humidity": self.humidity
            },
            "date": self.date.__str__()
        })

    def __json__(self):
        return {
            "data": {
                "humidity": self.humidity
            },
            "date": self.date.__str__()
        }


class ControllerDataModel(Base):
    __tablename__ = "ControllerData"

    id = Column(Integer, primary_key=True)
    device = Column(Integer, ForeignKey('Device.id', ondelete='CASCADE'), nullable=False)
    json_data = Column(String, nullable=False)
    date = Column(DateTime, nullable=False)

    def __str__(self):
        return dumps({
            "device": self.device,
            "data": self.json_data,
            "date": self.date.__str__()
        })

    def __json__(self):
        return {
            "data": self.json_data,
            "date": self.date.__str__()
        }
