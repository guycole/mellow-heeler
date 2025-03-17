#
# Title: test_postgres06.py
# Description: exercise box_score
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

    def fresh_box_score(self) -> dict[str, any]:
        args = {}

        args["bssid_new"] = 43
        args["bssid_total"] = 54
        args["bssid_unique"] = 21
        args["file_date"] = "2000-2-2"
        args["file_quantity"] = 66
        args["platform"] = "pytest"
        args["site"] = "pytest"

        return args

    def test1(self):
        """cooked select"""

        self.pg_setup()

        box_score1 = self.postgres.box_score_select("2000-3-3", "pytest", "pytest")
        print(box_score1)
        assert box_score1 is None

        box_score2 = self.postgres.box_score_select("2000-1-1", "pytest", "pytest")
        assert box_score2 is not None
        assert box_score2.file_quantity == 78

    def test2(self):
        """observation insert"""

        self.pg_setup()

        args = self.fresh_box_score()
        box_score1 = self.postgres.box_score_insert(args)
        assert box_score1 is not None

        args["file_population"] = 77
        box_score2 = self.postgres.box_score_update(args)
        assert box_score2 is not None


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
