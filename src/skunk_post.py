#
# Title: skunk_post.py
# Description: parse iwlist and post to skunk
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
import requests
import sys
import yaml

from yaml.loader import SafeLoader

from iwlist_converter import Converter
from observation import Observation, Parser


class Skunk:
    def __init__(self, url: str):
        self.url = url

    def skunk_post(self, obs_list: list[Observation]) -> None:
        """http post to mellow-skunk"""

        response = requests.post(self.url, json=obs_list)
        print(response)
        # print(response.text)

    def execute(self, file_name: str) -> None:
        converter = Converter()
        buffer = converter.json_reader(file_name)
        if len(buffer) < 1:
            print("empty preamble file")
            return

        self.skunk_post(buffer["wifi"])


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

    run_flag = configuration["skunkEnable"]
    url = configuration["skunkUrl"]

    # file_name = "/home/gsc/Documents/github/mellow-heeler/src/sample2.scan"
    file_name = "/tmp/preamble.json"

    if run_flag:
        skunk = Skunk(url)
        skunk.execute(file_name)
    else:
        print("skunk post is disabled")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
