#
# Title: postgres.py
# Description:
# Development Environment: OS X 12.6.9/Python 3.11.5
# Author: G.S. Cole (guycole at gmail dot com)
#
import sqlalchemy
import sys
import typing
import time
import yaml

from sql_table import GeoLoc, Wap

from sqlalchemy import create_engine, select
from sqlalchemy.orm import Session, sessionmaker
from sqlalchemy.orm import sessionmaker

from typing import Dict, List

from yaml.loader import SafeLoader


class PostGres(object):
    db_engine = None
    dry_run = False

    def __init__(self, db_engine: sqlalchemy.engine.base.Engine, dry_run: bool):
        self.db_engine = db_engine
        self.dry_run = dry_run

    def geoloc_insert(self, geoloc: Dict) -> GeoLoc:
        lat = round(geoloc["latitude"], 5)
        lng = round(geoloc["longitude"], 5)

        candidate = GeoLoc(
            geoloc["accuracy"],
            geoloc["altitude"],
            geoloc["fixTimeMs"],
            lat,
            lng,
            geoloc["device"],
        )

        if self.dry_run is True:
            print(f"skipping insert for geoloc {geoloc['fixTimeMs']}")
        else:
            Session = sessionmaker(bind=self.db_engine, expire_on_commit=False)
            session = Session()

            session.add(candidate)
            session.commit()
            session.close()

        return candidate

    def geoloc_select_by_device(self, device: str) -> GeoLoc:
        if device not in ["rpi4c-anderson1", "rpi4a-vallejo1"]:
            raise ValueError(f"invalid device:{device}")
        
        statement = select(GeoLoc).filter_by(device=device)

        row = None
        with Session(self.db_engine) as session:
            rows = session.scalars(statement).all()
            for row in rows:
                pass

        return row

    def geoloc_select_by_time(self, geoloc: Dict) -> GeoLoc:
        statement = select(GeoLoc).filter_by(fix_time_ms=geoloc["fixTimeMs"], device=geoloc["device"])

        lat = round(geoloc["latitude"], 5)
        lng = round(geoloc["longitude"], 5)

        row = None
        with Session(self.db_engine) as session:
            rows = session.scalars(statement).all()
            for row in rows:
                pass

        return row

    def geoloc_select_or_insert(self, geoloc: Dict) -> GeoLoc:
        result = self.geoloc_select_by_time(geoloc)
        if result is None:
            result = self.geoloc_insert(geoloc)

        return result

    def wap_insert(self, wap: Dict) -> Wap:
        candidate = Wap(wap["bssid"], wap["capability"], wap["frequency"], wap["ssid"], wap["version"])

        if self.dry_run is True:
            print(f"skipping insert for wap {wap['bssid']}")
        else:
            Session = sessionmaker(bind=self.db_engine, expire_on_commit=False)
            session = Session()

            session.add(candidate)
            session.commit()
            session.close()

        return candidate

    def wap_select_or_insert(self, wap: Dict) -> Wap:
        statement = select(Wap).filter_by(bssid=wap["bssid"]).order_by(Wap.version)

        row = None
        with Session(self.db_engine) as session:
            rows = session.scalars(statement).all()
            for row in rows:
                if (
                    row.capability == wap["capability"]
                    and row.frequency == wap["frequency"]
                    and row.ssid == wap["ssid"]
                ):
                    return row

        wap["version"] = row.version + 1
        return self.wap_insert(wap)

print("start postgres")

#
# argv[1] = configuration filename
#
if __name__ == "__main__":
    if len(sys.argv) > 1:
        file_name = sys.argv[1]
    else:
        file_name = "config.yaml"

    with open(file_name, "r") as stream:
        try:
            configuration = yaml.load(stream, Loader=SafeLoader)
        except yaml.YAMLError as exc:
            print(exc)

    db_engine2 = create_engine(configuration["dbConn"])
    postgres = PostGres(db_engine2, False)
    # postgres = PostGres(db_engine2, configuration["dryRun"])

    geosite = postgres.geoloc_select_by_device("rpi4a-vallejo1")
    print(type(geosite))
    print(geosite)

    geodict = {}
    geodict["accuracy"] = 128.9
    geodict["altitude"] = 0
    geodict["fixTimeMs"] = 1626015227705
    geodict['fixTimeMs'] = 1629166763219
    geodict["latitude"] = 40.4345425
    geodict["longitude"] = -122.2845614
    geodict["device"] = "rpi4c-anderson1"

    geoloc = postgres.geoloc_select_by_time(geodict)
    print(geoloc)
    #geoloc = postgres.geoloc_insert(geodict)
    #print(geoloc)

    geoloc = postgres.geoloc_select_or_insert(geodict)
    print(geoloc)

    wapdict = {}
    wapdict["bssid"] = "56:20:5f:fd:06:15"
    wapdict["ssid"] = "airportthru"
    wapdict["frequency"] = 2457
    wapdict["capability"] = "[IBSS]"

#    wap = postgres.wap_select_by_bssid(wapdict)
#    print(wap)

    wapdict["ssid"] = "updated"
    wap = postgres.wap_select_or_insert(wapdict)
    print(wap)

print("stop postgres")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
