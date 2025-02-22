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
        buffer = []

        with open(file_name, "r") as infile:
            try:
                buffer = infile.readlines()
                if len(buffer) < 3:
                    print("empty file noted")
                    return
            except Exception as error:
                print(error)

        parser = Parser(buffer)
        obs_list = parser.parser()
        self.wombat_post(obs_list)


print("start wombat post")

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

    url = configuration["wombatUrl"]

    file_name = "/home/gsc/Documents/github/mellow-heeler/src/sample2.scan"
    #    file_name = "/tmp/iwlist.scan"

    wombat = Wombat(url)
    wombat.execute(file_name)

print("stop wombat post")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
