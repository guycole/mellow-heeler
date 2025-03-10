#
# Title: converter.py
# Description: parse an iwlist(8) scan file and return observations
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
from observation import Observation, Parser

import json
import sys
import time
import uuid


class Converter:
    obs_list = []
    preamble = {}
    raw_buffer = []

    def file_name(self, dir_name: str) -> str:
        return "%s/%s" % (dir_name, str(uuid.uuid4()))

    def file_reader(self, file_name: str) -> bool:
        self.preamble = {}

        try:
            with open(file_name, "r", encoding="utf-8") as in_file:
                self.raw_buffer = in_file.readlines()
                if len(self.raw_buffer) < 3:
                    print("empty scan file noted")
                    self.raw_buffer = []
        except Exception as error:
            print(error)
            return False

        return True

    def file_writer(self, dir_name: str, json_preamble: str) -> None:
        file_name = self.file_name(dir_name)
        print(f"output filename: {file_name}")

        try:
            with open(file_name, "w") as out_file:
                out_file.write(json_preamble + "\n")
                out_file.write("RAWBUFFER\n")
                out_file.writelines(self.raw_buffer)
        except Exception as error:
            print(error)

    def json_reader(self, file_name: str) -> dict[str, any]:
        results = {}

        try:
            with open(file_name, "r") as in_file:
                results = json.load(in_file)
        except Exception as error:
            print(error)

        return results

    def json_writer(self, file_name, payload: dict[str, any]) -> None:
        try:
            with open(file_name, "w") as out_file:
                json.dump(payload, out_file, indent=4)
        except Exception as error:
            print(error)

    def converter(self, file_name: str) -> bool:
        if self.file_reader(file_name) is False:
            return False

        try:
            self.preamble = json.loads(self.raw_buffer[0])
        except Exception as error:
            print(error)

        parser = Parser(self.raw_buffer)
        observations = parser.parser()
        if len(observations) < 1:
            print("empty observation list")
            return False

        self.obs_list = []
        for obs in observations:
            self.obs_list.append(obs.to_dict())
            self.preamble['wifi'] = self.obs_list

        return True


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
