from sqlalchemy import create_engine

engine = create_engine('postgresql+psycopg2://postgres:testpassword@127.0.0.1:5432/collector_db')
