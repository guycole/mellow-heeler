#
# Title: test_postgres03.py
# Description: exercise wap
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

    def fresh_wap(self) -> dict[str, any]:
        args = {}

        args["bssid"] = "11:22:33:44:55:66"
        args["capability"] = "magick"
        args["frequency_mhz"] = 1234
        args["ssid"] = "pytest1"

        return args

    def test1(self):
        """wap select"""

        self.pg_setup()

        wap1 = self.postgres.wap_select_by_load_log(0)
        assert len(wap1) < 1

        wap1 = self.postgres.wap_select_by_load_log(3)
        assert len(wap1) > 0
        assert wap1[0].bssid == "11:22:33:44:55:66"
        assert wap1[0].ssid.startswith("pytest")

        wap2 = self.postgres.wap_select_by_bssid("11:22:33:44:55:66")
        assert len(wap2) > 1

        for wap in wap2:
            assert wap.bssid == "11:22:33:44:55:66"
            assert wap.ssid.startswith("pytest")

    def test2(self):
        """wap insert"""

        self.pg_setup()

        args = self.fresh_wap()

        # return selected wap
        wap1 = self.postgres.wap_select_or_insert(args, 3)
        assert wap1 is not None
        assert wap1.id == 1

        # new wap version
        args["ssid"] = "pytest" + str(random.randint(1, 999))
        wap2 = self.postgres.wap_select_or_insert(args, 3)
        assert wap2 is not None

        # new wap
        args["bssid"] = "a" + str(random.randint(1, 999))
        args["ssid"] = "pytest" + str(random.randint(1, 999))
        wap3 = self.postgres.wap_select_or_insert(args, 3)
        assert wap3 is not None
        assert wap3.version == 1


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
