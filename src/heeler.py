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

    def __init__(self, file_name: str, preamble: dict[str, any], postgres: postgres.PostGres):
        self.file_name = file_name
        self.postgres = postgres
        self.preamble = preamble

    def store_geo_loc(self, load_log_id:int) -> GeoLoc:
        geo_loc = self.preamble['geoLoc']
        site = geo_loc['site']

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

        load_log = self.postgres.load_log_insert(self.file_name, self.file_type, len(obs_list))
        print(f"load log {load_log}")

        location = self.store_geo_loc(load_log.id) 
        print(f"location:{location}")

        for obs in obs_list:
            print(obs)

#        wap = self.postgres.wap_select_or_insert()

        # store wap
        # store observation
        return True

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
