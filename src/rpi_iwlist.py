#
# Title: rpi_iwlist.py
# Description: rpi append a json header to iwlist scan output
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
import logging
import json
import os
import sys
import time
import uuid
import yaml

from yaml.loader import SafeLoader


class Converter(object):
    def __init__(self, fresh_dir: str, host: str, site: str):
        self.fresh_dir = fresh_dir
        self.host = host
        self.site = site

    def get_filename(self) -> str:
        return "%s/%s" % (self.fresh_dir, str(uuid.uuid4()))

    def get_preamble(self) -> dict[str, any]:
        geo_loc = {}
        geo_loc["site"] = self.site

        preamble = {}
        preamble["wifi"] = []
        preamble["project"] = "heeler"
        preamble["version"] = 1
        preamble["platform"] = self.host
        preamble["zTimeMs"] = round(time.time() * 1000)

        preamble["geoLoc"] = geo_loc

        return preamble

    def converter(self, buffer: list[str]):
        with open(self.get_filename(), "w") as outfile:
            json_preamble = json.dumps(self.get_preamble())
            outfile.write(json_preamble + "\n")
            outfile.write("RAWBUFFER\n")
            outfile.writelines(buffer)

    def execute(self, file_name: str):
        with open(file_name, "r") as infile:
            try:
                buffer = infile.readlines()
                if len(buffer) < 3:
                    print("empty file noted")
                    return
            except Exception as error:
                print(error)

        json_preamble = json.dumps(self.get_preamble())

        file_name = self.get_filename()
        print(f"filename: {file_name}")

        with open(file_name, "w") as outfile:
            try:
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
        except yaml.YAMLError as exc:
            print(exc)

    fresh_dir = configuration["freshDir"]
    host = configuration["host"]
    site = configuration["site"]

    converter = Converter(fresh_dir, host, site)
    converter.execute("/tmp/iwlist.scan")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
