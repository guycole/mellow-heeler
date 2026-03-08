#
# Title: validator.py
# Description: validate observation and collect basic stats
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
from asyncio.log import logger
import datetime
import json
import os

from postgres import PostGres


class Validator:

    def __init__(self, postgres: PostGres):
        self.postgres = postgres

        # path is from inside docker container
        self.failure_dir = "/mnt/wombat/heeler/failure/"
        self.fresh_dir = "/mnt/wombat/fresh/heeler"
        self.success_dir = "/mnt/wombat/heeler/success/"

        self.failure = 0
        self.success = 0

    def file_failure(self, file_name: str):
        logger.info(f"file failure:{file_name}")

        self.failure += 1
        os.rename(file_name, self.failure_dir + file_name)

    def file_success(self, file_name: str):
        # logger.info(f"file success:{file_name}")

        self.success += 1
        os.rename(file_name, self.success_dir + "/" + file_name)

    def file_reader(self, file_name: str) -> bool:
        self.file_name = file_name
        self.json_preamble = {}

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
            self.json_preamble = json.loads(self.raw_buffer[0])
        except Exception as error:
            return False

        return True

    def load_log(self) -> dict[str, any]:
        if self.json_preamble["version"] == 1:
            epoch = self.json_preamble["zTime"]
            utc_dt = datetime.datetime.fromtimestamp(epoch, tz=datetime.timezone.utc)

            return {
                "file_name": self.file_name,
                "file_time": utc_dt,
                "file_type": f"{self.json_preamble['project']}_{self.json_preamble['version']}",
                "obs_quantity": len(self.json_preamble["wifi"]),
                "platform": self.json_preamble["platform"],
            }
        else:
            logger.error(
                f"invalid version:{self.json_preamble['version']} for file:{self.file_name}"
            )
            return {}

    def file_processor(self, file_name: str) -> None:
        if os.path.isfile(file_name) is False:
            logger.warning(f"skipping non-file:{file_name}")
            return

        if self.converter(file_name):
            db_args = self.load_log()
            if len(db_args) > 0:
                try:
                    candidate = self.postgres.load_log_select_by_file_name(file_name)
                    if candidate is not None:
                        logger.info(f"skippping already processed:{file_name}")
                    else:
                        self.postgres.load_log_insert(db_args)
                    self.file_success(file_name)
                except Exception as error:
                    logger.error(f"postgres insert failed for {file_name}: {error}")
                    self.file_failure(file_name)
            else:
                logger.error(f"invalid db_args for file:{file_name}")
                self.file_failure(file_name)
        else:
            self.file_failure(file_name)

    def validate(self) -> None:
        logger.info("validator")
        logger.info(f"fresh dir:{self.fresh_dir}")

        os.chdir(self.fresh_dir)
        targets = os.listdir(".")
        logger.info(f"{len(targets)} files noted")

        for target in targets:
            self.file_processor(target)

        logger.info(f"success:{self.success} failure:{self.failure}")


# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
