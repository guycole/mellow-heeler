#
# Title: heeler.py
# Description:
# Development Environment: OS X 12.6.9/Python 3.11.5
# Author: G.S. Cole (guycole at gmail dot com)
#
import json

from typing import Dict, List

from postgres import PostGres

from sql_table import Cooked, GeoLoc, Observation, Wap

class Heeler:
    postgres = None
    run_stats = {}

    def __init__(self, postgres: PostGres):
        self.postgres = postgres

        self.run_stats["fresh_cooked"] = 0
        self.run_stats["fresh_observation"] = 0
        self.run_stats["fresh_wap"] = 0

    def run_stat_bump(self, key: str):
        if key in self.run_stats:
            self.run_stats[key] = self.run_stats[key] + 1
        else:
            print(f"unknown run_stats key: {key}")

    def run_stat_dump(self):
        print(
            f"cooked: {self.run_stats['fresh_cooked']} observation: {self.run_stats['fresh_observation']} wap: {self.run_stats['fresh_wap']}"
        )

    def geoloc_select(self, geoloc: Dict[str, str], fix_time_ms: int) -> GeoLoc:
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
        # Cell 06 - Address: 16:D4:50:45:52:29
        temp = buffer.split(" ")
        return temp[14].strip()

    def get_encryption_key(self, buffer: str) -> int:
        # Encryption key:on
        temp = buffer.split(":")
        return temp[1].strip()

    def get_frequency(self, buffer: str) -> int:
        # Frequency:2.447 GHz (Channel 8)
        # Frequency:5.765 GHz
        temp1 = buffer.split(" ")
        temp2 = temp1[20]
        temp3 = temp2.split(":")
        temp4 = temp3[1].strip()
        temp5 = temp4.replace(".", "")
        if len(temp5) < 4:
            temp5 = temp5 + "0"
        return int(temp5)

    def get_level(self, buffer: str) -> int:
        # Quality=30/70  Signal level=-80 dBm
        temp1 = buffer.split(" ")
        temp2 = temp1[23]
        temp3 = temp2.split("=")
        return int(temp3[1].strip())

    def get_mode(self, buffer: str) -> int:
        # Mode:Master
        temp = buffer.split(":")
        return temp[1].strip()

    def get_protocol(self, buffer: List[str]) -> str:
        # IE: IEEE 802.11i/WPA2 Version 1
        # IE: WPA Version 1
        # print(buffer)        
        results = ""
        if "WPA2 Version 1" in buffer[0]:
            results = results + "WPA2"
        elif "WPA Version 1" in buffer[0]:
            results = results + "WPA"
        else:
            print(f"unknown protocol: {buffer[0]}")
            return ""

        if "PSK" in buffer[3]:
            results = results + "-PSK"
        else:
            print(f"unknown authentication: {buffer[3]}")

        if "CCMP" in buffer[2]:
            results = results + "-CCMP"

        if "TKIP" in buffer[2]:
            results = results + "+TKIP"

        results = "[" + results + "]"

        return results

    def get_ssid(self, buffer: str) -> str:
        # ESSID:"Grandpas house boat"
        temp = buffer.split(":")
        temp = temp[1].strip()
        temp = temp.strip('"')
        return temp
    
   
    def write_cell_dict(self, cell_dict: Dict[str, str], geoloc:GeoLoc) -> int:
        print(cell_dict)

        cell_dict["geolocId"] = geoloc.id
        cell_dict["fixTimeMs"] = geoloc.fix_time_ms
        cell_dict["latitude"] = geoloc.latitude
        cell_dict["longitude"] = geoloc.longitude

        if "capability" not in cell_dict:
            # 'wXa-122-e2e1'
            cell_dict["capability"] = "capability"

        wap = self.postgres.wap_select(cell_dict)
        if wap is None:
            self.run_stat_bump("fresh_wap")
            wap = self.postgres.wap_select_or_insert(cell_dict)

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
            print("update")
            cooked = self.postgres.cooked_update(cell_dict)

        return 0

#    def extractor(self, buffer: List[str]) -> Dict[str, str]:
#        result = {}
#
#        for ndx in range(1, len(buffer)):
#            if "Address:" in buffer[ndx]:
#                result["bssid"] = self.get_bssid(buffer[ndx])
#            if "Cell" in buffer[ndx]:
#                if len(result) > 1:
#                    self.write_cell_dict(result)
#                    result.clear()
#            if "Encryption key" in buffer[ndx]:
#                result["encryption"] = self.get_encryption_key(buffer[ndx])
#            if "ESSID" in buffer[ndx]:
#                result["ssid"] = self.get_ssid(buffer[ndx])
#            if "Frequency:" in buffer[ndx]:
#                result["frequency"] = self.get_frequency(buffer[ndx])
#            if "Mode" in buffer[ndx]:
#                result["mode"] = self.get_mode(buffer[ndx])
#            if "WPA" in buffer[ndx]:
#                result["capability"] = self.get_protocol(buffer[ndx : ndx + 4])

#        self.write_cell_dict(result)

    def heeler_v1(self, buffer: List[str]) -> int:
        print("heeler parser v1")

        payload = json.loads(buffer[0])
        geoloc1 = payload["geoLoc"]

        # heeler v1 is always fixed site location, select should never fail
        geoloc2 = self.geoloc_select(geoloc1, payload["zTimeMs"])

        cell_dict = {}
        for ndx in range(1, len(buffer)):
            if "Cell" in buffer[ndx]:
                if len(cell_dict) > 1:
                    self.write_cell_dict(cell_dict, geoloc2)
                    cell_dict.clear()
            if "Address:" in buffer[ndx]:
                # a6:6a:44:db:95:2b
                cell_dict["bssid"] = self.get_bssid(buffer[ndx])
            if "Frequency:" in buffer[ndx]:
                cell_dict["frequency"] = self.get_frequency(buffer[ndx])
            if "ESSID" in buffer[ndx]:
                cell_dict["ssid"] = self.get_ssid(buffer[ndx])
            if "Signal level" in buffer[ndx]:
                cell_dict["level"] = self.get_level(buffer[ndx])
            if "WPA" in buffer[ndx]:
                # cell_dict["capability"] = self.get_protocol(buffer[ndx : ndx + 4])
                cell_dict["capability"] = "capability"

        self.write_cell_dict(cell_dict, geoloc2)

        self.run_stat_dump()

        box_score = self.postgres.box_score_select(geoloc2.fix_time_ms, geoloc2.device)

        if box_score is None:
            box_score = self.postgres.box_score_insert(self.run_stats["fresh_wap"], geoloc2.fix_time_ms, geoloc2.device)
        else:
            box_score = self.postgres.box_score_update(self.run_stats["fresh_wap"], geoloc2.fix_time_ms, geoloc2.device)

        return 0

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
