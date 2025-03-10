#
# Title: loader.py
# Description: parse mellow heeler files and load to postgresql
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
import datetime
import json
import os
import pytz
import sys

import yaml
from yaml.loader import SafeLoader

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

from observation import Observation
from preamble import PreambleHelper

import heeler
import postgres

from converter import Converter


class Eclectic:

    def __init__(
        self, file_name: str, preamble: dict[str, any], postgres: postgres.PostGres
    ):
        self.file_name = file_name
        self.postgres = postgres
        self.preamble = preamble

    def dispatch(self, obs_list: list[Observation]) -> bool:
        """dispatch to approprate file parser"""

        preamble_helper = PreambleHelper()
        file_type = preamble_helper.classifier(self.preamble)
        print(f"file:{self.file_name} type:{file_type}")
        self.preamble["file_name"] = self.file_name
        self.preamble["file_type"] = file_type

        if file_type == "heeler_1":
            heeler1 = heeler.Heeler1(self.preamble, self.postgres)
            return heeler1.execute(obs_list)
        else:
            print(f"unknown file type:{file_type}")
            return False

        return True


class Loader:
    """mellow heeler file parser and database loader"""

    def __init__(self, configuration: dict[str, str]):
        self.db_conn = configuration["dbConn"]
        self.dry_run = configuration["dryRun"]
        self.archive_dir = configuration["archiveDir"]
        self.failure_dir = configuration["failureDir"]
        self.fresh_dir = configuration["freshDir"]
        self.sql_echo = configuration["sqlEchoEnable"]

        connect_dict = {"options": "-csearch_path={}".format("heeler_v1")}
        db_engine = create_engine(
            self.db_conn, echo=self.sql_echo, connect_args=connect_dict
        )

        self.postgres = postgres.PostGres(
            sessionmaker(bind=db_engine, expire_on_commit=False)
        )

        self.failure_counter = 0
        self.success_counter = 0

    def file_success(self, file_name: str):
        """file was successfully processed"""

        self.success_counter += 1

        if self.dry_run is True:
            print(f"skip archive move for {file_name}")
        else:
            os.rename(file_name, self.archive_dir + "/" + file_name)

    def file_failure(self, file_name: str):
        """problem file, retain for review"""

        self.failure_counter += 1

        if self.dry_run is True:
            print(f"skip failure move for {file_name}")
        else:
            os.rename(file_name, self.failure_dir + "/" + file_name)

    def execute(self) -> None:
        print(f"fresh dir:{self.fresh_dir}")
        os.chdir(self.fresh_dir)
        targets = os.listdir(".")
        print(f"{len(targets)} files noted")

        converter = Converter()
        preamble_helper = PreambleHelper()

        for target in targets:
            if os.path.isfile(target) is False:
                continue

            # test for duplicate file
            selected = self.postgres.load_log_select_by_file_name(target)
            if len(selected) > 0:
                print(f"skip duplicate file:{target}")
                self.file_failure(target)
                continue

            # test for parsed observations
            if converter.converter(target) is False:
                print(f"converter failure noted:{target}")
                self.file_failure(target)
                continue

            # test for valid json preamble
            valid_preamble = preamble_helper.validate_preamble(converter.preamble)
            if valid_preamble is None:
                self.file_failure(target)
                continue

            # successful iwlist(8) scan file parse, now load into postgres

            file_type = preamble_helper.classifier(valid_preamble)
            print(f"file:{target} type:{file_type}")
            valid_preamble["file_name"] = target
            valid_preamble["file_type"] = file_type

            result_flag = False
            if file_type == "heeler_1":
                if self.dry_run is True:
                    print(f"skip heeler1 for {target}")
                    result_flag = True
                else:
                    heelerx = heeler.Heeler1(self.postgres, valid_preamble)
                    result_flag = heelerx.execute(converter.obs_list)
            else:
                print(f"unknown file type:{file_type}")

            if result_flag is True:
                self.file_success(target)
            else:
                self.file_failure(target)

        print(f"success:{self.success_counter} failure:{self.failure_counter}")


print("start loader")

#
# argv[1] = configuration filename
#
if __name__ == "__main__":
    if len(sys.argv) > 1:
        config_name = sys.argv[1]
    else:
        config_name = "config.yaml"

    with open(config_name, "r", encoding="utf-8") as in_file:
        try:
            configuration = yaml.load(in_file, Loader=SafeLoader)
        except yaml.YAMLError as error:
            print(error)

    loader = Loader(configuration)
    loader.execute()

print("stop loader")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
