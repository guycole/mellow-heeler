#
# Title: validator.py
# Description: ensure all schema elements are present
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
import os
import sys

import yaml
from yaml.loader import SafeLoader

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

from observation import Observation
from preamble import PreambleHelper
from sql_table import BoxScore, Cooked, GeoLoc, LoadLog, Observation, Wap

import postgres

class Validator:
    """mellow heeler file parser and database loader"""

    def __init__(self, configuration: dict[str, str]):
        self.db_conn = configuration["dbConn"]
        self.sql_echo = configuration["sqlEchoEnable"]

        connect_dict = {"options": "-csearch_path={}".format("heeler_v1")}
        db_engine = create_engine(
            self.db_conn, echo=self.sql_echo, connect_args=connect_dict
        )

        self.postgres = postgres.PostGres(
            sessionmaker(bind=db_engine, expire_on_commit=False)
        )

    def file_validator(self, candidate: LoadLog) -> None:
        print(f"file_validator {candidate.id} {candidate.site} {candidate.platform} {candidate.file_date}")

        if candidate.site.startswith("mobile"):
            geo_loc = self.postgres.geo_loc_select_by_load_log(candidate.id)
            if geo_loc is None:
                print(f"failure geo_loc {candidate.id} {candidate.site}")

        obs_list = self.postgres.observation_select_by_load_log(candidate.id)
        if len(obs_list) != candidate.obs_quantity:
            print(f"failure obs_list {candidate.id} {candidate.site} {len(obs_list)} {candidate.obs_quantity}")

        for obs in obs_list:
            wap = self.postgres.wap_select_by_id(obs.wap_id)
            if wap is None:
                print(f"failure wap {candidate.id} {candidate.site} {obs.wap_id}")

    def execute(self) -> None:
        load_log_rows = self.postgres.load_log_select_all()
        print(f"load log quantity {len(load_log_rows)}")

        for row in load_log_rows:
            if row.obs_quantity < 1:
                print(f"skipping no observations {row.id} {row.file_name}")
                continue

            self.file_validator(row)

print("start validator")

#
# argv[1] = configuration filename
#
if __name__ == "__main__":
    if len(sys.argv) > 1:
        config_name = sys.argv[1]
    else:
        config_name = "config.yaml"

    with open(config_name, "r", encoding="utf-8") as in_file:
        try:
            configuration = yaml.load(in_file, Loader=SafeLoader)
        except yaml.YAMLError as error:
            print(error)

    validator = Validator(configuration)
    validator.execute()

print("stop validator")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
