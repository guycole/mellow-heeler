"""utility to refresh box_score table values"""

import json
import os
import sys

import calendar
import datetime
import time

from typing import Dict

import pytz

from typing import List

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

import yaml
from yaml.loader import SafeLoader

from heeler import Heeler
from hound import Hound
from postgres import PostGres
from sql_table import BoxScore

class BoxScore:
    """utility to refresh box_score table values"""

    db_conn = None
    dry_run = False

    def __init__(self, db_conn: str, dry_run: bool):
        self.db_conn = db_conn
        self.dry_run = dry_run

    def daily_bissid_total(self, candidate:BoxScore, postgres:PostGres) -> int:
        """calculate bssid total"""

        start_time = datetime.datetime(candidate.score_date.year, candidate.score_date.month, candidate.score_date.day, 0, 0, 0, 0, pytz.utc)
        start_time_ms = 1000 * calendar.timegm(start_time.timetuple())

        stop_time = datetime.datetime(candidate.score_date.year, candidate.score_date.month, candidate.score_date.day, 23, 59, 59, 999999, pytz.utc)
        stop_time_ms = 1000 * calendar.timegm(stop_time.timetuple())

        return postgres.observation_count(start_time_ms, stop_time_ms, candidate.device)

    def execute(self):
        """process all box_score rows"""

        db_engine = create_engine(self.db_conn)
        postgres = PostGres(sessionmaker(bind=db_engine, expire_on_commit=False), False)
        # postgres = PostGres(sessionmaker(bind=db_engine, expire_on_commit=False), self.dry_run)

        box_score_rows = postgres.box_score_select_refresh()
        for box_score in box_score_rows:
            population = self.daily_bissid_total(box_score, postgres)

            box_score.bssid_total = population
            box_score.refresh_flag = False

            postgres.box_score_refresh(box_score)

print("start box_score")

#
# argv[1] = configuration filename
#
if __name__ == "__main__":
    if len(sys.argv) > 1:
        config_name = sys.argv[1]
    else:
        config_name = "config.yaml"

    with open(config_name, "r", encoding="utf-8") as stream:
        try:
            configuration = yaml.load(stream, Loader=SafeLoader)
        except yaml.YAMLError as exc:
            print(exc)

    driver = BoxScore(configuration["dbConn"], configuration["dryRun"])
    driver.execute()

#    bsdr = BoxScoreDateRange()
#    bsdr.date_range(1698813722129)

print("stop box_score")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
