#
# Title: heeler1.py
# Description:
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
import datetime
import json
import os
import pytz
import sys

import yaml
from yaml.loader import SafeLoader

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

from observation import Observation
from preamble import PreambleHelper

import postgres

from converter import Converter
from sql_table import GeoLoc


class Heeler1:
    file_type = "heeler_1"

    def __init__(self, postgres: postgres.PostGres, preamble: dict[str, any]):
        self.postgres = postgres
        self.preamble = preamble

    def store_geo_loc(self, load_log_id: int) -> GeoLoc:
        geo_loc = self.preamble["geoLoc"]
        site = geo_loc["site"]

        # replace this with "select or insert"?
        location = None
        if site.startswith("mobile"):
            location = self.postgres.geo_loc_insert(geo_loc, load_log_id)
        else:
            location = self.postgres.geo_loc_select_by_site(site)
            if location is None:
                raise Exception(f"unknown geo_loc site:{site}")

        return location

    def execute(self, obs_list: list[Observation]) -> bool:
        print(f"========> heeler1 execute {self.preamble}")

        load_log = self.postgres.load_log_insert(
            self.preamble, len(obs_list), self.preamble["geoLoc"]["site"]
        )

        location = self.postgres.geo_loc_select_or_insert(
            self.preamble["geoLoc"], load_log.id
        )

        for obs in obs_list:
            wap = self.postgres.wap_select_or_insert(obs, load_log.id)

            obs["file_date"] = load_log.file_date
            self.postgres.observation_insert(obs, load_log.id, wap.id)

        return True

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
