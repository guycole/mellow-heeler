#
# Title: box_score.py
# Description: calculate daily box score
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

from converter import Converter

class BoxScore:
    """daily station statistics"""

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

    def origin_day_start(self) -> datetime.datetime:
        return datetime.datetime(2024, 2, 15, 0, 0, tzinfo=datetime.timezone.utc)

    def origin_day_stop(self) -> datetime.datetime:
        start = self.origin_day_start()

        year = start.year
        month = start.month
        day = start.day
        hour = 23
        minute = 59
        second = 59
        microsecond = 999999

        return datetime.datetime(year, month, day, hour, minute, second, microsecond, pytz.utc)
    
    def execute(self) -> None:
        today = datetime.datetime.now(pytz.utc)

        current_day_start = self.origin_day_start()
        current_day_stop = self.origin_day_stop()

        while current_day_start < today:
            print(f"{current_day_start} {current_day_stop}")

            current_day_start = current_day_start + datetime.timedelta(days=1)
            current_day_stop = current_day_stop + datetime.timedelta(days=1)

            load_log_rows = self.postgres.load_log_select_by_date(current_day_start, current_day_stop)
            for row in load_log_rows:
                print(row.id, row.file_time)

print("start scorer")

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

    scorer = BoxScore(configuration)
    scorer.execute()

print("stop scorer")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
