#
# Title: test_postgres01.py
# Description: exercise load log
# Development Environment: OS X 12.6.9/Python 3.11.5
# Author: G.S. Cole (guycole at gmail dot com)
#
import datetime
import random
import uuid

import postgres

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

from unittest import TestCase


class TestPostgres(TestCase):

    load_log_anderson1 = "a89039a4-f75d-4bdc-af24-fcd8aadf3c09"
    load_log_vallejo1 = "5e7eb394-7ecd-40aa-b1c1-7527ea3a9e4f"
    new_day_2000 = datetime.datetime(2000, 1, 1, 0, 0, tzinfo=datetime.timezone.utc)

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

    def fresh_load_log(self) -> dict[str, any]:
        args = {}

        args["file_name"] = str(uuid.uuid4())
        args["file_time"] = self.new_day_2000
        args["file_type"] = "pytest"
        args["platform"] = "x" + str(random.randint(1, 999))

        return args

    def test1(self):
        """load log select"""

        self.pg_setup()

        load_log1 = self.postgres.load_log_select_by_file_name(self.load_log_anderson1)
        assert len(load_log1) == 1
        assert load_log1[0].id == 1
        assert load_log1[0].file_name == self.load_log_anderson1
#        assert load_log1[0].file_time == self.new_day_2000
        assert load_log1[0].load_time is not None
        assert type(load_log1[0].load_time) == datetime.datetime
        assert load_log1[0].file_type == "synthetic"
        assert load_log1[0].obs_quantity == 0
        assert load_log1[0].platform == "dummy1"
        assert load_log1[0].site == "anderson1"

        load_log2 = self.postgres.load_log_select_by_file_name(self.load_log_vallejo1)
        assert len(load_log2) == 1
        assert load_log2[0].id == 2
        assert load_log2[0].file_name == self.load_log_vallejo1
#        assert load_log2[0].file_time == self.new_day_2000
        assert load_log2[0].load_time is not None
        assert type(load_log2[0].load_time) == datetime.datetime
        assert load_log2[0].file_type == "synthetic"
        assert load_log2[0].obs_quantity == 0
        assert load_log2[0].platform == "dummy2"
        assert load_log2[0].site == "vallejo1"

        
        load_log3 = self.postgres.load_log_select_by_file_date(self.new_day_2000)
        assert len(load_log3) > 2
        
        load_log4 = self.postgres.load_log_select_by_file_name("obviously broken")
        assert len(load_log4) == 0
        

    def test2(self):
        """load log insert"""

        self.pg_setup()

        args = self.fresh_load_log()

        # first should succeed
        load_log1 = self.postgres.load_log_insert(args, 11, "pytest")
        assert load_log1.id is not None

        # second insert should fail as duplicate
        load_log2 = self.postgres.load_log_insert(args, 11, "pytest")
        assert load_log2.id is None

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
