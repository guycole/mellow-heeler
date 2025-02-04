#
# Title: parser.py
# Description: mellow heeler file parser and database loader
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

import heeler2
import observation
import postgres

class Parser:
    def classifier(self, preamble: dict[str, str]) -> str:
        """discover file format, i.e. heeler_v1, etc"""

        project = None
        if "project" in preamble:
            project = preamble["project"]

        version = None
        if "version" in preamble:
            version = preamble["version"]

        file_type = f"{project}_{version}"
        return file_type

    def file_reader(self, file_name: str) -> list[str]:
        """read a mellow heeler file into a buffer"""

        buffer = []
        with open(file_name, "r", encoding="utf-8") as infile:
            try:
                buffer = infile.readlines()
                if len(buffer) < 2:
                    print(f"empty file noted: {file_name}")
            except:
                print(f"file read error: {file_name}")

        return buffer

    def geo_location(self, preamble: dict[str, str]) -> str:
        """extract geolocation from the file"""

        if "geoLoc" in preamble:
            temp = preamble["geoLoc"]
            if "site" in temp:
                if temp["site"].startswith("and"):
                    return "anderson1"
                if temp["site"].startswith("val"):
                    return "vallejo1"

        return None

    def obs_time(self, preamble: dict[str, str]) -> datetime.datetime:
        """extract observation time"""

        if "zTimeMs" in preamble:
            seconds = int(preamble["zTimeMs"]) / 1000
            dt = datetime.datetime.fromtimestamp(seconds, pytz.utc)
            return dt

    def platform(self, preamble: dict[str, str]) -> str:
        """extract platform"""

        if "platform" in preamble:
            return preamble["platform"]

        return "unknown"

    def preamble(self, buffer: list[str]) -> dict[str, str]:
        """extract the preamble from the file"""

        preamble = {}

        try:
            preamble = json.loads(buffer[0])
        except Exception as error:
            print(error)

        return preamble

    def execute(self, file_name: str) -> list[observation.Observation]:
        """dispatch to approprate file parser"""

        obs_list = []

        buffer = self.file_reader(file_name)
        if len(buffer) < 1:
            return obs_list

        preamble = self.preamble(buffer)
        if len(preamble) < 1:
            print("preamble not found")
            return obs_list

        geoloc = self.geo_location(preamble)
        if geoloc is None:
            print("geoloc not found")
            return obs_list

        obs_time = self.obs_time(preamble)
        if obs_time is None:
            print("obs_time not found")
            return obs_list

        platform = self.platform(preamble)
        if platform is None:
            print("platform not found")
            return obs_list

        classifier = self.classifier(preamble)
        print(f"file:{file_name} classifier:{classifier}")

        if classifier == "heeler_1":
            heeler = heeler2.Heeler()
            obs_list = heeler.heeler_v1(buffer)
        elif classifier == "hound_1":
            pass
            # hound = Hound(postgres)
            # status = hound.hound_v1(buffer, load_log.id)
        else:
            print(f"unknown classifier:{classifier}")

        for obs in obs_list:
            obs.file_name = file_name
            obs.obs_time = obs_time
            obs.platform = platform
            obs.site = geoloc

        return obs_list


class Driver:
    """mellow heeler file parser and database loader"""

    def __init__(self, configuration: dict[str, str]):
        self.db_conn = configuration["dbConn"]
        self.dry_run = configuration["dryRun"]
        self.archive_dir = configuration["archiveDir"]
        self.failure_dir = configuration["failureDir"]
        self.fresh_dir = configuration["freshDir"]

        db_engine = create_engine(self.db_conn, echo=True)
        self.postgres = postgres.PostGres(sessionmaker(bind=db_engine, expire_on_commit=False))

    def file_success(self, file_name: str):
        """file was successfully processed"""

        if self.dry_run is True:
            print(f"skip archive move for {file_name}")
        else:
            os.rename(file_name, self.archive_dir + "/" + file_name)

    def file_failure(self, file_name: str):
        """problem file, retain for review"""

        if self.dry_run is True:
            print(f"skip failure move for {file_name}")
        else:
            os.rename(file_name, self.failure_dir + "/" + file_name)

    def execute(self):
        print("execute driver")

        os.chdir(self.fresh_dir)
        targets = os.listdir(".")
        print(f"{len(targets)} files noted")

        parser = Parser()

        failure_counter = 0
        success_counter = 0

        for target in targets:
            if os.path.isfile(target) is False:
                continue

            # test for duplicate file
            selected = self.postgres.load_log_select(target)
            if selected is not None:
                print(f"skip duplicate file:{target}")
                continue

            obs_list = parser.execute(target)
            if len(obs_list) > 0:
                if self.dry_run is True:
                    print(f"skip database load for {target}")
                else:
                    for obs in obs_list:
                        inserted = self.postgres.observation_insert(obs)
                
                success_counter += 1
                self.file_success(target)
            else:
                failure_counter += 1
                self.file_failure(target)

        print(f"success:{success_counter} failure:{failure_counter}")

print("start parser")

#
# argv[1] = configuration filename
#
if __name__ == "__main__":
    if len(sys.argv) > 1:
        config_name = sys.argv[1]
    else:
        config_name = "config.yaml"

    with open(config_name, "r", encoding="utf-8") as stream:
        try:
            configuration = yaml.load(stream, Loader=SafeLoader)
        except yaml.YAMLError as error:
            print(error)

    driver = Driver(configuration)
    driver.execute()

print("stop parser")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
