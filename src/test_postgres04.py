#
# Title: test_postgres04.py
# Description: exercise observation
# Development Environment: OS X 12.6.9/Python 3.11.5
# Author: G.S. Cole (guycole at gmail dot com)
#
import datetime
import pytz
import random
import uuid

import postgres

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

from unittest import TestCase


class TestPostgres(TestCase):

    load_log_anderson1 = "a89039a4-f75d-4bdc-af24-fcd8aadf3c09"
    load_log_vallejo1 = "5e7eb394-7ecd-40aa-b1c1-7527ea3a9e4f"
    load_log_pytest1 = "c4b1b876-f483-4ffd-9e5a-00426cac94aa"

    def pg_setup(self):
        sql_echo = False

        self.db_conn = (
            "postgresql+psycopg2://heeler_client:batabat@localhost:5432/heeler"
        )

        connect_dict = {"options": "-csearch_path={}".format("heeler_v1")}
        db_engine = create_engine(
            self.db_conn, echo=sql_echo, connect_args=connect_dict
        )

        self.postgres = postgres.PostGres(
            sessionmaker(bind=db_engine, expire_on_commit=False)
        )

    def fresh_obs(self) -> dict[str, any]:
        args = {}

        args["bssid"] = "11:22:33:44:55:66"
        args["file_date"] = "2000-1-1"
        args["signal_dbm"] = -23

        return args

    def test1(self):
        """observation select"""

        self.pg_setup()

        obs1 = self.postgres.observation_select_by_bssid_and_load_log("bogus", 3)
        assert len(obs1) < 1

        obs2 = self.postgres.observation_select_by_bssid_and_load_log("11:22:33:44:55:66", 3)
        assert len(obs2) > 0
        assert obs2[0].bssid == "11:22:33:44:55:66"

        obs3 = self.postgres.observation_select_by_wap_id(0)
        assert len(obs3) < 1

        obs4 = self.postgres.observation_select_by_wap_id(2)
        assert len(obs4) > 0

    def test2(self):
        """observation insert"""

        self.pg_setup()

        args = self.fresh_obs()

        obs1 = self.postgres.observation_insert(args, 3, 2)
        assert obs1 is not None


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
