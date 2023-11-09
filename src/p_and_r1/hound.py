#
# Title: hound.py
# Description:
# Development Environment: OS X 12.6.9/Python 3.11.5
# Author: G.S. Cole (guycole at gmail dot com)
#
import json
import time

from typing import Dict, List

from sql_table import GeoLoc, Wap

# from domain import Observatio=
# from domain import RawSample

import sqlalchemy

from sqlalchemy.orm import Session
from sqlalchemy import select


class Hound(object):
    db_engine = None
    dry_run = False

    def __init__(self, db_engine: sqlalchemy.engine.base.Engine, dry_run: bool):
        self.db_engine = db_engine
        self.dry_run = dry_run

    #    def observation_v1(self, file_name: str, geo_loc: Dict) -> Observation:
    #        self.project = 1  # hound
    #        self.version = 1  # version 1 format

    #        return Observation(
    #            geo_loc["accuracy"],
    #            geo_loc["altitude"],
    #            geo_loc["fixTimeMs"],
    #            geo_loc["latitude"],
    #            geo_loc["longitude"],
    #            file_name,
    #            time.strftime(
    #                "%Y-%m-%d %H:%M:%S", time.gmtime(geo_loc["fixTimeMs"] / 1000)
    #            ),
    #            self.project,
    #            self.version,
    #        )

    #    def raw_sample_v1(self, file_name: str, sample: Dict) -> RawSample:
    #        return RawSample(
    #            sample["bssid"],
    #            sample["capability"],
    #            sample["frequency"],
    #            sample["level"],
    #            file_name,
    #            sample["ssid"],
    #        )

    #    def hound_v1z(self, file_name: str, raw_load: Dict):
    #        if "geoLoc" in raw_load:
    #            obs = self.observation_v1(file_name, raw_load["geoLoc"])
    #        else:
    #            print("skipping bad observation missing geoLoc:", file_name)
    #            return
    #
    #        samples = None
    #        if "wiFi" in raw_load:
    #            samples = raw_load["wiFi"]
    #            print("sample population:", len(samples), " from:", file_name)
    #
    #        if samples is None:
    #            print("skipping bad observation missing wiFi:", file_name)
    #            return
    #
    #        if len(samples) < 1:
    #            print("skipping bad observation empty wiFi:", file_name)
    #            return

    #        for sample in samples:
    #            raw_sample = self.raw_sample_v1(file_name, sample)
    #            print(raw_sample)

    #            self.sample_raw_writer(file_name, observation)
    #            self.sample_cooked_writer(file_name, observation)

    #    def execute(self, file_name: str, version: int, raw_load: Dict):
    #        if version == 1:
    #            self.hound_v1(file_name, raw_load)
    #        else:
    #            print("skipping unknown hound version:", version)

    def process_geoloc(self, geoloc: Dict) -> GeoLoc:
        # truncate for database single precision real
        lat = round(geoloc["latitude"], 5)
        lng = round(geoloc["longitude"], 5)

        candidate = None
        statement = select(GeoLoc).filter_by(fix_time_ms=geoloc["fixTimeMs"])
        with Session(self.db_engine) as session:
            for row in session.scalars(statement):
                if (
                    row.accuracy == geoloc["accuracy"]
                    and row.altitude == geoloc["altitude"]
                    and row.latitude == lat
                    and row.longitude == lng
                ):
                    candidate = row

        if candidate is None:
            # fresh insert
            candidate = GeoLoc(geoloc["accuracy"], geoloc["altitude"], geoloc["fixTimeMs"], lat, lng, 'bogus')
            if self.dry_run is True:
                print("skipping geoloc insert")
            else:
                with Session(self.db_engine) as session:
                    session.add(candidate)
                    session.commit()

        return candidate

    def process_observation(self, geoloc_id: int, observation: Dict):
        # test for existing wap, create as needed
        # insert fresh observation
        # update cooked and boxscore

        print(observation)
        # if self.dry_run is True:
        #    return 0

        candidate = None
        statement = select(Wap).filter_by(bssid=observation["bssid"])
        with Session(self.db_engine) as session:
            for row in session.scalars(statement):
                candidate = row

        if candidate is None:
            # fresh insert
            candidate = Wap(
                observation["bssid"],
                observation["capability"],
                observation["frequency"],
                observation["ssid"],
            )
            if self.dry_run is True:
                print("skipping wap insert")
            else:
                with Session(self.db_engine) as session:
                    session.add(candidate)
                    session.commit()
        else:
            # test for changes
            if (
                candidate.capability != observation["capability"]
                or candidate.frequency != observation["frequency"]
                or candidate.ssid != observation["ssid"]
            ):
                # update
                candidate.capability = observation["capability"]
                candidate.frequency = observation["frequency"]
                candidate.ssid = observation["ssid"]
                if self.dry_run is True:
                    print("skipping wap update")
                else:
                    with Session(self.db_engine) as session:
                        session.add(candidate)
                        session.commit()

    def hound_v1(self, buffer: List[str]) -> int:
        print("hound parser v1")
        status = 0

        payload = json.loads(buffer[0])
        geoloc = payload["geoLoc"]
        wifi = payload["wiFi"]

        geoloc = self.process_geoloc(geoloc)
        # set refresh flag

        for observation in wifi:
            self.process_observation(geoloc.id, observation)

        return status


class HeelerLoader:
    def sample_cooked_writer(self, file_name, observation: Dict):
        print("sample_cooked_writer")

        # select cooked sample
        # ensure ssid, freq, capability match
        # lat/long distance?
        #
        sample_cooked = None

        if sample_cooked is None:
            print("insert cooked sample")

            sample_cooked = {}
            sample_cooked["bssid"] = observation["bssid"]
            sample_cooked["capability"] = observation["capability"]
            sample_cooked["frequency"] = observation["frequency"]
            sample_cooked["latitude"] = observation["latitude"]
            sample_cooked["longitude"] = observation["longitude"]
            sample_cooked["observed_first"] = observation["observed_at"]
            sample_cooked["observed_last"] = observation["observed_at"]
            sample_cooked["population"] = 1
            sample_cooked["ssid"] = observation["ssid"]
        else:
            print("update cooked sample")

            sample_cooked["observed_last"] = observation["observed_at"]
            sample_cooked["population"] = sample_cooked["population"] + 1

            # average latitude and longitude

    def sample_raw_writer(self, file_name: str, observation: Dict):
        print("sample_raw_writer")

        sample_raw = {}
        sample_raw["bssid"] = observation["bssid"]
        sample_raw["capability"] = observation["capability"]
        sample_raw["frequency"] = observation["frequency"]
        sample_raw["level"] = observation["level"]
        sample_raw["ssid"] = observation["ssid"]

        print(sample_raw)

    def hound_v1x(self, file_name: str, raw_load: Dict):
        print("hound parser v1")

        project = 1
        version = 1

        if "geoLoc" in raw_load:
            geo_loc = raw_load["geoLoc"]
        else:
            print("skipping bad observation missing geoLoc:", file_name)
            return

        observations = None
        if "wiFi" in raw_load:
            observations = raw_load["wiFi"]
            print("observation population:", len(observations), " from:", file_name)

        if observations is None:
            print("skipping bad observation missing wiFi:", file_name)
            return

        if len(observations) < 1:
            print("skipping bad observation empty wiFi:", file_name)
            return

        self.observation_writer(file_name, project, version, geo_loc)


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
