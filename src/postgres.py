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

import sqlalchemy
from sqlalchemy import and_
from sqlalchemy import func
from sqlalchemy import select

from sql_table import Cooked, DailyScore, GeoLoc, LoadLog, Observation, Wap, WeeklyRank, WeeklyRankDetail


class PostGres:
    """mellow heeler postgresql support"""

    db_engine = None
    Session = None

    def __init__(self, session: sqlalchemy.orm.session.sessionmaker):
        self.Session = session

    def cooked_insert(self, args: dict[str, any], wap_id: int) -> Cooked:
        candidate = Cooked(args, wap_id)

        try:
            with self.Session() as session:
                session.add(candidate)
                session.commit()
        except Exception as error:
            print(error)

        return candidate

    def cooked_select_by_wap_id(self, wap_id: int) -> Cooked:
        with self.Session() as session:
            return session.scalars(select(Cooked).filter_by(wap_id=wap_id)).first()

    def cooked_update_by_wap_id(self, args: dict[str, any], wap_id: int) -> Cooked:
        with self.Session() as session:
            candidate = session.scalars(select(Cooked).filter_by(wap_id=wap_id)).first()

            if candidate is None:
                print(f"skipping cooked update for wap {wap_id}")
            else:
                candidate.confidence = args["confidence"]
                candidate.latitude = args["latitude"]
                candidate.longitude = args["longitude"]
                candidate.note = args["note"]
                candidate.obs_quantity = args["obs_quantity"]
                candidate.obs_first = args["obs_first"]
                candidate.obs_last = args["obs_last"]
                candidate.street_address = args["street_address"]
                candidate.street_zip = args["street_zip"]

                session.add(candidate)
                session.commit()

                return candidate

    def daily_score_insert(self, args: dict[str, any]) -> DailyScore:
        candidate = DailyScore(args)
        #        print(f"insert {candidate.id} {candidate.site} {candidate.file_date} {candidate.bssid_new}")

        try:
            with self.Session() as session:
                session.add(candidate)
                session.commit()
        except Exception as error:
            print(error)

        return candidate

    def daily_score_select(
        self, file_date: datetime.date, platform: str, site: str
    ) -> DailyScore:
        statement = select(DailyScore).filter_by(
            file_date=file_date, platform=platform, site=site
        )

        with self.Session() as session:
            return session.scalars(statement).first()

    def daily_score_select_all(self) -> list[DailyScore]:
        statement = select(DailyScore).order_by(DailyScore.file_date)

        with self.Session() as session:
            return session.scalars(statement).all()

    def daily_score_update(self, args: dict[str, any]) -> DailyScore:
        with self.Session() as session:
            candidate = session.scalars(
                select(DailyScore).filter_by(
                    file_date=args["file_date"],
                    platform=args["platform"],
                    site=args["site"],
                )
            ).first()

            if candidate is not None:
                candidate.bssid_new = args["bssid_new"]
                candidate.bssid_total = args["bssid_total"]
                candidate.bssid_unique = args["bssid_unique"]
                candidate.file_quantity = args["file_quantity"]

                session.add(candidate)
                session.commit()

                return candidate

    def geo_loc_insert(self, args: dict[str, any]) -> GeoLoc:
        candidate = GeoLoc(args)

        try:
            with self.Session() as session:
                session.add(candidate)
                session.commit()
        except Exception as error:
            print(error)

        return candidate

    def geo_loc_select_by_id(self, id: int) -> Wap:
        with self.Session() as session:
            return session.scalars(select(GeoLoc).filter_by(id=id)).first()

    #    def geo_loc_select_by_load_log(self, load_log_id: int) -> GeoLoc:
    #        statement = (select(GeoLoc).filter_by(load_log_id=load_log_id).order_by(GeoLoc.fix_time))
    #
    #        with self.Session() as session:
    #            return session.scalars(statement).first()

    def geo_loc_select_by_site(self, site: str) -> list[GeoLoc]:
        statement = select(GeoLoc).filter_by(site=site).order_by(GeoLoc.fix_time)

        with self.Session() as session:
            return session.scalars(statement).all()

    def geo_loc_select_by_time_and_site(self, fix_time: datetime, site: str) -> GeoLoc:
        statement = (
            select(GeoLoc)
            .filter_by(site=site, fix_time=fix_time)
            .order_by(GeoLoc.fix_time)
        )

        with self.Session() as session:
            return session.scalars(statement).first()

    def geo_loc_select_or_insert(self, args: dict[str, any]) -> GeoLoc:
        if args["site"].startswith("mobile"):
            return self.geo_loc_insert(args)

        candidate = self.geo_loc_select_by_site(args["site"])
        if len(candidate) < 1:
            return self.geo_loc_insert(args)
        else:
            return candidate[0]

    def load_log_insert(
        self, args: dict[str, any], obs_quantity: int, geo_loc_id: int
    ) -> LoadLog:
        args["duration_ms"] = 0
        args["file_date"] = args["file_time"].date()
        args["obs_quantity"] = obs_quantity

        candidate = LoadLog(args, geo_loc_id)

        try:
            with self.Session() as session:
                session.add(candidate)
                session.commit()
        except Exception as error:
            print(error)

        return candidate

    def load_log_select_all(self) -> list[LoadLog]:
        with self.Session() as session:
            return session.scalars(select(LoadLog)).all()

    def load_log_select_by_file_name(self, file_name: str) -> LoadLog:
        with self.Session() as session:
            return session.scalars(
                select(LoadLog).filter_by(file_name=file_name)
            ).first()

    def load_log_select_by_file_date(self, target: datetime) -> list[LoadLog]:
        with self.Session() as session:
            return session.scalars(select(LoadLog).filter_by(file_date=target)).all()
        
    def load_log_update_duration(self, duration: int, load_log_id: int) -> LoadLog:
        with self.Session() as session:
            candidate = session.scalars(select(LoadLog).filter_by(id=load_log_id)).first()

            if candidate is None:
                print(f"skipping update duration for load log id {load_log_id}")
            else:
                candidate.duration_ms = duration

                session.add(candidate)
                session.commit()

                return candidate
        
    def observation_insert(
        self, args: dict[str, any], load_log_id: int, wap_id: int
    ) -> Observation:
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
        statement = select(Observation).filter_by(bssid=bssid, load_log_id=load_log_id)

        with self.Session() as session:
            return session.scalars(statement).all()

    def observation_select_by_load_log(self, load_log_id: int) -> list[Observation]:
        statement = (
            select(Observation)
            .filter_by(load_log_id=load_log_id)
            .order_by(Observation.bssid)
        )

        with self.Session() as session:
            return session.scalars(statement).all()

    def observation_select_by_wap_id(self, wap_id: int) -> list[Observation]:
        statement = (
            select(Observation).filter_by(wap_id=wap_id).order_by(Observation.file_time)
        )

        with self.Session() as session:
            return session.scalars(statement).all()

    def wap_insert(self, args: dict[str, any], version: int) -> Wap:
        args["update_flag"] = True
        candidate = Wap(args, version)

        try:
            with self.Session() as session:
                session.add(candidate)
                session.commit()
        except Exception as error:
            print(error)

        return candidate

    def wap_select_all(self) -> list[Wap]:
        with self.Session() as session:
            return session.scalars(select(Wap)).all()

    def wap_select_by_bssid(self, bssid: str) -> list[Wap]:
        statement = select(Wap).filter_by(bssid=bssid.lower()).order_by(Wap.version)

        with self.Session() as session:
            return session.scalars(statement).all()

    def wap_select_by_id(self, wap_id: int) -> Wap:
        with self.Session() as session:
            return session.scalars(select(Wap).filter_by(id=wap_id)).first()

    def wap_select_or_insert(self, args: dict[str, any]) -> Wap:
        rows = self.wap_select_by_bssid(args["bssid"])
        for row in rows:
            if (
                row.capability == args["capability"]
                and row.frequency_mhz == args["frequency_mhz"]
                and row.ssid == args["ssid"]
            ):
                return row

        if len(rows) < 1:
            version = 1
        else:
            version = 1 + row.version

        result = self.wap_insert(args, version)
        return result

    def wap_update_flag(self, update_flag: bool, wap_id: int) -> Wap:
        with self.Session() as session:
            candidate = session.scalars(select(Wap).filter_by(id=wap_id)).first()

            if candidate is None:
                print(f"skipping update flag for wap id {wap_id}")
            else:
                candidate.update_flag = update_flag

                session.add(candidate)
                session.commit()

                return candidate

    def weekly_rank_detail_bump(self, wap_id: int, weekly_rank_id: int) -> WeeklyRankDetail:
        with self.Session() as session:
            candidate = session.scalars(select(WeeklyRankDetail).filter_by(wap_id=wap_id, weekly_rank_id=weekly_rank_id)).first()

            if candidate is None:
                return self.weekly_rank_detail_insert(wap_id, weekly_rank_id)
            else:
                candidate.obs_quantity += 1

                session.add(candidate)
                session.commit()

                return candidate                

    def weekly_rank_detail_insert(self, wap_id: int, weekly_rank_id: int) -> WeeklyRankDetail:
        candidate = WeeklyRankDetail(1, wap_id, weekly_rank_id)

        try:
            with self.Session() as session:
                session.add(candidate)
                session.commit()
        except Exception as error:
            print(error)

        return candidate        

    def weekly_rank_insert(self, platform: str, site: str, start_date: datetime.date) -> WeeklyRank:
        args = {
            "platform": platform,
            "site": site,
            "start_date": start_date,
            "stop_date": start_date + datetime.timedelta(days=6),
        }
        
        candidate = WeeklyRank(args)

        try:
            with self.Session() as session:
                session.add(candidate)
                session.commit()
        except Exception as error:
            print(error)

        return candidate
            
    def weekly_rank_select_or_insert(self, platform: str, site: str, start_date: datetime.date) -> WeeklyRank:
        with self.Session() as session:
            candidate = session.scalars(select(WeeklyRank).filter_by(platform=platform, site=site, start_date=start_date)).first()

            if candidate is None:
                return self.weekly_rank_insert(platform, site, start_date)

            return candidate

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
