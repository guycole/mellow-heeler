#
# Title: preamble.py
# Description: json preamble helpers
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
import datetime
import gps_helper
import pytz
import time

class PreambleHelper:

    def create_preamble(
        self, host: str, site: str, gps_sample: gps_helper.GpsSample
    ) -> dict[str, any]:
        """create a fresh heeler preamble"""

        preamble = {}

        preamble["geoLoc"] = self.get_geoloc(site, gps_sample)
        preamble["platform"] = host
        preamble["project"] = "heeler"
        preamble["version"] = 1
        preamble["wifi"] = []
        preamble["zTime"] = round(time.time()) # collection hosts are always UTC

        return preamble

    def get_geoloc(self, site: str, gps_sample: gps_helper.GpsSample) -> dict[str, any]:
        """return geoLoc preamble element"""

        results = {}

        if site is None:
            print("site not found")
            return None
        else:
            results["site"] = site

        if gps_sample is not None:
            results = gps_sample.elements

        return results

    def validate_preamble(self, candidate: dict[str, any]) -> dict[str, any]:
        """validate and normalize heeler preamble"""

        result = {}

        temp = self.validate_geoloc(candidate)
        if len(temp) < 1:
            print("geoLoc not found")
            return None
        else:
            result["geoLoc"] = temp

        temp = self.validate_platform(candidate)
        if temp is None:
            print("platform not found")
            return None
        else:
            result["platform"] = temp

        temp = self.validate_project(candidate)
        if temp is None:
            print("project not found")
            return None
        else:
            result["project"] = temp

        temp = self.validate_version(candidate)
        if temp is None:
            print("version not found")
            return None
        else:
            result["version"] = temp

        temp = self.validate_ztime(candidate)
        if temp is None:
            print("zTime not found")
            return None
        else:
            result["file_time"] = temp

        return result

    def classifier(self, preamble: dict[str, str]) -> str:
        """discover file format, i.e. heeler_v1, etc"""

        project = None
        if "project" in preamble:
            project = preamble["project"]

        version = None
        if "version" in preamble:
            version = preamble["version"]

        file_type = f"{project}_{version}"
        return file_type

    def validate_geoloc(self, preamble: dict[str, any]) -> str:
        """test/normalize geographic location"""

        print(f"preamble:{preamble}")

        results = {}

        if "geoLoc" in preamble:
            temp = preamble["geoLoc"]
            if "site" in temp:
                if temp["site"] == "development":
                    print("skipping observation from development")
                    return results
                elif temp["site"].startswith("and"):
                    # fixed location site
                    results['site'] = 'anderson1'
                elif temp["site"].startswith("val"):
                    # fixed location site
                    results['site'] = 'vallejo1'
                elif temp["site"].startswith("mobile"):
                    # mobile location
                    results['altitude'] = temp['altitude']
                    results['fix_time'] = datetime.datetime.fromtimestamp(temp['fixTime'], pytz.utc)
                    results['latitude'] = temp['latitude']
                    results['longitude'] = temp['longitude']
                    results['site'] = 'mobile1'
                    results['speed'] = temp['speed']
                    results['track'] = temp['track']
                else:
                    print(f"geoloc unknown site: {temp['site']}")

        return results

    def validate_platform(self, preamble: dict[str, any]) -> str:
        """test/normalize platform"""

        if "platform" in preamble:
            return preamble["platform"]

        return None

    def validate_project(self, preamble: dict[str, any]) -> str:
        """test/normalize project"""

        if "project" in preamble:
            return preamble["project"]

        return None

    def validate_version(self, preamble: dict[str, any]) -> int:
        """test/normalize version"""

        if "version" in preamble:
            return preamble["version"]

        return None

    def validate_ztime(self, preamble: dict[str, any]) -> datetime.datetime:
        """extract observation time"""

        seconds = 0

        if "zTime" in preamble:
            seconds = int(preamble["zTimeMs"])
        elif "zTimeMs" in preamble:
            seconds = int(preamble["zTimeMs"]) / 1000
        else:
            return None

        return datetime.datetime.fromtimestamp(seconds, pytz.utc)

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
