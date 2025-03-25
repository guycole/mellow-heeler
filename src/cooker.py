#
# Title: cooker.py
# Description: update cooked history
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
import sys

import yaml
from yaml.loader import SafeLoader

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

import datetime
import postgres


class Cooker:
    """WAP history"""

    # key = wap_id
    cooked = {}

    def __init__(self, configuration: dict[str, str]):
        self.db_conn = configuration["dbConn"]
        self.dry_run = configuration["dryRun"]
        self.sql_echo = configuration["sqlEchoEnable"]

        connect_dict = {"options": "-csearch_path={}".format("heeler_v1")}
        db_engine = create_engine(
            self.db_conn, echo=self.sql_echo, connect_args=connect_dict
        )

        self.postgres = postgres.PostGres(
            sessionmaker(bind=db_engine, expire_on_commit=False)
        )

    def fresh_results(self, wap_id: int) -> None:
        self.cooked[wap_id] = {
            "confidence": 0,
            "latitude": 0,
            "longitude": 0,
            "note": "noNote",
            "observed_quantity": 0,
            "observed_first": None,
            "observed_last": None,
            "street_address": "noAddress",
            "street_zip": "noZip",
        }

    def select_observations(self, wap_id: int) -> None:
        obs_list = self.postgres.observation_select_by_wap_id(wap_id)
        # print(f"{wap_id} {len(obs_list)}")
        if len(obs_list) < 1:
            self.cooked[wap_id]["obs_quantity"] = 0
            self.cooked[wap_id]["obs_first"] = datetime.datetime(2000, 1, 1, 0, 0)
            self.cooked[wap_id]["obs_last"] = datetime.datetime(2000, 1, 1, 0, 0)
        else:
            self.cooked[wap_id]["obs_quantity"] = len(obs_list)
            self.cooked[wap_id]["obs_first"] = obs_list[0].file_time
            self.cooked[wap_id]["obs_last"] = obs_list[-1].file_time

    def execute(self) -> None:
        wap_rows = self.postgres.wap_select_all()
        for wap in wap_rows:
            self.fresh_results(wap.id)
            self.select_observations(wap.id)

        for key in self.cooked:
            temp = self.postgres.cooked_select_by_wap_id(key)
            if temp is None:
                self.postgres.cooked_insert(self.cooked[key], key)
            else:
                self.postgres.cooked_update_by_wap_id(self.cooked[key], key)

        print(len(wap_rows))


print("start cooker")

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

    cooker = Cooker(configuration)
    cooker.execute()

print("stop cooker")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
