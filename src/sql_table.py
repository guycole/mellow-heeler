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
from sqlalchemy.ext.declarative import declared_attr

mapper_registry = registry()

from sqlalchemy.orm import DeclarativeBase


class Base(DeclarativeBase):
    pass


class BoxScore(Base):
    """box_score table definition"""

    __tablename__ = "box_score"

    id = Column(Integer, primary_key=True)
    bssid_new = Column(Integer)
    bssid_total = Column(Integer)
    bssid_updated = Column(Integer)
    device = Column(String)
    file_population = Column(Integer)
    refresh_flag = Column(Boolean)
    score_date = Column(Date)

    def __init__(
        self,
        bssid_new,
        bssid_total,
        bssid_updated,
        device,
        file_population,
        refresh_flag,
        score_date,
    ):
        self.bssid_new = bssid_new
        self.bssid_total = bssid_total
        self.bssid_updated = bssid_updated
        self.device = device
        self.file_population = file_population
        self.refresh_flag = refresh_flag
        self.score_date = score_date

    def __repr__(self):
        return f"<box_score({self.id}, {self.score_date}, {self.device})>"


class Cooked(Base):
    """cooked table definition"""

    __tablename__ = "cooked"

    id = Column(Integer, primary_key=True)
    confidence = Column(Integer)
    latitude = Column(Float)
    longitude = Column(Float)
    note = Column(String)
    observed_counter = Column(BigInteger)
    observed_first = Column(DateTime)
    observed_last = Column(DateTime)
    wap_id = Column(BigInteger)

    def __init__(
        self,
        confidence,
        latitude,
        longitude,
        note,
        observed_counter,
        observed_first,
        observed_last,
        wap_id,
    ):
        self.confidence = confidence
        self.latitude = latitude
        self.longitude = longitude
        self.note = note
        self.observed_counter = observed_counter
        self.observed_first = observed_first
        self.observed_last = observed_last
        self.wap_id = wap_id

    def __repr__(self):
        if self.id is None:
            self.id = 0

        return "<cooked(%d)>" % (self.id)


class GeoLoc(Base):
    """geoloc table definition"""

    __tablename__ = "geo_loc"

    id = Column(Integer, primary_key=True)
    altitude = Column(Float)
    fix_time = Column(BigInteger)
    latitude = Column(Float)
    longitude = Column(Float)
    site = Column(String)
    speed = Column(Float)
    track = Column(Float)
    load_log_id = Column(BigInteger)

    def __eq__(self, other):
        return (
            self.fix_time == other.fix_time
            and self.site == other.site
        )

    def __init__(
        self,
        altitude: float,
        fix_time: int,
        latitude: float,
        longitude: float,
        site: str,
        speed: float,
        track: float,
        load_log_id: int,
    ):
        self.altitude = altitude
        self.fix_time = fix_time
        self.latitude = latitude
        self.longitude = longitude
        self.site = site
        self.speed = speed
        self.track = track
        self.load_log_id = load_log_id

    def __repr__(self):
        if self.id is None:
            self.id = 0

        return f"<geoloc({self.id}, {self.fix_time}, {self.site})>"


class LoadLog(Base):
    """load_log table definition"""

    __tablename__ = "load_log"

    id = Column(Integer, primary_key=True)
    file_name = Column(String)
    file_time = Column(DateTime)
    file_type = Column(String)
    load_time = Column(DateTime)
    obs_population = Column(Integer)
    platform = Column(String)
    site = Column(String)

    def __init__(self, args: dict[str, any], file_name: str, file_type: str):
        self.file_name = file_name
        self.file_time = args['zTimeMs']
        self.file_type = file_type
        self.obs_population = len(args['wifi'])
        self.platform = args['platform']
        self.time_stamp = datetime.now(timezone.utc)

    def __repr__(self):
        if self.id is None:
            self.id = 0

        return f"<load_log({self.id}, {self.file_name}, {self.file_type})>"


class Observation(Base):
    """observation table definition"""

    __tablename__ = "observation"

    id = Column(Integer, primary_key=True)
    fix_time_ms = Column(BigInteger)
    geoloc_id = Column(BigInteger)
    level = Column(Integer)
    load_log_id = Column(BigInteger)
    wap_id = Column(BigInteger)

    def __init__(self, fix_time_ms, geoloc_id, level, load_log_id, wap_id):
        self.fix_time_ms = fix_time_ms
        self.geoloc_id = geoloc_id
        self.level = level
        self.load_log_id = load_log_id
        self.wap_id = wap_id

    def __repr__(self):
        if self.id is None:
            self.id = 0

        return f"<observation({self.id}, {self.fix_time_ms}, {self.load_log_id})>"


class Wap(Base):
    """wap table definition"""

    __tablename__ = "wap"

    id = Column(Integer, primary_key=True)
    bssid = Column(String)
    capability = Column(String)
    frequency = Column(Integer)
    ssid = Column(String)
    version = Column(Integer)
    load_log_id = Column(BigInteger)

    def __init__(self, bssid, capability, frequency, ssid, version, load_log_id):
        self.bssid = bssid
        self.capability = capability
        self.frequency = frequency
        self.ssid = ssid
        self.version = version
        self.load_log_id = load_log_id

    def __repr__(self):
        if self.id is None:
            self.id = 0

        return "<wap(%d, %s, %d, %s)>" % (self.id, self.bssid, self.version, self.ssid)


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
