"""mellow heeler file parser and database loader"""

import json

from typing import Dict, List

from heeler_capability import HeelerCapabilityV1

from postgres import PostGres

from sql_table import GeoLoc


class Heeler:
    """mellow heeler file parser and database loader"""

    hc1 = None
    postgres = None
    run_stats = {}

    def __init__(self, postgres: PostGres):
        self.hc1 = HeelerCapabilityV1()
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

    def geoloc_select(self, geoloc: Dict[str, str], fix_time_ms: int) -> GeoLoc:
        """select a geoloc by device"""

        if geoloc["site"] == "anderson":
            device = "rpi4c-anderson1"
        elif geoloc["site"] == "vallejo":
            device = "rpi4a-vallejo1"
        else:
            raise ValueError(f"invalid site:{geoloc['site']}")

        geoloc2 = self.postgres.geoloc_select_by_device(device)
        geoloc2.fix_time_ms = fix_time_ms

        return geoloc2

    def get_bssid(self, buffer: str) -> str:
        """parse for bssid"""

        # Cell 06 - Address: 16:D4:50:45:52:29
        temp = buffer.split(" ")
        return temp[14].strip()

    def get_encryption_key(self, buffer: str) -> int:
        """parse for encryption key flag"""

        # Encryption key:on
        return buffer.split(":")[1].strip()

    def get_frequency(self, buffer: str) -> int:
        """parse for frequency"""

        # Frequency:2.447 GHz (Channel 8)
        # Frequency:5.765 GHz
        # return as 4 digit integer
        temp1 = buffer.split(" ")
        temp2 = temp1[20]
        temp3 = temp2.split(":")
        temp4 = temp3[1].strip()
        temp5 = temp4.replace(".", "")

        while len(temp5) < 4:
            temp5 = temp5 + "0"

        return int(temp5)

    def get_level(self, buffer: str) -> int:
        """parse for rssid signal level"""

        # Quality=30/70  Signal level=-80 dBm
        temp1 = buffer.split(" ")
        temp2 = temp1[23]
        temp3 = temp2.split("=")
        return int(temp3[1].strip())

    def get_mode(self, buffer: str) -> str:
        """parse for mode"""

        # Mode:Master
        return buffer.split(":")[1].strip()

    def get_ssid(self, buffer: str) -> str:
        """parse for ssid"""

        # ESSID:"Grandpas house boat"
        return buffer.split(":")[1].strip().strip('"')

    def write_cell_dict(
        self, cell_dict: Dict[str, str], geoloc: GeoLoc, load_log_id: int
    ) -> int:
        """write parsed elements to postgresql"""

        cell_dict["loadlogId"] = load_log_id

        cell_dict["geolocId"] = geoloc.id
        cell_dict["fixTimeMs"] = geoloc.fix_time_ms
        cell_dict["latitude"] = geoloc.latitude
        cell_dict["longitude"] = geoloc.longitude

        cell_dict["capability"] = self.hc1.build_capability(cell_dict)

        wap = self.postgres.wap_select_or_insert(cell_dict)

        if wap.insert_flag is True:
            self.run_stat_bump("fresh_wap")
        elif wap.update_flag is True:
            self.run_stat_bump("update_wap")

        cell_dict["wapId"] = wap.id

        observation = self.postgres.observation_select(cell_dict)
        if observation is None:
            self.run_stat_bump("fresh_observation")
            observation = self.postgres.observation_insert(cell_dict)

        cooked = self.postgres.cooked_select(wap.id)
        if cooked is None:
            self.run_stat_bump("fresh_cooked")
            cooked = self.postgres.cooked_insert(cell_dict)
        else:
            cooked = self.postgres.cooked_update(cell_dict)

        return 0

    def heeler_v1_get_timestamp(self, buffer: List[str]) -> int:
        """return file timestamp"""

        payload = json.loads(buffer[0])
        return payload["zTimeMs"]

    def heeler_v1(self, buffer: List[str], load_log_id: int) -> int:
        """heeler parser v1"""

        print("heeler parser v1")

        payload = json.loads(buffer[0])
        geoloc1 = payload["geoLoc"]

        # heeler v1 is always fixed site location, select should never fail
        geoloc2 = self.geoloc_select(geoloc1, payload["zTimeMs"])
        if geoloc2 is None:
            print("missing geoloc site")
            return -1

        cell_dict = {}
        for ndx in range(1, len(buffer)):
            if "Cell" in buffer[ndx]:
                # Cell 06 - Address: 16:D4:50:45:52:29
                if len(cell_dict) > 1:
                    self.write_cell_dict(cell_dict, geoloc2, load_log_id)
                    cell_dict.clear()

                cell_dict["bssid"] = self.get_bssid(buffer[ndx])
                cell_dict["ssid"] = ""
            elif "ESSID" in buffer[ndx]:
                # ESSID:"Gate K"
                cell_dict["ssid"] = self.get_ssid(buffer[ndx])
            elif "Frequency:" in buffer[ndx]:
                # Frequency:2.437 GHz (Channel 6)
                cell_dict["frequency"] = self.get_frequency(buffer[ndx])
            elif "Signal level" in buffer[ndx]:
                # Quality=44/70  Signal level=-66 dBm
                cell_dict["level"] = self.get_level(buffer[ndx])
            elif buffer[ndx].startswith(
                "                    IE: IEEE 802.11i/WPA2 Version 1"
            ):
                cell_dict["wpa2v1"] = self.hc1.get_wpa(buffer[ndx : ndx + 4])
            elif buffer[ndx].startswith("                    IE: WPA Version 1"):
                cell_dict["wpa1v1"] = self.hc1.get_wpa(buffer[ndx : ndx + 4])

        self.write_cell_dict(cell_dict, geoloc2, load_log_id)

        self.run_stat_dump()

        box_score = self.postgres.box_score_select(geoloc2.fix_time_ms, geoloc2.device)

        if box_score is None:
            box_score = self.postgres.box_score_insert(
                geoloc2.device,
                self.run_stats["fresh_wap"],
                self.run_stats["update_wap"],
                geoloc2.fix_time_ms,
            )
        else:
            box_score = self.postgres.box_score_update(
                geoloc2.device,
                self.run_stats["fresh_wap"],
                self.run_stats["update_wap"],
                geoloc2.fix_time_ms,
            )

        return 0


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
