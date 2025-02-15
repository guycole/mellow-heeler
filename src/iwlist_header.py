#
# Title: iwlist_header.py
# Description: append a json header to iwlist scan output
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
import gps_wrapper
import json
import sys
import time
import uuid
import yaml

from yaml.loader import SafeLoader


class Converter(object):
    def __init__(self, fresh_dir: str, gps_flag: bool, host: str, site: str):
        self.fresh_dir = fresh_dir
        self.gps_flag = gps_flag
        self.host = host
        self.site = site

    def get_filename(self) -> str:
        return "%s/%s" % (self.fresh_dir, str(uuid.uuid4()))

    def get_geoloc(self, gps_sample: gps_wrapper.GpsSample) -> dict[str, any]:
        results = {}

        if gps_sample is not None:
            results = gps_sample.elements

        results["site"] = self.site
        return results

    def get_preamble(self, gps_sample: gps_wrapper.GpsSample) -> dict[str, any]:
        preamble = {}
        preamble["wifi"] = []
        preamble["project"] = "heeler"
        preamble["version"] = 1
        preamble["platform"] = self.host
        preamble["zTimeMs"] = round(time.time() * 1000)

        preamble["geoLoc"] = self.get_geoloc(gps_sample)

        return preamble

    def execute(self, file_name: str):
        gps_sample = None
        if self.gps_flag:
            wrapper = gps_wrapper.GpsWrapper()
            gps_sample = wrapper.run_test()
            if gps_sample is None:
                return

        try:
            with open(file_name, "r") as infile:
                buffer = infile.readlines()
                if len(buffer) < 3:
                    print("empty scan file noted")
                    return
        except Exception as error:
            print(error)

        json_preamble = json.dumps(self.get_preamble(gps_sample))

        file_name = self.get_filename()
        print(f"filename: {file_name}")

        try:
            with open(file_name, "w") as outfile:
                outfile.write(json_preamble + "\n")
                outfile.write("RAWBUFFER\n")
                outfile.writelines(buffer)
        except Exception as error:
            print(error)


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

    fresh_dir = configuration["freshDir"]
    gps_flag = configuration["gpsEnable"]
    host = configuration["host"]
    site = configuration["site"]

    converter = Converter(fresh_dir, gps_flag, host, site)
    converter.execute("/tmp/iwlist.scan")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
