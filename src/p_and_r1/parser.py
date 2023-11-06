#
# Title: parser1.py
# Description:
# Development Environment: OS X 12.6.9/Python 3.11.5
# Author: G.S. Cole (guycole at gmail dot com)
#
import json
import os
import psycopg2
import sys
import yaml

from heeler import Heeler
from hound import Hound

from typing import List

from yaml.loader import SafeLoader

from sqlalchemy import create_engine, select

from sql_table import GeoLoc

from sqlalchemy.orm import Session
from sqlalchemy import select

class Parser(object):
    db_engine = None
    dry_run = False

    def __init__(self, db_conn:str, dry_run: bool):
        self.db_engine = create_engine(db_conn)
        self.dry_run = dry_run

    def file_classifier(self, buffer: List[str]) -> str:
        file_type = "unknown"

        if len(buffer) == 1:
            # hope for json
            temp = json.loads(buffer[0])

            project = None
            version = None

            if "project" in temp:
                project = temp["project"]
            if "version" in temp:
                version = temp["version"]

            file_type = f"{project}_{version}"
        elif buffer[1].startswith("RAWBUFFER"):
            file_type = "heeler_1"

        return file_type

    def file_reader(self, file_name: str) -> List[str]:
        buffer = []

        with open(file_name, "r") as infile:
            try:
                buffer = infile.readlines()
                if len(buffer) < 1:
                    print(f"empty file noted: {file_name}")
                    return buffer
            except:
                print(f"file read error: {file_name}")
                return buffer

        return buffer
    
    def file_processor(self, file_name: str) -> int:
        status = 0

        buffer = self.file_reader(file_name)

        classifier = self.file_classifier(buffer)

        if classifier == "heeler_1":
            heeler = Heeler(self.dry_run)
            status = heeler.heeler_v1(buffer)
        elif classifier == "hound_1":
            hound = Hound(self.db_engine, self.dry_run)
            status = hound.hound_v1(buffer)
        elif classifier == "unknown":
            status = -1

        return status

    def file_success(self, file_name: str):
        if self.dry_run is True:
            print(f"skip file delete for {file_name}")
        else:
            os.unlink(file_name)

    def file_failure(self, file_name: str, failure_dir: str):
        if self.dry_run is True:
            print(f"skip file move for {file_name}")
        else:
            os.rename(file_name, failure_dir + "/" + file_name)

    def directory_processor(self, import_dir: str, failure_dir: str):
        failure_counter = 0
        success_counter = 0

        os.chdir(import_dir)
        targets = os.listdir(".")
        print(f"{len(targets)} files noted")

        for target in targets:
            status = self.file_processor(target)

            if status == 0:
                success_counter += 1
                self.file_success(target)
            else:
                failure_counter += 1
                self.file_failure(target, failure_dir)

        print(f"success:{success_counter} failure:{failure_counter}")

print("start parser")

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

    parser = Parser(configuration["dbConn"], configuration["dryRun"])
    parser.directory_processor(configuration["importDir"], configuration["failureDir"])

print("stop parser")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
