"""mellow heeler file parser and database loader"""

from typing import Dict, List


class HeelerCapabilityV1:
    """mellow heeler WAP capability parser"""

    def get_wpa(self, raw_list: List[str]) -> Dict[str, str]:
        """parse for wpa elements"""

        # IE: IEEE 802.11i/WPA2 Version 1
        # IE: WPA Version 1

        results = {}
        temp = []
        for current in raw_list:
            temp.append(current.strip())

        for ndx in temp:
            if ndx == "IE: WPA Version 1":
                results["wpa"] = "wpa1v1"
            elif ndx == "IE: IEEE 802.11i/WPA2 Version 1":
                results["wpa"] = "wpa2v1"
            elif ndx.startswith("Group Cipher"):
                results["group_cipher"] = ndx.split(":")[1].strip()
            elif ndx.startswith("Pairwise Ciphers"):
                results["pair_cipher"] = ndx.split(":")[1].strip()
            elif ndx.startswith("Authentication Suites"):
                results["authentication_suite"] = ndx.split(":")[1].strip()
            else:
                print(f"unknown wpa element: {ndx}")

        return results

    def build_authentication(self, raw_auth: str) -> str:
        """build authentication string"""

        results = "UNKNOWN"

        if "PSK unknown" in raw_auth:
            results = "UNKNOWN+PSK"
        elif raw_auth.endswith("PSK"):
            results = "PSK"
        elif raw_auth.endswith("802.1x"):
            results = "EAP"

        return results

    def build_group_cipher(self, raw_cipher: str) -> str:
        """build group cipher string"""

        results = "UNKNOWN"

        if "unknown" in raw_cipher:
            results = "UNKNOWN"

        if raw_cipher.endswith("CCMP TKIP"):
            if len(results) > 0:
                results = results + "-CCMP+TKIP"
            else:
                results = "CCMP+TKIP"
        elif raw_cipher.endswith("TKIP CCMP"):
            if len(results) > 0:
                results = results + "-TKIP+CCMP"
            else:
                results = "TKIP+CCMP"
        elif raw_cipher.endswith("CCMP"):
            if len(results) > 0:
                results = results + "-CCMP"
            else:
                results = "CCMP"
        elif raw_cipher.endswith("TKIP"):
            if len(results) > 0:
                results = results + "-TKIP"
            else:
                results = "TKIP"

        return results

    def build_pair_cipher(self, raw_cipher: str) -> str:
        """build pair cipher string"""

        results = ""

        if "unknown" in raw_cipher:
            results = "UNKNOWN"

        if raw_cipher.endswith("CCMP TKIP"):
            if len(results) > 0:
                results = results + "-CCMP+TKIP"
            else:
                results = "CCMP+TKIP"
        elif raw_cipher.endswith("CCMP"):
            if len(results) > 0:
                results = results + "+CCMP"
            else:
                results = "CCMP"
        elif raw_cipher.endswith("TKIP"):
            if len(results) > 0:
                results = results + "+TKIP"
            else:
                results = "TKIP"

        return results

    def build_capability(self, cell_dict: Dict[str, str]) -> str:
        """build capability string"""

        # print(cell_dict)

        results = ""

        if "wpa1v1" in cell_dict:
            candidate = cell_dict["wpa1v1"]
            auth_suite = self.build_authentication(candidate["authentication_suite"])
            pair_cipher = self.build_pair_cipher(candidate["pair_cipher"])
            # group_cipher = self.build_group_cipher(candidate["group_cipher"])
            results = f"[WPA-{auth_suite}-{pair_cipher}]"

        if "wpa2v1" in cell_dict:
            candidate = cell_dict["wpa2v1"]
            auth_suite = self.build_authentication(candidate["authentication_suite"])
            pair_cipher = self.build_pair_cipher(candidate["pair_cipher"])
            # group_cipher = self.build_group_cipher(candidate["group_cipher"])
            results = results + f"[WPA2-{auth_suite}-{pair_cipher}]"

        if len(results) < 1:
            # 'wXa-122-e2e1' (these are likely IBSS stations)
            results = "[wierdo]"

        # print(results)

        return results


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
