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

from sql_table import BoxScore, Cooked, GeoLoc, LoadLog, Observation, Wap


class PostGres:
    """mellow heeler postgresql support"""

    db_engine = None
    Session = None

    def __init__(self, session: sqlalchemy.orm.session.sessionmaker):
        self.Session = session

    def box_score_insert(self, args: dict[str, any]) -> BoxScore:
        candidate = BoxScore(args)
#        print(f"insert {candidate.id} {candidate.site} {candidate.file_date} {candidate.bssid_new}")

        try:
            with self.Session() as session:
                session.add(candidate)
                session.commit()
        except Exception as error:
            print(error)

        return candidate

    def box_score_select(self, file_date: datetime.date, platform: str, site: str) -> BoxScore:
        statement = select(BoxScore).filter_by(file_date=file_date, platform=platform, site=site)

        with self.Session() as session:
            return session.scalars(statement).first()

    def box_score_select_all(self) -> list[BoxScore]:
        statement = select(BoxScore).order_by(BoxScore.file_date)

        with self.Session() as session:
            return session.scalars(statement).all()

    def box_score_update(self, args: dict[str, any]) -> BoxScore:
        with self.Session() as session:
            candidate = session.scalars(select(BoxScore).filter_by(file_date=args["file_date"], platform=args["platform"], site=args["site"])).first()
            
            if candidate is not None:
                candidate.bssid_new = args["bssid_new"]
                candidate.bssid_total = args["bssid_total"]
                candidate.bssid_unique = args["bssid_unique"]
                candidate.file_quantity = args["file_quantity"]

                session.add(candidate)
                session.commit()

                return candidate

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

            if candidate is not None:
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

    def geo_loc_select_or_insert(self, args: dict[str, any]) -> GeoLoc:
        if args["site"].startswith("mobile"):
            return self.geo_loc_insert(args, load_log_id)

        candidate = self.geo_loc_select_by_site(args["site"])
        if len(candidate) < 1:
            return self.geo_loc_insert(args)
        else:
            return candidate[0]
            
    def load_log_insert(self, args: dict[str, any], obs_quantity: int, geo_loc_id: int) -> LoadLog:
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
            return session.scalars(select(LoadLog).filter_by(file_name=file_name)).first()

    def load_log_select_by_file_date(self, target: datetime) -> list[LoadLog]:
        with self.Session() as session:
            return session.scalars(select(LoadLog).filter_by(file_date=target)).all()

    def observation_insert(self, args: dict[str, any], load_log_id: int, wap_id: int) -> Observation:
        candidate = Observation(args, load_log_id, wap_id)

        try:
            with self.Session() as session:
                session.add(candidate)
                session.commit()
        except Exception as error:
            print(error)

        return candidate

    def observation_select_by_bssid_and_load_log(self, bssid: str, load_log_id: int) -> list[Observation]:
        statement = select(Observation).filter_by(bssid=bssid, load_log_id=load_log_id)

        with self.Session() as session:
            return session.scalars(statement).all()

    def observation_select_by_load_log(self, load_log_id: int) -> list[Observation]:
        statement = (select(Observation).filter_by(load_log_id=load_log_id).order_by(Observation.bssid))

        with self.Session() as session:
            return session.scalars(statement).all()
        
    def observation_select_by_wap_id(self, wap_id: int) -> list[Observation]:
        statement = (select(Observation).filter_by(wap_id=wap_id).order_by(Observation.file_time))

        with self.Session() as session:
            return session.scalars(statement).all()

    def wap_insert(self, args: dict[str, any], version: int) -> Wap:
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


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
