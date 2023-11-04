#! /usr/bin/python3
#
# Title: loader1.py
# Description:
# Development Environment: OS X 12.6.9/Python 3.11.5
# Author: G.S. Cole (guycole at gmail dot com)
#
import json
import os
import sys
import time
import typing
import yaml

from yaml.loader import SafeLoader

from heeler import Heeler
from hound import Hound

# from sqlalchemy import create_engine
# from sqlalchemy.orm import sessionmaker


class Eclectic:
    def execute(self, file_name: str):
        with open(file_name, "r") as infile:
            try:
                raw_load = json.load(infile)
            except:
                print("parse error")

        project = None
        if "project" in raw_load:
            project = raw_load["project"]

        version = None
        if "version" in raw_load:
            version = raw_load["version"]

        if project == "heeler":
            heeler = Heeler()
            heeler.execute(file_name, version, raw_load)
        elif project == "hound":
            hound = Hound()
            hound.execute(file_name, version, raw_load)
        else:
            print("skipping unknown:", project, ":", version)


print("start loader")

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

    eclectic = Eclectic()

    os.chdir(import_dir)
    targets = os.listdir(".")
    print(len(targets), "files noted")
    for target in targets:
        eclectic.execute(target)

#    db_data_base = configuration['dbDataBase']
#    db_host_name = configuration['dbHostName']
#    db_pass_word = configuration['dbPassWord']
#    db_user_name = configuration['dbUserName']

print("stop loader")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
