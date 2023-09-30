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
import yaml

from yaml.loader import SafeLoader

class Collection:
    def sample_parser(self, start, stop: int, buffer: typing.List[str]):
        print(start, buffer[start])
        print(stop, buffer[stop])

        results = {}

        for ndx in range(start, stop):
            temp = buffer[ndx].strip()
            if len(temp) < 1:
                continue

            if temp.startswith('Cell'):
                # Cell 20 - Address: E6:63:D9:48:90:D3
                temp = temp.split('Address:')
                print(temp[1])
                results['bssid'] = temp[1].strip()
            elif temp.startswith('Frequency'):
                # Frequency:2.412 GHz (Channel 1)
                ndx1 = temp.index(':')
                ndx2 = temp.index(' ')
                results['frequency'] = temp[ndx1+1:ndx2].replace('.', '')

        print(results)

    def execute(self, file_name: str):
        with open(file_name, "r") as infile:
            try:
                buffer = infile.readlines()
                if len(buffer) < 2:
                    print("empty file noted")
                    return
            except:
                print("file read error")

        ndx = 0
        start = 0
        for current in buffer:
            if current.startswith("          Cell"):
                print(current)
                if start == 0:
                    start = ndx
                else:
                    self.sample_parser(start, ndx, buffer)
                    start = ndx

            ndx += 1

        self.sample_parser(start, len(buffer)-1, buffer)

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

    import_dir = configuration["importDir"]

    collection = Collection()
    collection.execute("sample.scan")

#    os.chdir(import_dir)
#    targets = os.listdir(".")
#    print(len(targets), "files noted")
#    for target in targets:
#        collection.execute(target)

#    db_data_base = configuration['dbDataBase']
#    db_host_name = configuration['dbHostName']
#    db_pass_word = configuration['dbPassWord']
#    db_user_name = configuration['dbUserName']

print("stop collection")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***


{"wiFi":[{"key":"value"}],"geoLoc":{"site":"vallejo", "latitude":12.34, "longitude":123.45}, "project":"heeler","version":1, "platform":"rpi", "zTimeMs": 12345}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
 "}],"geoLoc":{"fixTimeMs":1629166763219,"provider":"fused","longitude":-122.2835223,"latitude":40.3702153,"accuracy":87.6,"altitude":0},"project":"hound","version":1}