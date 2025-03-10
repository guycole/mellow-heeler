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

class BoxScore:
    """daily station statistics"""

    # key = date_platform_site
    box_scores = {}

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

    def fresh_box_score(self, file_date: datetime.date, platform: str, site: str) -> dict[str, any]:
        return {
            "bssid_new": 0,
            "bssid_total": 0,
            "bssid_updated": 0,
            "file_date": file_date,
            "file_population": 0,
            "platform": platform,
            "refresh_flag": False,
            "site": site,
        }

    def process_daily(self, current_day: datetime.date) -> None:
        # select files loaded for today
        load_log_rows = self.postgres.load_log_select_by_date(current_day)

        # discover platform and sites for today
        for row in load_log_rows:
            box_scores_key = f"{row.file_date}_{row.platform}_{row.site}"

            if box_scores_key in self.box_scores:
                self.box_scores[box_scores_key]["bssid_total"] += row.obs_population
            else:
                fresh = self.fresh_box_score(row.file_date, row.platform, row.site)
                fresh["bssid_total"] = row.obs_population
                self.box_scores[box_scores_key] = fresh

        for value in self.box_scores.values():
            self.postgres.box_score_update(value)

    def execute(self) -> None:
        today = datetime.datetime.now(pytz.utc)
        current_day = datetime.date(2024, 1, 1)

        while current_day < today.date():
            print(f"{current_day}")

            self.process_daily(current_day)

            current_day = current_day + datetime.timedelta(days=1)

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
