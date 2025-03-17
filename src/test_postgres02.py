#
# Title: test_postgres02.py
# Description: exercise geo_loc
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

    def fresh_geo_loc(self) -> dict[str, any]:
        args = {}

        args["altitude"] = random.randint(1, 999)
        args["fix_time"] = datetime.datetime.now(pytz.utc)
        args["latitude"] = random.randint(1, 900)/10.0
        args["longitude"] = random.randint(1, 1800)/10.0
        args["site"] = "pytest"
        args["speed"] = random.randint(1, 999)/10.0
        args["track"] = random.randint(1, 3609)/10.0

        return args

    def test1(self):
        """geo_loc select"""

        self.pg_setup()

        geo_loc1 = self.postgres.geo_loc_select_by_site("match nothing")
        assert len(geo_loc1) < 1

        geo_loc2 = self.postgres.geo_loc_select_by_site("anderson1")
        assert len(geo_loc2) == 1
        assert geo_loc2[0].site == "anderson1"

        geo_loc3 = self.postgres.geo_loc_select_by_load_log(2)
        assert len(geo_loc3) > 0
        assert geo_loc3[0].site == "vallejo1"

    def test2(self):
        """geo_loc insert"""

        self.pg_setup()

        args = self.fresh_geo_loc()

        geo_loc1 = self.postgres.geo_loc_insert(args, 1)
        print(geo_loc1)
        assert geo_loc1 is not None
        assert geo_loc1.id > 1

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
