#
# Title: sql_table.py
# Description: database table definitions
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
# import sqlalchemy
# from sqlalchemy import and_
# from sqlalchemy import select

from datetime import datetime, timezone

from sqlalchemy import Column
from sqlalchemy import BigInteger, Boolean, Date, DateTime, Float, Integer, String

from sqlalchemy.orm import registry
from sqlalchemy.orm import DeclarativeBase
from sqlalchemy.ext.declarative import declared_attr

mapper_registry = registry()


class Base(DeclarativeBase):
    pass


class BoxScore(Base):
    """box_score table definition"""

    __tablename__ = "box_score"

    id = Column(Integer, primary_key=True)
    bssid_new = Column(Integer)
    bssid_total = Column(Integer)
    bssid_unique = Column(Integer)
    file_date = Column(Date)
    file_quantity = Column(Integer)
    platform = Column(String)
    site = Column(String)

    def __init__(self, args: dict[str, any]):
        self.bssid_new = args["bssid_new"]
        self.bssid_total = args["bssid_total"]
        self.bssid_unique = args["bssid_unique"]
        self.file_date = args["file_date"]
        self.file_quantity = args["file_quantity"]
        self.platform = args["platform"]
        self.site = args["site"]


class Cooked(Base):
    """cooked table definition"""

    __tablename__ = "cooked"

    id = Column(Integer, primary_key=True)
    confidence = Column(Integer)
    latitude = Column(Float)
    longitude = Column(Float)
    note = Column(String)
    obs_quantity = Column(BigInteger)
    obs_first = Column(DateTime)
    obs_last = Column(DateTime)
    street_address = Column(String)
    street_zip = Column(String)
    wap_id = Column(BigInteger)

    def __init__(self, args: dict[str, any], wap_id: int):
        self.confidence = args["confidence"]
        self.latitude = args["latitude"]
        self.longitude = args["longitude"]
        self.note = args["note"]
        self.obs_quantity = args["obs_quantity"]
        self.obs_first = args["obs_first"]
        self.obs_last = args["obs_last"]
        self.street_address = args["street_address"]
        self.street_zip = args["street_zip"]
        self.wap_id = wap_id


class GeoLoc(Base):
    """geoloc table definition"""

    __tablename__ = "geo_loc"

    id = Column(Integer, primary_key=True)
    altitude = Column(Float)
    fix_time = Column(DateTime)
    latitude = Column(Float)
    longitude = Column(Float)
    site = Column(String)
    speed = Column(Float)
    track = Column(Float)
    load_log_id = Column(BigInteger)

    def __init__(self, args: dict[str, any], load_log_id: int):
        self.altitude = args["altitude"]
        self.fix_time = args["fix_time"]
        self.latitude = args["latitude"]
        self.longitude = args["longitude"]
        self.site = args["site"]
        self.speed = args["speed"]
        self.track = args["track"]

        self.load_log_id = load_log_id

    def __eq__(self, other):
        return self.fix_time == other.fix_time and self.site == other.site


class LoadLog(Base):
    """load_log table definition"""

    __tablename__ = "load_log"

    id = Column(Integer, primary_key=True)
    file_date = Column(Date)
    file_name = Column(String)
    file_time = Column(DateTime)
    file_type = Column(String)
    load_time = Column(DateTime)
    obs_quantity = Column(Integer)
    platform = Column(String)
    site = Column(String)

    def __init__(self, args: dict[str, any]):
        self.file_date = args["file_date"]
        self.file_name = args["file_name"]
        self.file_time = args["file_time"]
        self.file_type = args["file_type"]
        self.load_time = datetime.now(timezone.utc)
        self.obs_quantity = args["obs_quantity"]
        self.platform = args["platform"]
        self.site = args["site"]


class Observation(Base):
    """observation table definition"""

    __tablename__ = "observation"

    id = Column(Integer, primary_key=True)
    bssid = Column(String)
    file_date = Column(Date)
    signal_dbm = Column(Integer)
    load_log_id = Column(BigInteger)
    wap_id = Column(BigInteger)

    def __init__(
        self,
        args: dict[str, any],
        load_log_id: int,
        wap_id: int,
    ):
        self.bssid = args["bssid"]
        self.file_date = args["file_date"]
        self.signal_dbm = args["signal_dbm"]
        self.load_log_id = load_log_id
        self.wap_id = wap_id


class Wap(Base):
    """wap table definition"""

    __tablename__ = "wap"

    id = Column(Integer, primary_key=True)
    bssid = Column(String)
    capability = Column(String)
    frequency_mhz = Column(Integer)
    ssid = Column(String)
    version = Column(Integer)
    load_log_id = Column(BigInteger)

    def __init__(self, args: dict[str, any], version: int, load_log_id: int):
        self.bssid = args["bssid"]
        self.capability = args["capability"]
        self.frequency_mhz = args["frequency_mhz"]
        self.ssid = args["ssid"]
        self.version = version
        self.load_log_id = load_log_id


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
