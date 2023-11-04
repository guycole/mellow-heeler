import json
import os
import sys
import yaml

from hound import Hound

from typing import List

from yaml.loader import SafeLoader

# import psycopg2
# from sqlalchemy import create_engine, select
#
# from sql_table import GeoLoc
#
# from sqlalchemy.orm import Session

# Try uninstalling psycopg2-binary, and installing it afresh, with the --no-cache-dir option,

# if __name__ == '__main__':
# 	print("main noted")
#
# 	engine = create_engine('postgresql+psycopg2://heeler_py:bogus@localhost:5432/heeler_v1')
#
# 	statement = select(GeoLoc).where(GeoLoc.fix_time_ms > 0)
# 	with Session(engine) as session:
# 		for row in session.execute(statement):
# 			print(row)
#


class Parser(object):
    dry_run = False

    def __init__(self, dry_run: bool):
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

        if classifier == "hound_1":
            hound = Hound(self.dry_run)
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

    parser = Parser(configuration["dryRun"])
    parser.directory_processor(configuration["importDir"], configuration["failureDir"])

print("stop parser")

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
