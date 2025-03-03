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

class Heeler1:
    file_type = "heeler_1"

    def __init__(self, file_name: str, preamble: dict[str, any], postgres: postgres.PostGres):
        self.file_name = file_name
        self.postgres = postgres
        self.preamble = preamble

    def store_geo_loc(self) -> None:
        print(f"write location {location}")


    def execute(self, obs_list: list[Observation]) -> bool:
        print(f"========> heeler1 execute {self.preamble}")

        load_log = self.postgres.load_log_insert(self.file_name, self.file_type, len(obs_list))

        # store geo location
        # store wap
        # store observation
        return True

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
