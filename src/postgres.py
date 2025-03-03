#
# Title: postgres.py
# Description: postgresql support
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
# import sqlalchemy
# from sqlalchemy import and_
# from sqlalchemy import select

import datetime
import time

from typing import List, Dict

import pytz

import sqlalchemy
from sqlalchemy import and_
from sqlalchemy import select

from sql_table import BoxScore, Cooked, GeoLoc, LoadLog, Observation, Wap


class PostGres:
    """mellow heeler postgresql support"""

    db_engine = None
    Session = None

    def __init__(self, session: sqlalchemy.orm.session.sessionmaker):
        self.Session = session

    def box_score_insert(
        self, device: str, fresh_wap: int, updated_wap: int, fix_time_ms: int
    ) -> BoxScore:
        """box_score row insert"""

        tweaked_date = time.strftime("%Y-%m-%d", time.gmtime(fix_time_ms / 1000))

        candidate = BoxScore(fresh_wap, 0, updated_wap, device, 1, True, tweaked_date)

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

    def box_score_select_daily(self, target_date: str) -> List[BoxScore]:
        """return all rows for a specific date"""

        statement = (
            select(BoxScore).filter_by(score_date=target_date).order_by(BoxScore.device)
        )

        results = []

        with self.Session() as session:
            rows = session.scalars(statement).all()
            for row in rows:
                results.append(row)

        return results

    def box_score_select_refresh(self) -> List[BoxScore]:
        """return all rows with active refresh flag"""

        statement = select(BoxScore).filter_by(refresh_flag=True)
        results = []

        with self.Session() as session:
            rows = session.scalars(statement).all()
            for row in rows:
                results.append(row)

        return results

    def box_score_refresh(self, candidate: BoxScore) -> BoxScore:
        """box_score row refresh"""

        session = self.Session()
        session.add(candidate)
        session.commit()
        session.close()

        return candidate

    def box_score_update(
        self, device: str, fresh_wap: int, updated_wap: int, fix_time_ms: int
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

    def load_log_insert(self, file_name: str, file_type: str) -> LoadLog:
        """load_log insert row"""

        candidate = LoadLog(file_name, file_type)

        session = self.Session()
        session.add(candidate)
        session.commit()
        session.close()

        return candidate

    def load_log_select(self, file_name: str) -> LoadLog:
        """load_log select row"""

        with self.Session() as session:
            rows = session.scalars(select(LoadLog).filter_by(file_name=file_name)).all()
            for row in rows:
                return row

        #        statement = select(LoadLog).filter_by(file_name=file_name)

        #        with self.Session() as session:
        #            rows = session.scalars(statement).all()
        #            for row in rows:
        #                return row

        return None

    def observation_count(
        self, start_time_ms: int, stop_time_ms: int, device: str
    ) -> int:
        """count observations between times"""

        # start_time_ms, stop_time_ms are the midnight boundaries
        # device = target device, i.e. 'android1'
        # return the population of observed distinct wap for the period and device

        # select observation.geoloc_id, observation.fix_time_ms, observation.wap_id,
        # geoloc.device from observation inner join geoloc on
        # observation.geoloc_id = geoloc.id where geoloc.device = 'android1';

        # TODO find the sqlalchemy equivalent and delete the kludge dictionary

        kludge = {}
        with self.Session() as session:
            for row in (
                session.query(Observation, GeoLoc)
                .filter(Observation.geoloc_id == GeoLoc.id)
                .filter(
                    GeoLoc.device == device,
                    and_(
                        Observation.fix_time_ms >= start_time_ms,
                        Observation.fix_time_ms <= stop_time_ms,
                    ),
                )
                .all()
            ):
                kludge[row[0].wap_id] = "kludge"

        return len(kludge)

    def observation_insert(self, observation: Dict[str, str]) -> Observation:
        """observation insert row"""

        candidate = Observation(
            observation["fixTimeMs"],
            observation["geolocId"],
            observation["level"],
            observation["loadlogId"],
            observation["wapId"],
        )

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
                    row.insert_flag = False
                    row.update_flag = False
                    return row

        if row is None:
            wap["version"] = 1
            insert_flag = True
            update_flag = False
        else:
            wap["version"] = row.version + 1
            insert_flag = False
            update_flag = True

        result = self.wap_insert(wap)
        result.insert_flag = insert_flag
        result.update_flag = update_flag
        return result


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
