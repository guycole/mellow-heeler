#
# Title: heeler1.py
# Description:
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
import datetime
import json
import os
import sys

import yaml
from yaml.loader import SafeLoader

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

from observation import Observation
from preamble import PreambleHelper

import os
import postgres

from converter import Converter
from sql_table import GeoLoc, Cooked


class Heeler1:
    file_type = "heeler_1"

    def __init__(self, postgres: postgres.PostGres, preamble: dict[str, any]):
        self.postgres = postgres
        self.preamble = preamble

    def cooked(self, file_time: datetime, wap_id: int) -> Cooked:
        cooked = self.postgres.cooked_select_by_wap_id(wap_id)
        if cooked is None:
            args = {
                "confidence": 0,
                "latitude": 0,
                "longitude": 0,
                "note": "noNote",
                "obs_quantity": 1,
                "obs_first": file_time,
                "obs_last": file_time,
                "street_address": "noAddress",
                "street_zip": "noZip",
            }

            cooked = self.postgres.cooked_insert(args, wap_id)
        else:
            if cooked.obs_first < file_time:
                first_time = cooked.obs_first
            else:
                first_time = file_time

            if cooked.obs_last < file_time:
                last_time = file_time
            else:
                last_time = cooked.obs_last

            args = {
                "confidence": cooked.confidence,
                "latitude": cooked.latitude,
                "longitude": cooked.longitude,
                "note": cooked.note,
                "obs_quantity": 1 + cooked.obs_quantity,
                "obs_first": first_time,
                "obs_last": last_time,
                "street_address": cooked.street_address,
                "street_zip": cooked.street_zip,
            }

            cooked = self.postgres.cooked_update_by_wap_id(args, wap_id)

        return cooked

    def find_monday(self, file_date: datetime.date) -> datetime.date:
        current_day = file_date

        while current_day.weekday() != 0:
            current_day = current_day - datetime.timedelta(days=1)

        return current_day
    
    def weekly(self, file_date: datetime.date, platform: str, site: str, wap_id:int) -> None:
        start_date = self.find_monday(file_date)
#        print(f"monday: {start_date} file_date: {file_date}")

        weekly_rank = self.postgres.weekly_rank_select_or_insert(platform, site, start_date)
#        print(weekly_rank)

        detail = self.postgres.weekly_rank_detail_bump(wap_id, weekly_rank.id)
#        print(detail)

    def execute(self) -> bool:
        #        print(f"========> heeler1 execute {self.preamble}")

        obs_list = self.preamble["wifi"]
        if len(obs_list) < 1:
            print(f"skipping file with no observations {self.preamble['file_name']}")
            return False

        geo_loc = self.postgres.geo_loc_select_or_insert(self.preamble["geoLoc"])
#        print(f"geo loc: {geo_loc.id} {geo_loc}")

        load_log = self.postgres.load_log_insert(
            self.preamble, len(obs_list), geo_loc.id
        )

        if load_log.id is None:
            print(f"load log insert failure for {self.preamble['file_name']}")
            return False

        start_time = datetime.datetime.now()

        for obs in obs_list:
            wap = self.postgres.wap_select_or_insert(obs)
            if wap.id is None:
                print(
                    f"wap select/insert failure for {obs} {self.preamble['file_name']}"
                )
            else:
                if wap.update_flag is False:
                    self.postgres.wap_update_flag(True, wap_id)

                obs["file_time"] = load_log.file_time
                self.postgres.observation_insert(obs, load_log.id, wap.id)

                cooked = self.cooked(load_log.file_time, wap.id)

                weekly = self.weekly(load_log.file_date, load_log.platform, "site", wap.id)

        stop_time = datetime.datetime.now()
        duration = stop_time - start_time

        self.postgres.load_log_update_duration(duration.microseconds, load_log.id)

        return True


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
