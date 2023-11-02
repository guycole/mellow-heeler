import datetime

from sqlalchemy import Column
from sqlalchemy import BigInteger, Boolean, Date, DateTime, Integer, String

from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

class Bogus(Base):
  __tablename__ = 'geoloc'


