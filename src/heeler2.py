#
# Title: heeler.py
# Description: 
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
import sys

import yaml
from yaml.loader import SafeLoader

import observation

class Heeler:
    """mellow heeler file parser"""

    def __init__(self):
        pass

    def heeler_v1(self, buffer: list[str]) -> list[observation.Observation]:      
        parser = observation.Parser(buffer)
        results = parser.parser()
        return results

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
            sys.exit(-1)

#    export_dir = configuration["exportDir"]
#    site = configuration["site"]
#    host = configuration["host"]

    file_name = "/Users/gsc/Documents/github/mellow-heeler/src/converter_rpi/sample2.scan"
    file_name = "/Users/gsc/Documents/github/mellow-heeler/samples/ffe08060-3068-4424-bd9d-d23507d53c87"
    file_name = "/Users/gsc/Documents/github/mellow-heeler/samples/capability_test"

    with open(file_name, "r") as infile:
        try:
            buffer = infile.readlines()
            if len(buffer) < 2:
                print("empty file noted")
                sys.exit(-1)
        except Exception as error:
            print(error)
            print("file read error")

    heeler = Heeler()
    result = heeler.heeler_v1(buffer)
    print(result)

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***