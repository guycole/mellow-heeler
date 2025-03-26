#
# Title: exporter.py
# Description: dump database as json files
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

class Exporter:

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

    def fresh_file(self, file_name: str, platform: str, ztime: datetime) -> dict[str, any]:
        return {
            "file_name": file_name,
            "file_type": "heeler_1",
            "geoLoc": {},
            "platform": platform,
            "project": "heeler",
            "wifi": [],
            "version": 1,
            "zTime": int(ztime.timestamp())
        }

    def geo_loc(self, id: int) -> dict[str, any]:
        row = self.postgres.geo_loc_select_by_id(id)
        if row is None:
            print(f"empty geoloc for {load_log_id}")
        
        return {
            "altitude": row.altitude,
            "fix_time": int(row.fix_time.timestamp()),
            "latitude": row.latitude,
            "longitude": row.longitude,
            "site": row.site,
            "speed": row.speed,
            "track": row.track
        }

    def wifi(self, load_log_id: int) -> dict[str, any]:
        result = []

        obs_list = self.postgres.observation_select_by_load_log(load_log_id)
        for obs in obs_list:
            wap = self.postgres.wap_select_by_id(obs.wap_id)

            temp = {
                "bssid": obs.bssid,
                "capability": wap.capability,
#               "file_time": obs.file_time,
                "frequency_mhz": wap.frequency_mhz,
                "signal_dbm": obs.signal_dbm,
                "ssid": wap.ssid
            }

            result.append(temp)
        
        return result

    def assembler(self, load_log: LoadLog) -> dict[str, any]:
        print(f"{load_log.id} {load_log.file_name}")
        ff = self.fresh_file(load_log.file_name, load_log.platform, load_log.file_time)
        ff['geoLoc'] = self.geo_loc(load_log.geo_loc_id)
        ff['wifi'] = self.wifi(load_log.id)
        return ff

    def json_writer(self, payload: dict[str, any]) -> None:
        file_name = f"{self.export_dir}/{payload['file_name']}"
        print(file_name)

        try:
            with open(file_name, "w") as out_file:
                json.dump(payload, out_file, indent=4)
        except Exception as error:
            print(error)

    def execute(self) -> None:
        load_log_rows = self.postgres.load_log_select_all()
        print(f"load log quantity {len(load_log_rows)}")

        for row in load_log_rows:
            payload = self.assembler(row)
            self.json_writer(payload)

print("start exporter")

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

    exporter = Exporter(configuration)
    exporter.execute()

print("stop exporter")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
