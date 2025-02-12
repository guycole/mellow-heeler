#
# Title: rpi_iwlist.py
# Description: rpi append a json header to iwlist scan output
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
import logging
import json
import os
import pygpsd
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

    def get_geoloc(self) -> dict[str, any]:
        results = {}
        results["site"] = self.site

        if self.gps_flag:
            print("...must read GPS...")

            gpsd = pygpsd.GPSD()
            datum = gpsd.poll()
            if datum is None:
                print("...gpsd returns none...")
            else:
                errors = {}
                errors["epc"] = datum.geo.errors.epc
                errors["epd"] = datum.geo.errors.epd
                errors["eph"] = datum.geo.errors.eph
                errors["epv"] = datum.geo.errors.epv
                errors["epx"] = datum.geo.errors.epx
                errors["epy"] = datum.geo.errors.epy

                results["mode"] = int(datum.mode)
                results["altitude"] = datum.geo.position.altitude
                results["errors"] = errors
                results["latitude"] = datum.geo.position.latitude
                results["longitude"] = datum.geo.position.longitude
                results["speed"] = datum.geo.trajectory.speed
                results["fixTime"] = int(datum.time.timestamp())
                results["track"] = datum.geo.trajectory.track
        else:
            print("...skipping GPS read...")

        return results

    def get_preamble(self) -> dict[str, any]:
        preamble = {}
        preamble["wifi"] = []
        preamble["project"] = "heeler"
        preamble["version"] = 1
        preamble["platform"] = self.host
        preamble["zTimeMs"] = round(time.time() * 1000)

        preamble["geoLoc"] = self.get_geoloc()

        return preamble

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
