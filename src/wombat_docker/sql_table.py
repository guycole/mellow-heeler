#
# Title: sql_table.py
# Description: database table definitions
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
# import sqlalchemy
# from sqlalchemy import and_
# from sqlalchemy import select

from datetime import datetime

from sqlalchemy import Column
from sqlalchemy import BigInteger, Boolean, Date, DateTime, Float, Integer, String

from sqlalchemy.orm import registry
from sqlalchemy.orm import DeclarativeBase
from sqlalchemy.ext.declarative import declared_attr

mapper_registry = registry()

class Base(DeclarativeBase):
    pass

class DailyScore(Base):
    __tablename__ = "heeler_daily_score"

    id = Column(Integer, primary_key=True)
    score_date = Column(Date)
    file_quantity = Column(Integer)
    obs_quantity = Column(Integer)
    platform = Column(String)

    def __init__(self, args: dict[str, any]):
        self.score_date = args["score_date"]
        self.file_quantity = args["file_quantity"]
        self.obs_quantity = args["obs_quantity"]
        self.platform = args["platform"]

    def __repr__(self):
        return f"daily_score({self.score_date} {self.platform})"

class LoadLog(Base):
    """load_log table definition"""

    __tablename__ = "heeler_load_log"

    id = Column(Integer, primary_key=True)
    file_name = Column(String)
    file_time = Column(DateTime)
    file_type = Column(String)
    load_time = Column(DateTime)
    obs_quantity = Column(Integer)
    platform = Column(String)

    def __init__(self, args: dict[str, any]):
        self.file_name = args["file_name"]
        self.file_time = args["file_time"]
        self.file_type = args["file_type"]
        self.load_time = datetime.now()
        self.obs_quantity = args["obs_quantity"]
        self.platform = args["platform"]

    def __repr__(self):
        return f"load_log({self.file_name} {self.file_time})"

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
