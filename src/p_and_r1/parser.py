"""mellow heeler file parser and database loader"""

import json
import os
import sys

from typing import List

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

import yaml
from yaml.loader import SafeLoader

from heeler import Heeler
from hound import Hound
from postgres import PostGres


class Parser:
    """mellow heeler file parser and database loader"""

    db_conn = None
    dry_run = False

    def __init__(self, db_conn: str, dry_run: bool):
        self.db_conn = db_conn
        self.dry_run = dry_run

    def file_classifier(self, buffer: List[str]) -> str:
        """discover file format, i.e. heeler_v1, hound_v1, etc"""

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
        """read a mellow heeler file into a buffer"""

        buffer = []

        with open(file_name, "r", encoding="utf-8") as infile:
            try:
                buffer = infile.readlines()
                if len(buffer) < 1:
                    print(f"empty file noted: {file_name}")
            except:
                print(f"file read error: {file_name}")

        return buffer

    def file_processor(self, file_name: str, postgres: PostGres) -> int:
        """dispatch to approprate file parser/loader"""

        status = 0

        buffer = self.file_reader(file_name)

        classifier = self.file_classifier(buffer)
        print(f"file:{file_name} classifier:{classifier}")

        load_log = postgres.load_log_select(file_name)
        if load_log is not None:
            print("skipping duplicate file")
            return 0

        load_log = postgres.load_log_insert(file_name, classifier)

        if classifier == "heeler_1":
            heeler = Heeler(postgres)
            status = heeler.heeler_v1(buffer, load_log.id)
        elif classifier == "hound_1":
            hound = Hound(postgres)
            status = hound.hound_v1(buffer, load_log.id)
        elif classifier == "unknown":
            status = -1

        return status

    def file_success(self, file_name: str):
        """file was successfully processed"""

        if self.dry_run is True:
            print(f"skip file delete for {file_name}")
        else:
            os.unlink(file_name)

    def file_failure(self, file_name: str, failure_dir: str):
        """problem file, retain for review"""

        if self.dry_run is True:
            print(f"skip file move for {file_name}")
        else:
            os.rename(file_name, failure_dir + "/" + file_name)

    def directory_processor(self, import_dir: str, failure_dir: str):
        """process all files in directory"""

        db_engine = create_engine(self.db_conn, echo=False)
        postgres = PostGres(sessionmaker(bind=db_engine, expire_on_commit=False), False)
        # postgres = PostGres(sessionmaker(bind=db_engine, expire_on_commit=False), self.dry_run)

        failure_counter = 0
        success_counter = 0

        os.chdir(import_dir)
        targets = os.listdir(".")
        print(f"{len(targets)} files noted")

        for target in targets:
            if os.path.isfile(target) is False:
                continue

            status = self.file_processor(target, postgres)

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
        config_name = sys.argv[1]
    else:
        config_name = "config.yaml"

    with open(config_name, "r", encoding="utf-8") as stream:
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
