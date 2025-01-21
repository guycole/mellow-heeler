#
# Title: skunk.py
# Description:
# Development Environment: OS X 12.7.6/Python 3.13.0
# Author: G.S. Cole (guycole at gmail dot com)
#
import datetime
import json
import requests
import sys

class Skunk:
    def __init__(self, raw: list[str]):
        self.raw = raw

    def parse_cell(self, start_ndx: int, stop_ndx: int) -> dict[str, str]:
        """parse a cell stanza"""

        obs = {}
        obs["time_stamp"] = datetime.datetime.now(datetime.timezone.utc).isoformat()

        for ndx in range(start_ndx, stop_ndx + 1):
            line = self.raw[ndx].strip()

            if "Address" in line:
                # Cell 01 - Address: 6C:CD:D6:2A:62:06
                obs["bssid"] = line.split()[4].lower()
            elif "ESSID" in line:
                # ESSID:"braingang2_2GEXT"
                temp1 = line.split(":")[1]
                obs["ssid"] = temp1.replace('"', "")  # remove quotes
                if len(obs["ssid"]) < 1:
                    obs["ssid"] = "empty_ssid"
            elif "Frequency" in line:
                # Frequency:2.437 GHz (Channel 6)
                temp1 = line.split()
                temp2 = temp1[0].split(":")
                obs["frequency"] = int(1000 * float(temp2[1]))
        
        return obs

    def discover_indices(self, origin: int) -> tuple[int, int]:
        """discover the start and stop indices of a cell stanza"""

        start_ndx = -1
        stop_ndx = -1

        if origin >= len(self.raw):
            print("origin out of range")
            return [start_ndx, stop_ndx]

        for ndx in range(origin + 1, len(self.raw)):
            if "Cell" in self.raw[ndx]:
                if start_ndx < 0:s
                    start_ndx = ndx
                else:
                    stop_ndx = ndx - 1
                    break

        # last element at end of file
        if (stop_ndx < 0) and (start_ndx > 0):
            stop_ndx = len(self.raw) - 1

        return [start_ndx, stop_ndx]

    def skunk_post(self, obs_list: list[dict[str, str]]):
        payload = json.dumps(obs_list, indent=4)

        response = requests.post("http://localhost/heeler", json=payload) 

        print(response)

    def loader(self):
        obs_list = []
        origin_ndx = -1

        while True:
            (start_ndx, stop_ndx) = self.discover_indices(origin_ndx)

            print(f"{start_ndx} {stop_ndx}")

            if start_ndx == stop_ndx:
                print("file consumed")
                break

            origin_ndx = stop_ndx

            obs_dict = self.parse_cell(start_ndx, stop_ndx)
            obs_list.append(obs_dict)

        self.skunk_post(obs_list)


print("start test")

#
# argv[1] = configuration filename
#
if __name__ == "__main__":
    file_name = "/home/gsc/Documents/github/mellow-heeler/src/collection/sample2.scan"
    #    file_name = "/Users/gsc/Documents/github/mellow-heeler/samples/ffe08060-3068-4424-bd9d-d23507d53c87"

    with open(file_name, "r") as infile:
        try:
            buffer = infile.readlines()
            if len(buffer) < 2:
                print("empty file noted")
                sys.exit(-1)
        except Exception as error:
            print(error)

        skunk = Skunk(buffer)
        skunk.loader()

print("stop test")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
