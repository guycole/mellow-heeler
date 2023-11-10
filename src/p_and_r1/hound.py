#
# Title: hound.py
# Description:
# Development Environment: OS X 12.6.9/Python 3.11.5
# Author: G.S. Cole (guycole at gmail dot com)
#
import json
import time

from postgres import PostGres

from typing import Dict, List

from sql_table import GeoLoc, Wap

import sqlalchemy

from sqlalchemy.orm import Session
from sqlalchemy import select


class Hound(object):
    db_engine = None
    dry_run = False
    postgres = None

    def __init__(self, db_engine: sqlalchemy.engine.base.Engine, dry_run: bool):
        self.db_engine = db_engine
        self.dry_run = dry_run
        self.postgres = PostGres(self.db_engine, self.dry_run)

    def process_observation( self, geoloc_id: int, geoloc_fix_time_ms: int, observation: Dict):
        print(observation)

        wap = self.postgres.wap_select_or_insert(observation)
        print(wap)

        obs_dict = {}
        obs_dict["geolocId"] = geoloc_id
        obs_dict["level"] = observation["level"]
        obs_dict["fixTimeMs"] = geoloc_fix_time_ms
        obs_dict["wapId"] = wap.id

        observation = self.postgres.observation_insert(obs_dict)

    def hound_v1(self, buffer: List[str]) -> int:
        print("hound parser v1")

        fresh_cooked = 0
        fresh_geoloc = False
        fresh_observation = 0
        fresh_wap = 0
        status = 0

        payload = json.loads(buffer[0])
        geoloc = payload["geoLoc"]
        wifi = payload["wiFi"]

        # hound parser v1 is always a mobile sample
        geoloc["device"] = "android1"

        geoloc2 = self.postgres.geoloc_select_by_time(geoloc) 
        if geoloc2 is None:
          fresh_geoloc = True
          geoloc2 = self.postgres.geoloc_insert(geoloc)
          box_sxore = self.postgres.box_score_insert(geoloc2.fix_time_ms, geoloc2.device)

        for observation in wifi:
            print(observation)
            wap = self.postgres.wap_select(observation)
            if wap is None:
                fresh_wap = fresh_wap + 1
                wap = self.postgres.wap_select_or_insert(observation)

            print(wap)

            observation2 = self.postgres.observation_select(geoloc2, observation, wap)
            if observation2 is None:
                fresh_observation = fresh_observation + 1

                observation['fixTimeMs'] = geoloc2.fix_time_ms
                observation['geolocId'] = geoloc2.id
                observation['wapId'] = wap.id
            
                observation2 = self.postgres.observation_insert(observation)

            print(observation2)

            observation['fixTimeMs'] = geoloc2.fix_time_ms
            observation['wapId'] = wap.id

            cooked = self.postgres.cooked_select(wap.id)
            if cooked is None:
                fresh_cooked = fresh_cooked + 1

                observation['latitude'] = geoloc2.latitude
                observation['longitude'] = geoloc2.longitude

                cooked = self.postgres.cooked_insert(observation)
            else:
                cooked = self.postgres.cooked_update(observation)

### still need boxscore

        return status

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
