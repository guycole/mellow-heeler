"""mellow heeler database table definitions"""

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

    __tablename__ = "geoloc"

    id = Column(Integer, primary_key=True)
    accuracy = Column(Float)
    altitude = Column(Float)
    fix_time_ms = Column(BigInteger)
    latitude = Column(Float)
    longitude = Column(Float)
    device = Column(String)

    def __eq__(self, other):
        return (
            self.accuracy == other.accuracy
            and self.altitude == other.altitude
            and self.fix_time_ms == other.fix_time_ms
            and self.latitude == other.latitude
            and self.longitude == other.longitude
            and self.device == other.device
        )

    def __init__(
        self,
        accuracy: float,
        altitude: float,
        fix_time_ms: int,
        latitude: float,
        longitude: float,
        device: str,
    ):
        self.accuracy = accuracy
        self.altitude = altitude
        self.fix_time_ms = fix_time_ms
        self.latitude = latitude
        self.longitude = longitude
        self.device = device

    def __repr__(self):
        if self.id is None:
            self.id = 0

        return f"<geoloc({self.id}, {self.fix_time_ms}, {self.device})>"

class LoadLog(Base):
    """load_log table definition"""

    __tablename__ = "load_log"

    id = Column(Integer, primary_key=True)
    file_name = Column(String)
    file_type = Column(String)
    time_stamp = Column(DateTime)

    def __init__(self, file_name, file_type):
        self.file_name = file_name
        self.file_type = file_type
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

    def __init__(self, bssid, capability, frequency, ssid, version):
        self.bssid = bssid
        self.capability = capability
        self.frequency = frequency
        self.ssid = ssid
        self.version = version

    def __repr__(self):
        if self.id is None:
            self.id = 0

        return "<wap(%d, %s, %d, %s)>" % (self.id, self.bssid, self.version, self.ssid)
