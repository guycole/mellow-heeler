#
# Title: scorer.py
# Description: daily boxscore
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
from asyncio.log import logger
import datetime
import json
import os

from postgres import PostGres
from sql_table import LoadLog


class Scorer:

    def __init__(self, postgres: PostGres):
        self.postgres = postgres

    def select_dates(self) -> list[datetime.date]:
        results = []

        try:
            rows = self.postgres.load_log_select_all()
            for row in rows:
                candidate = row.file_time.date()
                if candidate not in results:
                    results.append(candidate)
        except Exception as error:
            logger.error(f"postgres select all failed: {error}")

        return results

    def score_date(self, target: datetime.date) -> None:
        logger.info(f"scoring {target}")

        results = {}

        try:
            rows = self.postgres.load_log_select_all_by_date(target)
            for row in rows:
                if row.platform in results:
                    results[row.platform]["file_quantity"] += 1
                    results[row.platform]["obs_quantity"] += row.obs_quantity
                else:
                    results[row.platform] = {
                        "file_quantity": 1,
                        "obs_quantity": row.obs_quantity,
                        "platform": row.platform,
                    }
        except Exception as error:
            logger.error(f"postgres select all failed: {error}")

        try:
            for platform in results:
                args = {
                    "score_date": target,
                    "file_quantity": results[platform]["file_quantity"],
                    "obs_quantity": results[platform]["obs_quantity"],
                    "platform": platform,
                }
                self.postgres.daily_score_insert_or_update(args)
        except Exception as error:
            logger.error(f"postgres update failed: {error}")

    def scorer(self):
        logger.info("scorer")
        dates = self.select_dates()
        for date in dates:
            self.score_date(date)


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
