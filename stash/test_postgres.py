#
# Title: test_postgres.py
# Description:
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
        assert load_log1[0].file_time == self.new_day_2000
        assert load_log1[0].load_time is not None
        assert type(load_log1[0].load_time) == datetime.datetime
        assert load_log1[0].file_type == "synthetic"
        assert load_log1[0].obs_population == 0
        assert load_log1[0].platform == "dummy1"
        assert load_log1[0].site == "anderson1"

        load_log2 = self.postgres.load_log_select_by_file_name(self.load_log_vallejo1)
        assert len(load_log2) == 1
        assert load_log2[0].id == 2
        assert load_log2[0].file_name == self.load_log_vallejo1
        assert load_log2[0].file_time == self.new_day_2000
        assert load_log2[0].load_time is not None
        assert type(load_log2[0].load_time) == datetime.datetime
        assert load_log2[0].file_type == "synthetic"
        assert load_log2[0].obs_population == 0
        assert load_log2[0].platform == "dummy2"
        assert load_log2[0].site == "vallejo1"

        load_log3 = self.postgres.load_log_select_by_file_name("obviously broken")
        assert len(load_log3) == 0

    def test2(self):
        """load log insert"""

        self.pg_setup()

        args = self.fresh_load_log()

        # first should succeed
        load_log1 = self.postgres.load_log_insert(args, 11, "site")
        assert load_log1.id is not None

        # second insert should fail as duplicate
        load_log2 = self.postgres.load_log_insert(args, 11, "site")
        print(load_log2.id)
        assert load_log2.id is None

    def test3(self):
        """geo_loc select"""

        self.pg_setup()

        geo_loc1 = self.postgres.geo_loc_select_by_site("match nothing")
        assert len(geo_loc1) < 1

        geo_loc2 = self.postgres.geo_loc_select_by_site("anderson1")
        assert len(geo_loc2) == 1
        assert geo_loc2[0].site == "anderson1"

        geo_loc3 = self.postgres.geo_loc_select_by_load_log(1)
        assert len(geo_loc3) == 1
        assert geo_loc3[0].site == "anderson1"

    def test4(self):
        """geo_loc insert"""

        self.pg_setup()

        load_log1 = self.postgres.load_log_insert(self.fresh_load_log(), 11, "site")
        assert load_log1.id is not None

        args = {}
        args["altitude"] = 123
        args["fix_time"] = self.new_day_2000
        args["latitude"] = 12.34
        args["longitude"] = 123.45
        args["site"] = "z" + str(random.randint(1, 999))
        args["speed"] = 43.21
        args["track"] = 12.34

        geo_loc1 = self.postgres.geo_loc_insert(args, load_log1.id)
        assert geo_loc1 is not None

    def test5(self):
        """wap select"""

        self.pg_setup()

        load_log1 = self.postgres.load_log_insert(self.fresh_load_log(), 11, "site")
        assert load_log1.id is not None

        args = {}
        args["bssid"] = "bssid3"
        args["capability"] = "capability"
        args["frequency_mhz"] = 1234
        args["ssid"] = "ssid_test"

        wap1 = self.postgres.wap_select_or_insert(args, load_log1.id)
        assert wap1 is not None

        wap2 = self.postgres.wap_select_or_insert(args, load_log1.id)
        assert wap1.id == wap2.id

    def test6(self):
        """wap insert"""

        self.pg_setup()

        load_log1 = self.postgres.load_log_insert(self.fresh_load_log(), 11, "site")
        assert load_log1.id is not None

        args = {}
        args["bssid"] = "bssid2"
        args["capability"] = "capability"
        args["frequency_mhz"] = 1234
        args["ssid"] = "ssid2x"

        wap1 = self.postgres.wap_select_or_insert(args, load_log1.id)
        assert wap1 is not None

        base_version = wap1.version

        args["ssid"] = "ssid2xy"

        wap1 = self.postgres.wap_select_or_insert(args, load_log1.id)
        assert wap1 is not None
        assert wap1.version == base_version + 1

    def test7(self):
        """observation select"""

        self.pg_setup()

        load_log1 = self.postgres.load_log_insert(self.fresh_load_log(), 11, "site")
        assert load_log1.id is not None

        args = {}
        args["bssid"] = "bssid2"
        args["level"] = 12

        obs1 = self.postgres.observation_insert(args, load_log1.id)
        assert obs1 is not None

        obs2 = self.postgres.observation_select_by_bssid_and_load_log(
            "bssid2", load_log1.id
        )
        assert len(obs2) == 1
        assert obs2[0].bssid == "bssid2"

    def test8(self):
        """observation insert"""

        self.pg_setup()

        load_log1 = self.postgres.load_log_insert(self.fresh_load_log(), 11, "site")
        assert load_log1.id is not None

        args = {}
        args["bssid"] = "bssid2"
        args["level"] = 12

        obs1 = self.postgres.observation_insert(args, load_log1.id)
        assert obs1 is not None


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
