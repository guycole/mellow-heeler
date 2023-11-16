#
# Title: ie.py
# Description: development for information element rows
# Development Environment: OS X 12.6.9/Python 3.11.5
# Author: G.S. Cole (guycole at gmail dot com)
#
import json
import os
import sys

from typing import List

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

import yaml
from yaml.loader import SafeLoader

from heeler import Heeler
from hound import Hound
from postgres import PostGres


class InformationElement:

    def file_reader(self, file_name: str) -> List[str]:
        buffer = []

        with open(file_name, "r", encoding="utf-8") as infile:
            try:
                buffer = infile.readlines()
                if len(buffer) < 1:
                    print(f"empty file noted: {file_name}")
            except:
                print(f"file read error: {file_name}")

        return buffer

    def service_set_identity(self, raw_item: str) -> str:
        # empty ssid returns empty string
        #print(raw_item)

        element_id = raw_item[0:2]
        element_length = raw_item[2:4]

        buffer = ""
        for ndx in range(4, len(raw_item), 2):
            buffer += chr(int(raw_item[ndx:ndx+2], 16))

        return buffer

    def supported_rates(self, raw_item: str):
        pass

    def frequency_hop(self, raw_item: str):
        pass

    def direct_sequence(self, raw_item: str):
        pass

    def contention_free(self, raw_item: str):
        pass
 
    def traffic_indication_map(self, raw_item: str):
        pass

    def ibss_parameter(self, raw_item: str):
        pass

    def country(self, raw_item: str):
        element_id = raw_item[0:2]
        element_length = raw_item[2:4]

        buffer = ""
        for ndx in range(4, 10, 2):
            buffer += chr(int(raw_item[ndx:ndx+2], 16))

        return buffer

    def hop_pattern_param(self, raw_item: str):
        pass

    def hop_pattern_table(self, raw_item: str):
        pass

    def probe_request(self, raw_item: str):
        pass

    def ie_classifier(self, raw_item: str):
        temp = raw_item[13:]
        if temp.startswith("00"):
            ssid = self.service_set_identity(temp)
            print(f"ssid:{ssid}")
        elif temp.startswith("01"):
            self.supported_rates(temp)
        elif temp.startswith("02"):
            self.frequency_hop(temp)
        elif temp.startswith("03"):
            self.direct_sequence(temp)
        elif temp.startswith("04"):
            self.contention_free(temp)
        elif temp.startswith("05"):
            self.traffic_indication_map(temp)
        elif temp.startswith("06"):
            self.ibss_parameter(temp)
        elif temp.startswith("07"):
            country = self.country(temp)
            print(f"country:{country}")
        elif temp.startswith("08"):
            self.hop_pattern_param(temp)
        elif temp.startswith("09"):
            self.hop_pattern_table(temp)
        elif temp.startswith("0A"):
            self.probe_request(temp)
        elif temp.startswith("0B"):
            print(temp)
        elif temp.startswith("0C"):
            print(temp)
        elif temp.startswith("0D"):
            print(temp)
        elif temp.startswith("0E"):
            print(temp)
        elif temp.startswith("0F"):
            print(temp)
        elif temp.startswith("10"):
            print(temp)

    def driver(self, file_name: str):
        buffer = self.file_reader(file_name)
        for ndx in range(1, len(buffer)):
            #print(buffer[ndx])

            if buffer[ndx].startswith("                    IE: Unknown:"):
                self.ie_classifier(buffer[ndx].strip())
# 000A41545438753669326E38

print("start ie")

#
# argv[1] = configuration filename
#
if __name__ == "__main__":
    candidate = "../../samples/1f99eb09-033d-4e99-b51f-4165762cf995"
    #candidate = "../../samples/c1eed425-2a27-4a09-92e3-4ddd83c55c31"

    ie = InformationElement()
    ie.driver(candidate) 

print("stop ie")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
