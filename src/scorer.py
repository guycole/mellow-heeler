#
# Title: scorer.py
# Description: calculate daily box score
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


class BoxScore:
    """daily station statistics"""

    # key = date_platform_site
    daily_scores = {}

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

    def fresh_daily_score(
        self, file_date: datetime.date, platform: str, site: str
    ) -> dict[str, any]:
        return {
            "bssid_new": 0,
            "bssid_total": 0,
            "bssid_unique": 0,
            "file_date": file_date,
            "file_quantity": 1,
            "platform": platform,
            "refresh_flag": False,
            "site": site,
            "wap_list": [],
        }

    def pass1(self, current_day: datetime.date) -> None:
        # select files loaded for today
        load_log_rows = self.postgres.load_log_select_by_file_date(current_day)
        print(f"load log quantity {len(load_log_rows)} for {current_day}")

        # discover platform and sites for today
        for row in load_log_rows:
            geo_loc_row = self.postgres.geo_loc_select_by_id(row.geo_loc_id)

            daily_scores_key = f"{row.file_date}-{row.platform}-{geo_loc_row.site}"
            # 2025-03-13-rpi3a-vallejo1

            if daily_scores_key in self.daily_scores:
                # print(f"existing key {box_scores_key}")
                self.daily_scores[daily_scores_key]["file_quantity"] += 1
            else:
                # print(f"fresh key {daily_scores_key}")
                self.daily_scores[daily_scores_key] = self.fresh_daily_score(
                    row.file_date, row.platform, geo_loc_row.site
                )

            # observed wap for today
            wap_list = self.daily_scores[daily_scores_key]["wap_list"]

            # observations for this load log file
            raw_obs_list = self.postgres.observation_select_by_load_log(row.id)
            self.daily_scores[daily_scores_key]["bssid_total"] += len(raw_obs_list)
            if len(raw_obs_list) != row.obs_quantity:
                print(f"mismatch {row.id} {row.obs_quantity} {len(raw_obs_list)}")

            # add wap for today
            for obs in raw_obs_list:
                if obs.wap_id not in wap_list:
                    wap_list.append(obs.wap_id)

    # determine new and unique wap
    def pass2(self) -> None:
        for key, value in self.box_scores.items():
            value["bssid_unique"] = len(value["wap_list"])
            for wap_id in value["wap_list"]:
                cooked = self.postgres.cooked_select_by_wap_id(wap_id)
                if cooked is None:
                    print(f"cooked select falure for wap id {wap_id} on {key}")
                    continue
                obs_first_date = cooked.obs_first.date()
                if obs_first_date == value["file_date"]:
                    value["bssid_new"] += 1

    # write to postgres
    def pass3(self) -> None:
        for key, value in self.box_scores.items():
            selected = self.postgres.box_score_select(
                value["file_date"], value["platform"], value["site"]
            )
            if selected is None:
                self.postgres.box_score_insert(value)
            else:
                self.postgres.box_score_update(value)

    def execute(self) -> None:
        today = datetime.datetime.now()
        current_day = datetime.date(2024, 2, 18)
        current_day = datetime.date(2021, 7, 4)
        limit_day = datetime.date(2024, 2, 25)
        limit_day = today.date()

        while current_day <= limit_day:
            self.pass1(current_day)
            current_day = current_day + datetime.timedelta(days=1)


#        self.pass2()
#        self.pass3()

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
