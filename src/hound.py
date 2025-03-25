#
# Title: hound.py
# Description: parse mellow hound files and load to postgresql
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

import heeler
import postgres

from converter import Converter

class Loader:
    """mellow heeler file parser and database loader"""

    def __init__(self, configuration: dict[str, str]):
        self.db_conn = configuration["dbConn"]
        self.dry_run = configuration["dryRun"]
        self.archive_dir = configuration["archiveDir"]
        self.failure_dir = configuration["failureDir"]
        self.fresh_dir = "/var/mellow/heeler/rawap"
        self.sql_echo = configuration["sqlEchoEnable"]

        connect_dict = {"options": "-csearch_path={}".format("heeler_v1")}
        db_engine = create_engine(
            self.db_conn, echo=self.sql_echo, connect_args=connect_dict
        )

        self.postgres = postgres.PostGres(
            sessionmaker(bind=db_engine, expire_on_commit=False)
        )

        self.failure_counter = 0
        self.success_counter = 0
        
    def add_geoloc(self, preamble: dict[str, any]) -> None:
        result = {}

        result["mode"] = 3
        result["altitude"] = self.json_datum['geoLoc']['altitude']
        result["errors"] = None
        result["latitude"] = self.json_datum['geoLoc']['latitude']
        result["longitude"] = self.json_datum['geoLoc']['longitude']
        result["site"] = "mobile2"
        result["speed"] = 0
        result["fixTime"] = int(self.json_datum['geoLoc']['fixTimeMs'])//1000
        result["fix_time"] = datetime.datetime.fromtimestamp(result["fixTime"], pytz.utc)
        result["track"] = 0

        preamble['geoLoc'] = result

        preamble['file_time'] = result["fix_time"]

    def add_wifi(self, preamble: dict[str, any]) -> None:
        results = []
        
        wifi = self.json_datum['wiFi']
        for wap in wifi:
            wap['frequency_mhz'] = wap['frequency']
            wap['signal_dbm'] = wap['level']

            obs1 = Observation(wap)
            obs2 = obs1.to_dict()
            obs2['capability'] = wap['capability']
            results.append(obs2)

        preamble['wifi'] = results

    def file_reader(self, file_name: str) -> bool:
        try:
            with open(file_name, "r", encoding="utf-8") as in_file:
                raw_buffer = in_file.readlines()
                if len(raw_buffer) > 1:
                    # this is a mellow heeler file
                    return False

                self.json_datum = json.loads(raw_buffer[0])
        except Exception as error:
            print(error)
            return False

        return True

    def file_success(self, file_name: str):
        """file was successfully processed"""

        self.success_counter += 1

        if self.dry_run is True:
            print(f"skip archive move for {file_name}")
        else:
            os.rename(file_name, self.archive_dir + "/" + file_name)

    def file_failure(self, file_name: str):
        """problem file, retain for review"""

        self.failure_counter += 1

        if self.dry_run is True:
            print(f"skip failure move for {file_name}")
        else:
            print(f"failure move for {file_name}")
            os.rename(file_name, self.failure_dir + "/" + file_name)

    def execute(self) -> None:
        print(f"fresh dir:{self.fresh_dir}")
        os.chdir(self.fresh_dir)
        targets = os.listdir(".")
        print(f"{len(targets)} files noted")

        converter = Converter()
        preamble_helper = PreambleHelper()

        for target in targets:
            if os.path.isfile(target) is False:
                continue

            # test for duplicate file
            selected = self.postgres.load_log_select_by_file_name(target)
            if len(selected) > 0:
                print(f"skip duplicate file:{target}")
                self.file_failure(target)
                continue
            
            if self.file_reader(target) is False:
                print(f"skip heeler file:{target}")
                self.file_failure(target)
                continue

            preamble = preamble_helper.preamble_factory("android", "mobile2", None)
            self.add_geoloc(preamble)
            self.add_wifi(preamble)

            preamble['file_name'] = target
            preamble['file_type'] = "hound_1"
            preamble['project'] = 'hound'
            preamble['zTime'] = int(preamble['geoLoc']['fixTime'])//1000
            print(preamble)

            load_log = self.postgres.load_log_insert(preamble, len(preamble['wifi']), "mobile2")
            if load_log.id is None:
                print("bad load log")
                self.file_failure(target)
                continue

            location = self.postgres.geo_loc_select_or_insert(preamble["geoLoc"], load_log.id)
            if location.id is None:
                print("bad location")
                self.file_failure(target)
                continue

            wifi = preamble['wifi']
            for obs in wifi:
                wap = self.postgres.wap_select_or_insert(obs, load_log.id)

                obs["file_date"] = load_log.file_date
                self.postgres.observation_insert(obs, load_log.id, wap.id)



#            if result_flag is True:
#                self.file_success(target)
#            else:
#                self.file_failure(target)

        print(f"success:{self.success_counter} failure:{self.failure_counter}")


print("start loader")

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

    loader = Loader(configuration)
    loader.execute()

print("stop loader")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
