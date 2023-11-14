#
# Title: postgres.py
# Description:
# Development Environment: OS X 12.6.9/Python 3.11.5
# Author: G.S. Cole (guycole at gmail dot com)
#
import time
from typing import Dict

import sqlalchemy
from sqlalchemy import select

from sql_table import BoxScore, Cooked, GeoLoc, Observation, Wap


class PostGres:
    db_engine = None
    dry_run = False
    Session = None

    def __init__(self, session: sqlalchemy.orm.session.sessionmaker, dry_run: bool):
        self.Session = session
        self.dry_run = dry_run

    def box_score_insert(
        self, fresh_wap: int, fix_time_ms: int, device: str
    ) -> BoxScore:
        tweaked_date = time.strftime("%Y-%m-%d", time.gmtime(fix_time_ms / 1000))

        candidate = BoxScore(fresh_wap, 0, 0, 1, True, tweaked_date, device)

        session = self.Session()

        session.add(candidate)
        session.commit()
        session.close()

        return candidate

    def box_score_select(self, fix_time_ms: int, device: str) -> BoxScore:
        tweaked_date = time.strftime("%Y-%m-%d", time.gmtime(fix_time_ms / 1000))

        statement = select(BoxScore).filter_by(score_date=tweaked_date, device=device)

        with self.Session() as session:
            rows = session.scalars(statement).all()
            for row in rows:
                return row

        return None

    def box_score_update(
        self, fresh_wap: int, fix_time_ms: int, device: str
    ) -> BoxScore:
        tweaked_date = time.strftime("%Y-%m-%d", time.gmtime(fix_time_ms / 1000))

        session = self.Session()
        candidate = session.execute(
            select(BoxScore).filter_by(score_date=tweaked_date, device=device)
        ).scalar_one()

        candidate.bssid_new = candidate.bssid_new + fresh_wap
        candidate.file_population = candidate.file_population + 1
        candidate.observed_last = fix_time_ms
        session.commit()

        return candidate

    def cooked_insert(self, cooked: Dict[str, str]) -> Cooked:
        # convert from ms to calendar date
        tweaked_time = time.strftime(
            "%Y-%m-%d %H:%M:%S", time.gmtime(cooked["fixTimeMs"] / 1000)
        )

        candidate = Cooked(
            1,
            cooked["latitude"],
            cooked["longitude"],
            "note",
            1,
            tweaked_time,
            tweaked_time,
            cooked["wapId"],
        )

        if self.dry_run is True:
            print(f"skipping insert for cooked {cooked['fixTimeMs']}")
        else:
            session = self.Session()

            session.add(candidate)
            session.commit()
            session.close()

        return candidate

    def cooked_select(self, wap_id: int) -> Cooked:
        statement = select(Cooked).filter_by(wap_id=wap_id)

        with self.Session() as session:
            rows = session.scalars(statement).all()
            for row in rows:
                return row

        return None

    def cooked_update(self, cooked: Dict[str, str]) -> Cooked:
        # update counter and observed last
        tweaked_time = time.strftime(
            "%Y-%m-%d %H:%M:%S", time.gmtime(cooked["fixTimeMs"] / 1000)
        )

        session = self.Session()
        candidate = session.execute(
            select(Cooked).filter_by(wap_id=cooked["wapId"])
        ).scalar_one()

        candidate.observed_counter = candidate.observed_counter + 1
        candidate.observed_last = tweaked_time
        session.commit()

    def geoloc_insert(self, geoloc: Dict[str, str]) -> GeoLoc:
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
            # Session = sessionmaker(bind=self.db_engine, expire_on_commit=False)
            session = self.Session()

            session.add(candidate)
            session.commit()
            session.close()

        return candidate

    def geoloc_select_by_device(self, device: str) -> GeoLoc:
        if device not in ["rpi4c-anderson1", "rpi4a-vallejo1"]:
            raise ValueError(f"invalid device:{device}")

        statement = select(GeoLoc).filter_by(device=device)

        row = None
        with self.Session() as session:
            rows = session.scalars(statement).all()
            for row in rows:
                pass

        return row

    def geoloc_select_by_time(self, geoloc: Dict[str, str]) -> GeoLoc:
        statement = select(GeoLoc).filter_by(
            fix_time_ms=geoloc["fixTimeMs"], device=geoloc["device"]
        )

        with self.Session() as session:
            rows = session.scalars(statement).all()
            for row in rows:
                return row

        return None

    def observation_insert(self, observation: Dict[str, str]) -> Observation:
        candidate = Observation(
            observation["geolocId"],
            observation["level"],
            observation["fixTimeMs"],
            observation["wapId"],
        )

        if self.dry_run is True:
            print(f"skipping insert for observation {observation['wap_id']}")
        else:
            session = self.Session()
            session.add(candidate)
            session.commit()
            session.close()

        return candidate

    def observation_select(self, observation: Dict[str, str]) -> Observation:
        print(observation)
        statement = select(Observation).filter_by(
            geoloc_id=observation["geolocId"],
            fix_time_ms=observation["fixTimeMs"],
            wap_id=observation["wapId"],
        )

        with self.Session() as session:
            rows = session.scalars(statement).all()
            for row in rows:
                return row

        return None

    def wap_insert(self, wap: Dict[str, str]) -> Wap:
#        if wap["version"] > 10:
#            raise ValueError(f"invalid version:{wap['version']}")
        
#        if len(wap) > 1:
#            raise ValueError(f"invalid wap:{wap}")
        
        candidate = Wap(
            wap["bssid"].lower(),
            wap["capability"],
            wap["frequency"],
            wap["ssid"],
            wap["version"],
        )

        if self.dry_run is True:
            print(f"skipping insert for wap {wap['bssid']}")
        else:
            # Session = sessionmaker(bind=self.db_engine, expire_on_commit=False)
            session = self.Session()

            session.add(candidate)
            session.commit()
            session.close()

        return candidate

    def wap_select(self, wap: Dict[str, str]) -> Wap:
        statement = (
            select(Wap).filter_by(bssid=wap["bssid"].lower()).order_by(Wap.version)
        )

        with self.Session() as session:
            rows = session.scalars(statement).all()
            for row in rows:
                if (
                    row.capability == wap["capability"]
                    and row.frequency == wap["frequency"]
                    and row.ssid == wap["ssid"]
                ):
                    return row

        return None

    def wap_select_or_insert(self, wap: Dict[str, str]) -> Wap:
        print("0x0x0x0x0x0x0")
        print(wap)
        statement = (
            select(Wap).filter_by(bssid=wap["bssid"].lower()).order_by(Wap.version)
        )

        row = None
        row2 = None
        with self.Session() as session:
            rows = session.scalars(statement).all()
            for row in rows:
                print(f"row version:{row.version}")

                if row.capability == wap["capability"]:
                    print("capability match")
                if row.frequency == wap["frequency"]:
                    print("frequency match")
                if row.ssid == wap["ssid"]:
                    print("ssid match")

                if (
                    row.capability == wap["capability"]
                    and row.frequency == wap["frequency"]
                    and row.ssid == wap["ssid"]
                ):
                    row2 = row
                    break
                

        print("must create new wap")
        if row2 is not None:
            return row2
 
        if row is None:
            wap["version"] = 1
        else:
            wap["version"] = row.version + 1

        return self.wap_insert(wap)


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
