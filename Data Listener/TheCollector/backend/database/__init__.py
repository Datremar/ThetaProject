from backend.database.engine import engine
from backend.database.models import Base

if __name__ == "__main__":
    Base.metadata.create_all(engine)
