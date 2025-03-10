#
# Title: iwlist_header.py
# Description: add a json header to iwlist scan output
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
from gps_helper import GpsSample, GpsWrapper
from converter import Converter
from observation import Observation, Parser
from preamble import PreambleHelper

import json
import sys
import time
import uuid

import yaml
from yaml.loader import SafeLoader


class Header:
    """make the iwlist observation file"""

    def __init__(self, args: dict[str, any]):
        self.fresh_dir = configuration["freshDir"]
        self.gps_flag = configuration["gpsEnable"]
        self.host = configuration["host"]
        self.site = configuration["site"]

    def get_filename(self) -> str:
        return "%s/%s" % (self.fresh_dir, str(uuid.uuid4()))

    def execute(self, file_name: str) -> None:
        gps_sample = None
        if self.gps_flag:
            # must have a GPS location to run
            wrapper = GpsWrapper()
            gps_sample = wrapper.run_test()
            if gps_sample is None:
                return

        # convert iwlist scan to observations
        converter = Converter()
        observations = converter.converter(file_name)
        print(f"observations: {len(observations)}")

        # create json preamble
        helper = PreambleHelper()
        preamble = helper.create_preamble(self.host, self.site, gps_sample)
        preamble["wifi"] = observations

        # save preamble to file for skunk and wombat
        converter.json_writer("/tmp/preamble.json", preamble)

        # iwlist observation to archive file
        converter.file_writer(self.fresh_dir, json.dumps(preamble))


#
# argv[1] = configuration filename
#
if __name__ == "__main__":
    if len(sys.argv) > 1:
        file_name = sys.argv[1]
    else:
        file_name = "config.yaml"

    with open(file_name, "r") as in_file:
        try:
            configuration = yaml.load(in_file, Loader=SafeLoader)
        except yaml.YAMLError as error:
            print(error)

    header = Header(configuration)
    header.execute("/tmp/iwlist.scan")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
