#
# Title: test_postgres05.py
# Description: exercise cooked
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

    def fresh_cooked(self) -> dict[str, any]:
        return {
            "confidence": 5,
            "latitude": 43.21,
            "longitude": 12.34,
            "note": "fresh note",
            "obs_first": datetime.datetime.now(),
            "obs_last": datetime.datetime.now(),
            "obs_quantity": 3,
            "street_address": "123 nowhere",
            "street_zip": "54321",
        }

    def test1(self):
        """cooked select"""

        self.pg_setup()

        cooked1 = self.postgres.cooked_select_by_wap_id(1)
        assert cooked1 is None

        cooked2 = self.postgres.cooked_select_by_wap_id(2)
        assert cooked2 is not None
        assert cooked2.note == "default"

    def test2(self):
        """observation insert"""

        self.pg_setup()

        args = self.fresh_cooked()
        cooked1 = self.postgres.cooked_insert(args, 2)
        assert cooked1 is not None

        args["note"] = "updated note"
        cooked2 = self.postgres.cooked_update_by_wap_id(args, 2)
        assert cooked2 is not None


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
