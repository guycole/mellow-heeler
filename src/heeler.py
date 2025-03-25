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
from sql_table import GeoLoc


class Heeler1:
    file_type = "heeler_1"

    def __init__(self, postgres: postgres.PostGres, preamble: dict[str, any]):
        self.postgres = postgres
        self.preamble = preamble

    def execute(self) -> bool:
#        print(f"========> heeler1 execute {self.preamble}")

        obs_list = self.preamble['wifi']
        if len(obs_list) < 1:
            print(f"skipping file with no observations {self.preamble['file_name']}")

        load_log = self.postgres.load_log_insert(
            self.preamble, len(obs_list), self.preamble["geoLoc"]["site"]
        )

        if load_log.id is None:
            print(f"load log insert failure for {self.preamble['file_name']}")
            return False

        location = self.postgres.geo_loc_select_or_insert(
            self.preamble["geoLoc"], load_log.id
        )

        for obs in obs_list:
            wap = self.postgres.wap_select_or_insert(obs)

            obs["file_date"] = load_log.file_date
            self.postgres.observation_insert(obs, load_log.id, wap.id)

        return True


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
