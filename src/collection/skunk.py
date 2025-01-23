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


class Observation:
    def __init__(self, args: dict[str, any]):
        self.args = args
        self.bssid = args["bssid"]
        self.frequency = args["frequency"]
        self.ssid = args["ssid"]

    def __repr__(self):
        return self.ssid

    def __str__(self):
        return self.ssid

    def __eq__(self, other):
        return (
            self.bssid == other.bssid
            and self.ssid == other.ssid
            and self.frequency == other.frequency
        )


class Skunk:
    def __init__(self, raw: list[str]):
        self.raw = raw

    def parse_cell(self, start_ndx: int, stop_ndx: int) -> Observation:
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

        result = Observation(obs)
        return result

    def discover_indices(self, origin: int) -> tuple[int, int]:
        """discover the start and stop indices of a cell stanza"""

        start_ndx = -1
        stop_ndx = -1

        if origin >= len(self.raw):
            print("origin out of range")
            return [start_ndx, stop_ndx]

        for ndx in range(origin + 1, len(self.raw)):
            if "Cell" in self.raw[ndx]:
                if start_ndx < 0:
                    start_ndx = ndx
                else:
                    stop_ndx = ndx - 1
                    break

        # last element at end of file
        if (stop_ndx < 0) and (start_ndx > 0):
            stop_ndx = len(self.raw) - 1

        return [start_ndx, stop_ndx]

    def skunk_post(self, obs_list: list[Observation]) -> None:
        payload = []

        for current in obs_list:
            payload.append(current.args)

        print(payload)
        response = requests.post("http://localhost:8000/heeler/", json=payload)
        print(response)
        print(response.text)

    def loader(self) -> None:
        obs_list = []
        origin_ndx = -1

        while True:
            (start_ndx, stop_ndx) = self.discover_indices(origin_ndx)

            print(f"{start_ndx} {stop_ndx}")

            if start_ndx == stop_ndx:
                print("file consumed")
                break

            origin_ndx = stop_ndx

            obs = self.parse_cell(start_ndx, stop_ndx)
            if obs not in obs_list:
                obs_list.append(obs)

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
