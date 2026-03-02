import logging
import os
import time

from converter import Converter

logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(message)s")
logger = logging.getLogger("heeler")


class HeelerApp:
    def __init__(self):
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
        # os.rename(file_name, self.success_dir + "/" + file_name)

    def write_statistics(self, file_name: str) -> None:
        pass

    def file_processor(self, file_name: str) -> None:
        if os.path.isfile(file_name) is False:
            logger.warning(f"skipping non-file:{file_name}")
            return

        converter = Converter()
        if converter.converter(file_name):
            self.file_success(file_name)
        else:
            self.file_failure(file_name)

    def execute(self) -> None:
        logger.info("heeler execute")

        logger.info(f"fresh dir:{self.fresh_dir}")
        os.chdir(self.fresh_dir)
        targets = os.listdir(".")
        logger.info(f"{len(targets)} files noted")

        for target in targets:
            self.file_processor(target)

        logger.info(f"success:{self.success} failure:{self.failure}")

if __name__ == "__main__":
    app = HeelerApp()
    app.execute()
