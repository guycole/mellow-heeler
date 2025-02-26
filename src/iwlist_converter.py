#
# Title: iwlist_converter.py
# Description: parse an iwlist(8) scan file and return observations
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
from observation import Observation, Parser

import json
import sys
import time
import uuid

class Converter(object):

    def file_reader(self, file_name: str) -> list[str]:
        buffer = []
        
        try:
            with open(file_name, "r", encoding="utf-8") as in_file:
                buffer = in_file.readlines()
                if len(buffer) < 3:
                    print("empty scan file noted")
                    return []
        except Exception as error:
            print(error)

        return buffer

    def converter(self, file_name: str) -> list[dict[str, any]]:
        buffer = self.file_reader(file_name)
        if len(buffer) < 3:
            return

        parser = Parser(buffer)
        observations = parser.parser()
    
        obs_list = []
        for obs in observations:
            temp = {}
            temp["bssid"] = obs.bssid
            temp["capability"] = "unknown"
            temp["frequency_mhz"] = obs.frequency_mhz
            temp["signal_dbm"] = obs.signal_dbm
            temp["ssid"] = obs.ssid
            obs_list.append(temp)

        return obs_list

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
