"""mellow heeler postgresql support"""

import datetime
import time

from typing import Dict

import pytz

import sqlalchemy
from sqlalchemy import select

from sql_table import BoxScore, Cooked, GeoLoc, Observation, Wap


class PostGres:
    """mellow heeler postgresql support"""

    db_engine = None
    dry_run = False
    Session = None

    def __init__(self, session: sqlalchemy.orm.session.sessionmaker, dry_run: bool):
        self.Session = session
        self.dry_run = dry_run

    def box_score_insert(
        self, fresh_wap: int, updated_wap: int, fix_time_ms: int, device: str
    ) -> BoxScore:
        """box_score row insert"""

        tweaked_date = time.strftime("%Y-%m-%d", time.gmtime(fix_time_ms / 1000))

        candidate = BoxScore(fresh_wap, 0, updated_wap, 1, True, tweaked_date, device)

        session = self.Session()

        session.add(candidate)
        session.commit()
        session.close()

        return candidate

    def box_score_select(self, fix_time_ms: int, device: str) -> BoxScore:
        """box_score row select"""

        tweaked_date = time.strftime("%Y-%m-%d", time.gmtime(fix_time_ms / 1000))

        statement = select(BoxScore).filter_by(score_date=tweaked_date, device=device)

        with self.Session() as session:
            rows = session.scalars(statement).all()
            for row in rows:
                return row

        return None

    def box_score_update(
        self, fresh_wap: int, updated_wap: int, fix_time_ms: int, device: str
    ) -> BoxScore:
        """box_score row update"""

        tweaked_date = time.strftime("%Y-%m-%d", time.gmtime(fix_time_ms / 1000))

        session = self.Session()
        candidate = session.execute(
            select(BoxScore).filter_by(score_date=tweaked_date, device=device)
        ).scalar_one()

        candidate.bssid_new = candidate.bssid_new + fresh_wap
        candidate.bssid_updated = candidate.bssid_updated + updated_wap
        candidate.file_population = candidate.file_population + 1
        candidate.observed_last = fix_time_ms
        session.commit()

        return candidate

    def cooked_insert(self, cooked: Dict[str, str]) -> Cooked:
        """cooked row insert"""

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
        """cooked row select"""

        statement = select(Cooked).filter_by(wap_id=wap_id)

        with self.Session() as session:
            rows = session.scalars(statement).all()
            for row in rows:
                return row

        return None

    def cooked_update(self, cooked: Dict[str, str]) -> Cooked:
        """cooked row update"""

        tweaked_seconds = cooked["fixTimeMs"] / 1000
        tweaked_date = datetime.datetime.fromtimestamp(tweaked_seconds, pytz.utc)
        tweaked_time = time.strftime("%Y-%m-%d %H:%M:%S", time.gmtime(tweaked_seconds))

        candidate = self.cooked_select(cooked["wapId"])
        candidate.observed_counter = candidate.observed_counter + 1

        # ensure first/last dates are correct
        if candidate.observed_first > tweaked_date:
            candidate.observed_first = tweaked_time
        elif candidate.observed_last < tweaked_date:
            candidate.observed_last = tweaked_time

        session = self.Session()
        session.add(candidate)
        session.commit()
        session.close()

    def geoloc_insert(self, geoloc: Dict[str, str]) -> GeoLoc:
        """geoloc row insert"""

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
        """geoloc select row for a device"""

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
        """geoloc select row for a time"""

        statement = select(GeoLoc).filter_by(
            fix_time_ms=geoloc["fixTimeMs"], device=geoloc["device"]
        )

        with self.Session() as session:
            rows = session.scalars(statement).all()
            for row in rows:
                return row

        return None

    def observation_insert(self, observation: Dict[str, str]) -> Observation:
        """observation insert row"""

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
        """observation select row"""

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
        """wap insert row"""

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
        """wap select row"""

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
        """discover if wap exists or if not, max version for insert"""

        statement = (
            select(Wap).filter_by(bssid=wap["bssid"].lower()).order_by(Wap.version)
        )

        row = None
        with self.Session() as session:
            rows = session.scalars(statement).all()
            for row in rows:
                if (
                    row.capability == wap["capability"]
                    and row.frequency == wap["frequency"]
                    and row.ssid == wap["ssid"]
                ):
                    return row

        if row is None:
            wap["version"] = 1
        else:
            wap["version"] = row.version + 1

        return self.wap_insert(wap)


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
