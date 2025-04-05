#
# Title: reporter.py
# Description: write box scores as markdown
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
import datetime
import sys

import yaml
from yaml.loader import SafeLoader

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

import postgres

from sql_table import WeeklyRank, WeeklyRankDetail

class DailyScores:
    daily_scores = {}

    def __init__(self, configuration: dict[str, str]):
        self.report_dir = configuration["reportDir"]
        self.db_conn = configuration["dbConn"]
        self.sql_echo = configuration["sqlEchoEnable"]

        connect_dict = {"options": "-csearch_path={}".format("heeler_v1")}
        db_engine = create_engine(
            self.db_conn, echo=self.sql_echo, connect_args=connect_dict
        )

        self.postgres = postgres.PostGres(
            sessionmaker(bind=db_engine, expire_on_commit=False)
        )

    def converter(self):
        """convert from postgres daily score to markdown"""

        selected = self.postgres.daily_score_select_all()
        if len(selected) < 1:
            print("empty daily score select")
            return

        for row in selected:
            row_year = row.file_date.year
            if row_year not in self.daily_scores:
                self.daily_scores[row_year] = []

            temp = self.daily_scores[row_year]

            candidate = f"|{row.file_date}|{row.site}|{row.platform}|{row.file_quantity}|{row.bssid_total}|{row.bssid_unique}|{row.bssid_new}|\n"

            temp.append(candidate)

    def execute(self) -> None:
        self.converter()

        time_now = datetime.datetime.now()
        banner2 = f"created at {time_now}\n\n"
        banner3 = (
            f"|date|site|platform|file total|bssid total|bssid unique|bssid new|\n"
        )
        banner4 = f"|--|--|--|--|--|--|--|\n"

        for key, values in self.daily_scores.items():
            file_name = f"{self.report_dir}/{key}.md"
            print(f"creating file: {file_name}")

            banner1 = f"mellow-heeler collection scores for {key}\n\n"

            try:
                with open(file_name, "w", encoding="utf-8") as out_file:
                    out_file.write(banner1)
                    out_file.write(banner2)
                    out_file.write(banner3)
                    out_file.write(banner4)

                    for value in values:
                        out_file.write(value)
            except Exception as error:
                print(error)
                return None

class WeeklyScores:
    candidates = {}

    def __init__(self, configuration: dict[str, str]):
        self.report_dir = configuration["reportDir"]
        self.db_conn = configuration["dbConn"]
        self.sql_echo = configuration["sqlEchoEnable"]

        connect_dict = {"options": "-csearch_path={}".format("heeler_v1")}
        db_engine = create_engine(
            self.db_conn, echo=self.sql_echo, connect_args=connect_dict
        )

        self.postgres = postgres.PostGres(
            sessionmaker(bind=db_engine, expire_on_commit=False)
        )

    
    def converter(self, weekly_rank: WeeklyRank):
        self.candidates.clear()

        selected = self.postgres.weekly_rank_detail_select(weekly_rank.id)

        limit = 5

        counter = 0
        for ndx in range(len(selected)):
            counter += 1
            
        

    def write_weekly(self, weekly_rank: WeeklyRank) -> None:
        time_now = datetime.datetime.now()

#        banner2 = f"created at {time_now} total rows {len(selected)}\n\n"
        banner3 = (f"|date|site|platform|obs total|bssid|ssid|\n")
        banner4 = f"|--|--|--|--|--|--|\n"

        geo_loc = self.postgres.geo_loc_select_by_id(weekly_rank.geo_loc_id)
        if geo_loc.site.startswith("mobile"):
            print("skipping mobile report")
            return

        key = f"{weekly_rank.start_date}-{geo_loc.site}-{weekly_rank.platform}"
        print(key)

        file_name = f"{self.report_dir}/{key}.md"
        print(f"creating file: {file_name}")

        selected = self.postgres.weekly_rank_detail_select(weekly_rank.id)
            
        for row in selected:
            print(row)
        
    def execute(self) -> None:
        rows = self.postgres.weekly_rank_select_all()

        for row in rows:
            self.write_weekly(row)

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

#    reporter = DailyScores(configuration)
#    reporter.execute()
    reporter = WeeklyScores(configuration)
    reporter.execute()

print("stop reporter")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
