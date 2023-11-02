import datetime

from sqlalchemy import Column
from sqlalchemy import BigInteger, Boolean, Date, DateTime, Float, Integer, String

from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

class BoxScore(Base):
  __tablename__ = 'box_score'

  id = Column(Integer, primary_key=True)
  bssid_new = Column(Integer)
  bssid_total = Column(Integer)
  bssid_updated = Column(Integer)
  file_population = Column(Integer)
  refresh_flag = Column(Boolean)
  score_date = Column(Date)
  site = Column(String)

  def __init__(self, bssid_new, bssid_total, bssid_updated, file_population, refresh_flag, score_date, site):
    self.bssid_new = bssid_new
    self.bssid_total = bssid_total
    self.bssid_updated = bssid_updated
    self.file_population = file_population
    self.refresh_flag = refresh_flag
    self.score_date = score_date
    self.site = site

  def __repr__(self):
    return "<box_score(%d, %d)>" % (self.id, self.score_date)

class Cooked(Base):
  __tablename__ = 'cooked'

  id = Column(Integer, primary_key=True)
  latitude = Column(String)
  longitude = Column(String)
  note = Column(String)
  observed_counter = Column(Integer)
  observed_first = Column(DateTime, default=datetime.datetime.utcnow)
  observed_last = Column(DateTime, default=datetime.datetime.utcnow)
  wap_id = Column(BigInteger)

  def __init__(self, latitude, longitude, note, observed_counter, observed_first, observed_last, wap_id):
    self.latitude = latitude
    self.longitude = longitude
    self.note = note
    self.observed_counter = observed_counter
    self.observed_first = observed_first
    self.observed_last = observed_last
    self.wap_id = wap_id

  def __repr__(self):
    return "<cooked(%d)>" % (self.id)

class GeoLoc(Base):
  __tablename__ = 'geoloc'

  id = Column(Integer, primary_key=True)
  accuracy = Column(String)
  altitude = Column(String)
  fix_time_ms = Column(BigInteger)
  latitude = Column(String)
  longitude = Column(String)

  def __init__(self, accuracy, altitude, fix_time_ms, latitude, longitude):
    self.accuracy = accuracy
    self.altitude = altitude
    self.fix_time_ms = fix_time_ms
    self.latitude = latitude
    self.longitude = longitude

  def __repr__(self):
    return "<geoloc(%d, %d)>" % (self.id, self.fix_time_ms)

class Observation(Base):
  __tablename__ = 'observation'

  id = Column(Integer, primary_key=True)
  geoloc_id = Column(BigInteger)
  level = Column(Integer)
  time_stamp = Column(DateTime, default=datetime.datetime.utcnow)
  wap_id = Column(BigInteger)
  
  def __init__(self, geoloc_id, level, time_stamp, wap_id):
    self.geoloc_id = geoloc_id
    self.level = level
    self.time_stamp = time_stamp
    self.wap_id = wap_id

  def __repr__(self):
    return "<observation(%d, %d)>" % (self.id, self.time_stamp)

class Wap(Base):
  __tablename__ = 'wap'

  id = Column(Integer, primary_key=True)
  bssid = Column(String)
  capability = Column(String)
  frequency = Column(Integer)
  ssid = Column(String)

  def __init__(self, bssid, capability, frequency, ssid):
    self.bssid = bssid
    self.capability = capability
    self.frequency = frequency
    self.ssid = ssid

  def __repr__(self):
    return "<wap(%d, %s, %s)>" % (self.id, self.bssid, self.ssid)