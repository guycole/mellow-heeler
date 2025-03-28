#
# Title: importer.py
# Description: dump import from json files
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
from sql_table import BoxScore, Cooked, GeoLoc, LoadLog, Observation, Wap

import postgres


class Importer:

    def __init__(self, configuration: dict[str, str]):
        self.db_conn = configuration["dbConn"]
        self.export_dir = configuration["exportDir"]
        self.sql_echo = configuration["sqlEchoEnable"]

        connect_dict = {"options": "-csearch_path={}".format("heeler_v1")}
        db_engine = create_engine(
            self.db_conn, echo=self.sql_echo, connect_args=connect_dict
        )

        self.postgres = postgres.PostGres(
            sessionmaker(bind=db_engine, expire_on_commit=False)
        )

    def json_reader(self, file_name: str) -> dict[str, any]:
        results = {}

        try:
            with open(file_name, "r") as in_file:
                results = json.load(in_file)
        except Exception as error:
            print(error)

        return results

    def get_geo_loc(self, args: dict[str, any]) -> GeoLoc:
        stanza = args["geoLoc"]
        stanza["fix_time"] = datetime.datetime.fromtimestamp(stanza["fix_time"])

        geo_loc = self.postgres.geo_loc_select_or_insert(stanza)
        return geo_loc

    def get_load_log(self, args: dict[str, any], geo_loc_id: int) -> LoadLog:
        stanza = args["wifi"]
        args["file_time"] = datetime.datetime.fromtimestamp(args["zTime"])

        load_log = self.postgres.load_log_insert(args, len(stanza), geo_loc_id)
        return load_log

    def get_obs(self, args: dict[str, any], load_log_id: int) -> None:
        file_time = datetime.datetime.fromtimestamp(args["zTime"])

        for obs in args["wifi"]:
            wap = self.postgres.wap_select_or_insert(obs)

            obs["file_time"] = file_time
            obs = self.postgres.observation_insert(obs, load_log_id, wap.id)

    def execute(self) -> None:
        os.chdir(self.export_dir)
        targets = os.listdir(".")
        print(f"{len(targets)} files noted")

        for target in targets:
            if os.path.isfile(target) is False:
                continue

            # test for duplicate file
            selected = self.postgres.load_log_select_by_file_name(target)
            if selected is not None:
                print(f"skip duplicate file:{target}")
                continue

            buffer = self.json_reader(target)
            if len(buffer) < 1:
                print(f"skip json parse error:{target}")
                continue

            geo_loc = self.get_geo_loc(buffer)
            if geo_loc.id is None:
                print(f"skip bad geo_loc:{target}")
                continue

            load_log = self.get_load_log(buffer, geo_loc.id)
            if load_log.id is None:
                print(f"skip bad load_log:{target}")
                continue

            self.get_obs(buffer, load_log.id)


print("start importer")

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

    importer = Importer(configuration)
    importer.execute()

print("stop importer")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
