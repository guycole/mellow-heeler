#
# Title: heeler.py
# Description:
# Development Environment: OS X 12.6.9/Python 3.11.5
# Author: G.S. Cole (guycole at gmail dot com)
#
import json
import time

from postgres import PostGres

from typing import Dict, List

from sql_table import Cooked, GeoLoc, Observation, Wap

from postgres import PostGres



import sqlalchemy

from sqlalchemy.orm import Session
from sqlalchemy import select


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
        return(temp[14].strip())
    
    def get_frequency(self, buffer: str) -> int:
        # Frequency:2.447 GHz (Channel 8)
        # Frequency:5.765 GHz
        temp = buffer.split(" ")
        #print(temp)
        return 0
    
    def get_level(self, buffer: str) -> int:
        # Quality=30/70  Signal level=-80 dBm
        temp = buffer.split(" ")
        #print(temp)
        return 0
    
    def get_ssid(self, buffer: str) -> str:
        # ESSID:"Grandpas house boat"
        temp = buffer.split(":")
        temp = temp[1].strip()
        temp = temp.strip('\"')
        return temp
    
    def stub1(self, observation: Dict[str, str]) -> int:
        wap = self.postgres.wap_select(observation)
        if wap is None:
            self.run_stat_bump("fresh_wap")
            wap = self.postgres.wap_select_or_insert(observation)

    def extractor(self, buffer: List[str]) -> Dict[str, str]:
        result = {}
      
        for ndx in range(1, len(buffer)):
            if "Address:" in buffer[ndx]:
                result['bssid'] = self.get_bssid(buffer[ndx])
            if "Frequency:" in buffer[ndx]:
                result['frequency'] = self.get_frequency(buffer[ndx])
            if "Signal level" in buffer[ndx]:
                result['level'] = self.get_level(buffer[ndx])
            if "ESSID" in buffer[ndx]:
                result['ssid'] = self.get_ssid(buffer[ndx])
            if "IEEE 802" in buffer[ndx]:
                result['capability'] = "bogus"
                self.stub1(result)
    
    def heeler_v1(self, buffer: List[str]) -> int:
        print("heeler parser v1")

        payload = json.loads(buffer[0])
        geoloc1 = payload["geoLoc"]
 
        # heeler v1 is always fixed site location
        geoloc1 = self.geoloc_select(geoloc1, payload["zTimeMs"])
       
        self.extractor(buffer)
       
        return 0

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
