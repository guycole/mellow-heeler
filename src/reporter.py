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


class Reporter:
    box_scores = {}

    def __init__(self, configuration: dict[str, str]):
        self.box_score_dir = configuration["boxScoreDir"]
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
        """convert from postgres boxscore to markdown"""

        selected = self.postgres.box_score_select_all()
        if len(selected) < 1:
            print("empty box score select")
            return

        for row in selected:
            row_year = row.file_date.year
            if row_year not in self.box_scores:
                self.box_scores[row_year] = []

            temp = self.box_scores[row_year]

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

        for key, values in self.box_scores.items():
            file_name = f"{self.box_score_dir}/{key}.md"
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
