#
# Title: reporter.py
# Description: write box scores as markdown
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

import postgres

class Reporter:
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

    def execute(self) -> None:
        selected = self.postgres.box_score_select_all()

        for row in selected:
            print(f"|{row.file_date}|{row.site}|{row.platform}|{row.file_population}|{row.bssid_total}|{row.bssid_unique}|{row.bssid_new}|")

            

print("start reporter")

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

    reporter = Reporter(configuration)
    reporter.execute()

print("stop reporter")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
