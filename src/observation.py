#
# Title: observation.py
# Description: parse iwlist and extract observations
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
import datetime


class Observation:
    """container, helps prune duplicate items"""

    def __init__(self, args: dict[str, any]):
        self.altitude = None
        self.args = args
        self.bssid = args["bssid"]
        self.capability = None
        self.file_name = None
        self.frequency_mhz = args["frequency_mhz"]
        self.latitude = None
        self.longitude = None
        self.obs_time = None
        self.platform = None
        self.site = None
        self.signal_dbm = args["signal_dbm"]
        self.ssid = args["ssid"]

    def __repr__(self):
        return self.ssid

    def __str__(self):
        return self.ssid

    def __eq__(self, other):
        return (
            self.bssid == other.bssid
            and self.ssid == other.ssid
            and self.frequency_mhz == other.frequency_mhz
        )

    def to_dict(self) -> dict[str, any]:
        result = {}

        result["bssid"] = self.bssid
        result["capability"] = "unknown"
        result["frequency_mhz"] = self.frequency_mhz
        result["signal_dbm"] = self.signal_dbm
        result["ssid"] = self.ssid

        return result


class Parser:
    def __init__(self, raw: list[str]):
        self.raw = raw

    def parse_capability(self, start_ndx: int, stop_ndx: int) -> str:
        results = {}

        #    for temp in range(start_ndx, stop_ndx + 1):
        #        if "Encryption key" in self.raw[temp]:
        #            if "Encryption key:on" in self.raw[temp]:
        #                results["encryption"] = True
        #            else:
        #                results["encryption"] = False

        #        if "Mode:Master" in self.raw[temp]:
        #            results["mode"] = "master"

        #        if "IE: WPA Version 1" in self.raw[temp]:
        #            results["wpa"] = "wpa1v1"

        #        if "IE: IEEE 802.11i/WPA2 Version 1" in self.raw[temp]:
        #            results["wpa"] = "wpa2v1"

        #        if "Authentication Suites" in self.raw[temp]:
        #            results["authentication_suite"] = self.raw[temp].split(":")[1].strip()

        #        if "Group Cipher" in self.raw[temp]:
        #            results["group_cipher"] = self.raw[temp].split(":")[1].strip()

        #        if "Pairwise Ciphers" in self.raw[temp]:
        #            results["pair_cipher"] = self.raw[temp].split(":")[1].strip()

        capabilities = "unknown"

        # if "wpa" in results:
        #    if results["wpa"] == "wpa1v1":
        #        pass
        #    else:
        #        capabilities = f"[WPA2-{results['authentication_suite']}-{results['group_cipher']}]"

        # Cell 01 - Address: 6C:CD:D6:2A:62:06
        # [802.11-Auth Suite-Pair Cipher]
        # 00:22:6b:81:03:d9 | braingang2 | [WPA2-PSK-CCMP][ESS]
        # 6c:cd:d6:2a:62:05 | braingang2_5GEXT | [WPA2-PSK-CCMP][WPS][ESS]
        #
        #          IE: IEEE 802.11i/WPA2 Version 1
        #                        Group Cipher : CCMP
        #                        Pairwise Ciphers (1) : CCMP
        #                        Authentication Suites (1) : PSK

        #        capabilities = "unknown"

        return capabilities

    def parse_cell(self, start_ndx: int, stop_ndx: int) -> Observation:
        """parse a cell stanza"""

        obs = {}
        obs["bssid"] = "unknown"
        obs["ssid"] = "unknown"
        obs["frequency_mhz"] = 0
        obs["signal_dbm"] = 0
        obs["time_stamp_z"] = datetime.datetime.now(datetime.timezone.utc).isoformat()

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
                obs["frequency_mhz"] = int(1000 * float(temp2[1]))
            elif "Quality" in line:
                # Quality=49/70  Signal level=-61 dBm
                temp1 = line.split()
                temp2 = temp1[2].split("=")
                obs["signal_dbm"] = int(temp2[1].strip())

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

    def parser(self) -> list[Observation]:
        obs_list = []
        origin_ndx = -1

        while True:
            (start_ndx, stop_ndx) = self.discover_indices(origin_ndx)
            # print(f"{start_ndx} {stop_ndx}")

            if start_ndx == stop_ndx:
                print("file consumed")
                break

            origin_ndx = stop_ndx

            obs = self.parse_cell(start_ndx, stop_ndx)
            if obs not in obs_list:
                obs.capability = self.parse_capability(start_ndx, stop_ndx)
                obs_list.append(obs)

        return obs_list


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
