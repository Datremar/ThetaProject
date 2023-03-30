import datetime

from sqlalchemy import Column, Integer, String, ForeignKey, Float, Date, select
from sqlalchemy.orm import relationship, Session, declarative_base

from database import engine

Base = declarative_base()


class DeviceModel(Base):
    __tablename__ = 'Device'

    id = Column(Integer, primary_key=True)
    name = Column(String, unique=True, nullable=False)
    type = Column(String, nullable=False)


class ClimateDataModel(Base):
    __tablename__ = 'ClimateData'

    id = Column(Integer, primary_key=True)
    device = Column(Integer, ForeignKey('Device.id', ondelete='CASCADE'), nullable=False)
    humidity = Column(Float, nullable=False)
    temperature = Column(Float, nullable=False)

    date = Column(Date, nullable=False)


class ChargeDataModel(Base):
    __tablename__ = 'ChargeData'

    id = Column(Integer, primary_key=True)
    device = Column(Integer, ForeignKey('Device.id', ondelete='CASCADE'), nullable=False)
    charge = Column(Float, nullable=False)
    date = Column(Date, nullable=False)


class SoilDataModel(Base):
    __tablename__ = 'SoilData'

    id = Column(Integer, primary_key=True)
    device = Column(Integer, ForeignKey('Device.id', ondelete='CASCADE'), nullable=False)
    humidity = Column(Float, nullable=False)
    date = Column(Date, nullable=False)


if __name__ == "__main__":
    with Session(bind=engine) as session:
        # device = DeviceModel(
        #     name="test_device",
        #     type="test_type"
        # )

        device = session.query(DeviceModel).where(DeviceModel.name == "test_device").first()
        print(device.id, device.name, device.type)

        climate_data = ClimateDataModel(
            device=device.id,
            humidity=40.3,
            temperature=27.34,
            date=datetime.date.today().__str__()
        )

        charge_data = ChargeDataModel(
            device=device.id,
            charge=100,
            date=datetime.date.today().__str__()
        )

        soil_data = SoilDataModel(
            device=device.id,
            humidity=57.4,
            date=datetime.date.today().__str__()
        )

        session.add(climate_data)
        session.add(charge_data)
        session.add(soil_data)
        session.commit()
