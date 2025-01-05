#
# Title: driver.py
# Description: rpi collection
# Development Environment: OS X 12.6.9/Python 3.11.5
# Author: G.S. Cole (guycole at gmail dot com)
#
import logging
import json
import os
import sys
import time
import typing
import uuid
import yaml

from yaml.loader import SafeLoader

class Converter(object):
    def __init__(self, export_dir, site, host):
        self.export_dir = export_dir
        self.host = host
        self.site = site

    def get_filename(self) -> str:
        return "%s/%s" % (self.export_dir, str(uuid.uuid4()))

    def get_preamble(self) -> typing.Dict:
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
    
    def converter(self, buffer:typing.List[str]):
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

print("start collection")

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

    export_dir = configuration["exportDir"]
    site = configuration["site"]
    host = configuration["host"]

    converter = Converter(export_dir, site, host)
    converter.execute("/tmp/iwlist.scan")

print("stop collection")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
