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

    def load_log_insert(
        self, args: dict[str, any], obs_list_population: int, site: str
    ) -> LoadLog:
        """load_log insert row"""

        # must create date this way to avoid timezone issues
        temp1 = args['file_time']
        args['file_date'] = datetime.date(temp1.year, temp1.month, temp1.day)

        candidate = LoadLog(args, obs_list_population, site)

        try:
            with self.Session() as session:
                session.add(candidate)
                session.commit()
        except Exception as error:
            print(error)

        return candidate

    def load_log_select_by_file_name(self, file_name: str) -> list[LoadLog]:
        """load_log select by file name"""

        with self.Session() as session:
            return session.scalars(select(LoadLog).filter_by(file_name=file_name)).all()

    def load_log_select_by_date(self, start: datetime.datetime, stop: datetime.datetime) -> list[LoadLog]:
        """ return all load_log rows for a date """

        pass

#        arg = select(LoadLog).filter(LoadLog.file_time.between(start, stop))

#        with self.Session() as session:
#            return session.scalars(arg).all()
#            return session.scalars(select(LoadLog).filter_by(LoadLog.file_time>start)).all()
#            return session.scalars(select(LoadLog).filter(LoadLog.file_time.between(start, stop))).all()

#qry = DBSession.query(User).filter(User.birthday.between('1985-01-17', '1988-01-17'))
#        # Query events within the datetime range
#events_in_range = session.query(Event).filter(Event.timestamp >= start_datetime, Event.timestamp <= end_datetime).all()
#
# Print the results
#for event in events_in_range:
#    print(f"Event ID: {event.id}, Timestamp: {event.timestamp}")


    def geo_loc_insert(self, args: dict[str, any], load_log_id: int) -> GeoLoc:
        """geoloc insert row"""

        candidate = GeoLoc(args, load_log_id)

        try:
            with self.Session() as session:
                session.add(candidate)
                session.commit()
        except Exception as error:
            print(error)

        return candidate

    def geo_loc_select_by_load_log(self, id: int) -> list[GeoLoc]:
        """geoloc select row for a load log"""

        statement = select(GeoLoc).filter_by(load_log_id=id).order_by(GeoLoc.fix_time)

        with self.Session() as session:
            return session.scalars(statement).all()

    def geo_loc_select_by_site(self, site: str) -> list[GeoLoc]:
        """geoloc select row for a site"""

        statement = select(GeoLoc).filter_by(site=site).order_by(GeoLoc.fix_time)

        with self.Session() as session:
            return session.scalars(statement).all()

    def geo_loc_select_or_insert(
        self, args: dict[str, any], load_log_id: int
    ) -> GeoLoc:
        if args['site'].startswith("mobile"):
            return self.geo_loc_insert(args, load_log_id)
        else:
            candidate = self.geo_loc_select_by_site(args['site'])
            if len(candidate) < 1:
                return None
            else:
                return candidate[0]

    def observation_insert(self, args: dict[str, any], load_log_id: int, wap_id: int) -> Observation:
        """observation insert row"""

        candidate = Observation(args, load_log_id, wap_id)

        try:
            with self.Session() as session:
                session.add(candidate)
                session.commit()
        except Exception as error:
            print(error)

        return candidate

    def observation_select_by_bssid_and_load_log(
        self, bssid: str, load_log_id: int
    ) -> list[Observation]:
        """observation select row"""

        statement = select(Observation).filter_by(bssid=bssid, load_log_id=load_log_id)

        with self.Session() as session:
            return session.scalars(statement).all()

    def wap_insert(self, args: dict[str, any], version: int, load_log_id: int) -> Wap:
        """wap insert row"""

        candidate = Wap(args, version, load_log_id)

        try:
            with self.Session() as session:
                session.add(candidate)
                session.commit()
        except Exception as error:
            print(error)

        return candidate

    def wap_select_by_bssid(self, args: dict[str, any]) -> Wap:
        """wap select row"""

        statement = (
            select(Wap).filter_by(bssid=wap["bssid"].lower()).order_by(Wap.version)
        )

        with self.Session() as session:
            rows = session.scalars(statement).all()

            for row in rows:
                if (
                    row.capability == args["capability"]
                    and row.frequency == args["frequency_mhz"]
                    and row.ssid == args["ssid"]
                ):
                    return row

        return None

    def wap_select_or_insert(self, args: dict[str, any], load_log_id: int) -> Wap:
        """discover if wap exists or if not, max version for insert"""

        statement = (
            select(Wap).filter_by(bssid=args["bssid"].lower()).order_by(Wap.version)
        )

        row = None
        with self.Session() as session:
            rows = session.scalars(statement).all()
            for row in rows:
                if (
                    row.capability == args["capability"]
                    and row.frequency_mhz == args["frequency_mhz"]
                    and row.ssid == args["ssid"]
                ):
                    row.insert_flag = False
                    row.update_flag = False
                    return row

        if row is None:
            version = 1
            print(f"insert version {version}")
            insert_flag = True
            update_flag = False
        else:
            version = row.version + 1
            print(f"insert version {version}")
            insert_flag = False
            update_flag = True

        result = self.wap_insert(args, version, load_log_id)
        result.insert_flag = insert_flag
        result.update_flag = update_flag
        return result

    ############# old below

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


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
