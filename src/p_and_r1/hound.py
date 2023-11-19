"""mellow heeler hound file parser and database loader"""

import json

from typing import List

from postgres import PostGres


class Hound:
    """mellow heeler hound file parser and database loader"""

    postgres = None
    run_stats = {}

    def __init__(self, postgres: PostGres):
        self.postgres = postgres

        self.run_stats["fresh_cooked"] = 0
        self.run_stats["fresh_observation"] = 0
        self.run_stats["fresh_wap"] = 0
        self.run_stats["update_wap"] = 0

    def run_stat_bump(self, key: str):
        """increment a run_stat"""

        if key in self.run_stats:
            self.run_stats[key] = self.run_stats[key] + 1
        else:
            print(f"unknown run_stats key: {key}")

    def run_stat_dump(self):
        """print run_stats summary"""

        print(
            f"cooked: {self.run_stats['fresh_cooked']} observation: {self.run_stats['fresh_observation']} wap: {self.run_stats['fresh_wap']}"
        )

    def hound_v1(self, buffer: List[str]) -> int:
        """hound parser v1"""

        print("hound parser v1")

        payload = json.loads(buffer[0])
        geoloc1 = payload["geoLoc"]
        wifi = payload["wiFi"]

        # hound parser v1 is always a mobile sample
        geoloc1["device"] = "android1"

        geoloc2 = self.postgres.geoloc_select_by_time(geoloc1)
        if geoloc2 is None:
            geoloc2 = self.postgres.geoloc_insert(geoloc1)
        else:
            # print("duplicate geoloc")
            return 0

        for observation1 in wifi:
            wap = self.postgres.wap_select(observation1)
            if wap is None:
                self.run_stat_bump("fresh_wap")
                wap = self.postgres.wap_select_or_insert(observation1)
            else:
                self.run_stat_bump("update_wap")

            observation1["fixTimeMs"] = geoloc2.fix_time_ms
            observation1["geolocId"] = geoloc2.id
            observation1["latitude"] = geoloc2.latitude
            observation1["longitude"] = geoloc2.longitude
            observation1["wapId"] = wap.id

            observation2 = self.postgres.observation_select(observation1)
            if observation2 is None:
                self.run_stat_bump("fresh_observation")
                observation2 = self.postgres.observation_insert(observation1)

            cooked = self.postgres.cooked_select(wap.id)
            if cooked is None:
                self.run_stat_bump("fresh_cooked")
                cooked = self.postgres.cooked_insert(observation1)
            else:
                cooked = self.postgres.cooked_update(observation1)

        self.run_stat_dump()

        box_score = self.postgres.box_score_select(geoloc2.fix_time_ms, geoloc2.device)

        if box_score is None:
            box_score = self.postgres.box_score_insert(
                self.run_stats["fresh_wap"], self.run_stats['update_wap'], geoloc2.fix_time_ms, geoloc2.device
            )
        else:
            box_score = self.postgres.box_score_update(
                self.run_stats["fresh_wap"], self.run_stats['update_wap'], geoloc2.fix_time_ms, geoloc2.device
            )

        return 0


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
