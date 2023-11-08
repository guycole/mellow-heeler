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

from sql_table import GeoLoc

from sqlalchemy import create_engine, select
from sqlalchemy.orm import Session, sessionmaker

from typing import Dict, List

from yaml.loader import SafeLoader

class PostGres(object):
    db_engine = None
    dry_run = False
    
    def __init__(self, db_engine: sqlalchemy.engine.base.Engine, dry_run: bool):
        self.db_engine = db_engine
        self.dry_run = dry_run

    def geoloc_insert(self, geoloc: Dict) -> GeoLoc:
        lat = round(geoloc['latitude'], 5)
        lng = round(geoloc['longitude'], 5)

        candidate = GeoLoc(geoloc['accuracy'], geoloc['altitude'], geoloc['fixTimeMs'], lat, lng, geoloc['site'])
        with Session(self.db_engine) as session:
            session.add(candidate)
            session.commit()

        return candidate

    def geoloc_select_by_site(self, site:str) -> GeoLoc:
        if site not in ['anderson1', 'vallejo1']:
            raise ValueError(f"invalid site:{site}")

        statement = select(GeoLoc).filter_by(site = site)

        row = None
        with Session(self.db_engine) as session:
            rows = session.scalars(statement).all()
            for row in rows:
                pass

        return row
    
    def geoloc_select_by_time(self, geoloc: Dict) -> GeoLoc:
        statement = select(GeoLoc).filter_by(fix_time_ms = geoloc['fixTimeMs'])

        lat = round(geoloc['latitude'], 5)
        lng = round(geoloc['longitude'], 5)

        print("xxxxxx")

        row = None
        with Session(self.db_engine) as session:
            rows = session.scalars(statement).all()
            for row in rows:
                if row.accuracy == geoloc['accuracy'] and row.altitude == geoloc['altitude'] and row.latitude == lat and row.longitude == lng:
                    return row

        return None
    

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
    postgres = PostGres(db_engine2, configuration["dryRun"])
    geosite = postgres.geoloc_select_by_site('vallejo1')
    print(type(geosite))
    print(geosite)

    geodict = {}
    geodict['accuracy'] = 128.9
    geodict['altitude'] = 0
    geodict['fixTimeMs'] = 1626015227705
    #geodict['fixTimeMs'] = 1629166763219
    geodict['latitude'] = 40.4345425
    geodict['longitude'] = -122.2845614
    geodict['site'] = 'bogus'
    
    geoloc = postgres.geoloc_select_by_time(geodict)
    print(geoloc)
    geoloc = postgres.geoloc_insert(geodict)
    print(geoloc)


print("stop postgres")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
