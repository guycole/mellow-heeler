#
# Title: wombat_post.py
# Description:
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
import logging
import json
import requests
import os
import sys
import time
import uuid
import yaml

from yaml.loader import SafeLoader

from observation import Observation, Parser


class Wombat:
    def __init__(self, url: str):
        self.url = url

    def wombat_post(self, obs_list: list[Observation]) -> None:
        """http post to mellow-wombat"""

        payload = []

        for current in obs_list:
            payload.append(current.args)

        response = requests.post(self.url, json=payload)
        print(response)
        # print(response.text)

    def execute(self, file_name: str) -> None:
        converter = Converter()
        buffer = converter.json_reader(file_name)
        if len(buffer) < 1:
            print("empty preamble file")
            return

        self.wombat_post(buffer["wifi"])


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

    run_flag = configuration["wombatEnable"]
    url = configuration["wombatUrl"]

    file_name = "/home/gsc/Documents/github/mellow-heeler/src/sample2.scan"
    #    file_name = "/tmp/iwlist.scan"

    if run_flag:
        wombat = Wombat(url)
        wombat.execute(file_name)
    else:
        print("wombat post is disabled")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
