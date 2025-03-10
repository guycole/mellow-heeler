#
# Title: observation.py
# Description: 
# Development Environment: OS X 12.7.6/Python 3.13.0
# Author: G.S. Cole (guycole at gmail dot com)
#
import logging
import json
import os
import sys
import time
from typing import Dict, List
import uuid
import yaml

from yaml.loader import SafeLoader

class Observation():
    bssid = "unknown"
    capability = "unknown"
    frequency = 0
    latitude = 0.0
    level = 0
    longitude = 0.0
#    platform = "configme"
    raw = None
    ssid = "empty_ssid"
    ztime_ms = 0
    project = "heeler"
#    site = "configme"
    version = 1

    def __init__(self, raw: list[str]):
        self.raw = raw

    def discover_indices(self, origin: int) -> tuple[int, int]:
        """discover the start and stop indices of a cell stanza"""

        start_ndx = -1
        stop_ndx = -1

        if origin >= len(self.raw):
            print("origin out of range")
            return [start_ndx, stop_ndx]
        
        for ndx in range(origin+1, len(self.raw)):
            if "Cell" in self.raw[ndx]:
                if start_ndx < 0:
                    start_ndx = ndx
                else:
                    stop_ndx = ndx-1
                    break
    
        # last element at end of file
        if (stop_ndx < 0) and (start_ndx > 0):
            stop_ndx = len(self.raw) - 1

        return [start_ndx, stop_ndx]
    
    def parse_capability(self, start_ndx: int, stop_ndx: int) -> str:
        return "bogus"
    
# [802.11-Auth Suite-Pair Cipher]
# 00:22:6b:81:03:d9 | braingang2 | [WPA2-PSK-CCMP][ESS]
# 6c:cd:d6:2a:62:05 | braingang2_5GEXT | [WPA2-PSK-CCMP][WPS][ESS]
    
    def parse_cell(self, start_ndx: int, stop_ndx: int) -> Dict[str, str]:
        """parse a cell stanza"""

        obs = {}
        obs['capability'] = self.parse_capability(start_ndx, stop_ndx)

        for ndx in range(start_ndx, stop_ndx+1):
            line = self.raw[ndx].strip()
#            print(line)

            if "Address" in line:
                # Cell 01 - Address: 6C:CD:D6:2A:62:06
                obs['bssid'] = line.split()[4].lower()
            elif "ESSID" in line:
                # ESSID:"braingang2_2GEXT"
                temp1 = line.split(":")[1]
                obs['ssid'] = temp1.replace('"', '') #remove quotes

                if len(obs['ssid']) < 1:
                    obs["ssid"] = "empty_ssid"
            elif "Frequency" in line:
                # Frequency:2.437 GHz (Channel 6)
                temp1 = line.split()
                temp2 = temp1[0].split(":")
                obs['frequency'] = int(1000 * float(temp2[1]))
            elif "Quality" in line:
                # Quality=45/70  Signal level=-65 dBm
                temp1 = line.split()[2]
                temp2 = temp1.split("=")
                obs['level'] = int(temp2[1])

        return obs
    
    def iwlist_parser(self) -> Dict[str, str]:
        origin_ndx = -1
    
        while True:
            (start_ndx, stop_ndx) = self.discover_indices(origin_ndx)
 
            print(f"{start_ndx} {stop_ndx}")

            if start_ndx == stop_ndx:
                print("file consumed")
                break

            origin_ndx = stop_ndx

            obs = self.parse_cell(start_ndx, stop_ndx)
            print(obs)

    def get_filename(self) -> str:
        return "%s/%s" % (self.export_dir, str(uuid.uuid4()))

    def get_preamble(self) -> Dict:
        geo_loc = {}
        geo_loc['site'] = self.site

        if site == "anderson":
            geo_loc['latitude'] = 44.30
            geo_loc['longitude'] = -122.17
        elif site == "vallejo":
            geo_loc['latitude'] = 44.30
            geo_loc['longitude'] = -122.17
        else:
            print("unsupported site:", self.site)
            geo_loc['latitude'] = 0.0
            geo_loc['longitude'] = 0.0

        preamble = {}
        preamble['wifi'] = []
        preamble['project'] = 'heeler'
        preamble['version'] = 1
        preamble['platform'] = self.host
        preamble['zTimeMs'] = round(time.time() * 1000)

        preamble['geoLoc'] = geo_loc

        return preamble
    
    def converter(self, buffer:List[str]):
        with open(self.get_filename(), "w") as outfile:
            json_preamble = json.dumps(self.get_preamble())
            outfile.write(json_preamble + "\n")
            outfile.write("RAWBUFFER\n")
            outfile.writelines(buffer)

    def execute(self, file_name: str):
        with open(file_name, "r") as infile:
            try:
                buffer = infile.readlines()
                if len(buffer) < 2:
                    print("empty file noted")
                    return
            except:
                print("file read error")

        self.converter(buffer)

#        preamble = self.get_preamble()
#        print(preamble)

print("start test")

#
# argv[1] = configuration filename
#
if __name__ == "__main__":
    if len(sys.argv) > 1:
        file_name = sys.argv[1]
    else:
        file_name = "config.yaml"

    with open(file_name, "r") as stream:
        try:
            configuration = yaml.load(stream, Loader=SafeLoader)
        except yaml.YAMLError as error:
            print(error)
            sys.exit(-1)

    export_dir = configuration["exportDir"]
    site = configuration["site"]
    host = configuration["host"]

    file_name = "/Users/gsc/Documents/github/mellow-heeler/src/converter_rpi/sample2.scan"
#    file_name = "/Users/gsc/Documents/github/mellow-heeler/samples/ffe08060-3068-4424-bd9d-d23507d53c87"

    with open(file_name, "r") as infile:
        try:
            buffer = infile.readlines()
            if len(buffer) < 2:
                print("empty file noted")
                sys.exit(-1)
        except Exception as error:
            print(error)
            print("file read error")

        obs = Observation(buffer)
        obs.iwlist_parser()

#        self.converter(buffer)        

#    converter = Converter(export_dir, site, host)
#    converter.execute("/tmp/iwlist.scan")

print("stop test")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
