#
# Title: heeler.py
# Description:
# Development Environment: OS X 12.6.9/Python 3.11.5
# Author: G.S. Cole (guycole at gmail dot com)
#
import json
import time

from typing import List

#from domain import Observation

class Heeler(object):
    dry_run = False

    def __init__(self, dry_run: bool):
        self.dry_run = dry_run

    def heeler_v1(self, buffer: List[str]) -> int:
        print("heeler parser v1")

        payload = json.loads(buffer[0])
        geoloc = payload["geoLoc"]
        print(geoloc)
        wifi = payload["wifi"]
        print(wifi)

#        status = self.process_geoloc(geoloc)
#        if status != 0:
#            return status

        status = 0

        return status
    

#    def observation_v1(self, file_name: str, project: int, geo_loc: typing.Dict):
#        print("observation_writer")

#        obs = Observation()
#        obs.accuracy = geo_loc["accuracy"]
#        obs.altitude = geo_loc["altitude"]
#        obs.fix_time_ms = geo_loc["fixTimeMs"]
#        obs.latitude = geo_loc["latitude"]
#        obs.longitude = geo_loc["longitude"]
#        obs.observation_key = file_name
#        obs.observed_at = time.strftime(
#            "%Y-%m-%d %H:%M:%S", time.gmtime(geo_loc["fixTimeMs"] / 1000)
#        )
#        obs.project = 2  # heeler
#        obs.version = 1  # version 1 format
#
#        print(obs)

#    def heeler_v1(self, file_name: str, raw_load: typing.Dict):
#        project = 2
#        version = 1

#        if "geoLoc" in raw_load:
#            geo_loc = raw_load["geoLoc"]
#        else:
#            print("skipping bad observation missing geoLoc:", file_name)
#            return

#        observations = None
#        if "wiFi" in raw_load:
#            observations = raw_load["wiFi"]
#            print("observation population:", len(observations), " from:", file_name)

#        if observations is None:
#            print("skipping bad observation missing wiFi:", file_name)
#            return

#        if len(observations) < 1:
#            print("skipping bad observation empty wiFi:", file_name)
#            return

    #        self.observation_writer(file_name, project, version, geo_loc)

#    def execute(self, file_name: str, version: int, raw_load: typing.Dict):
#        if version == 1:
#            self.heeler_v1(file_name, raw_load)
#        else:
#            print("skipping unknown heeler version:", version)


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
