#
# Title: converter.py
# Description: parse an iwlist(8) scan file and return observations
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
import json

class Converter:
    preamble = {}
    raw_buffer = []

    def file_reader(self, file_name: str) -> bool:
        self.preamble = {}

        try:
            with open(file_name, "r", encoding="utf-8") as in_file:
                self.raw_buffer = in_file.readlines()
                if len(self.raw_buffer) < 3:
                    self.raw_buffer = []
                    return False
        except Exception as error:
            return False

        return True

    def converter(self, file_name: str) -> bool:
        if self.file_reader(file_name) is False:
            return False

        try:
            self.preamble = json.loads(self.raw_buffer[0])
        except Exception as error:
            return False

        return True

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
